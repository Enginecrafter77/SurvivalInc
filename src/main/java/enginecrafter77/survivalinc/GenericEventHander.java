package enginecrafter77.survivalinc;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.ghost.GhostProvider;
import enginecrafter77.survivalinc.item.ItemFeatherFan;
import enginecrafter77.survivalinc.ghost.Ghost;
import enginecrafter77.survivalinc.net.StatUpdateMessage;
import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.impl.DefaultStats;
import enginecrafter77.survivalinc.stats.impl.SanityModifier;
import enginecrafter77.survivalinc.stats.impl.WetnessModifier;
import enginecrafter77.survivalinc.util.SchopServerEffects;

/*
 * This is the event handler regarding capabilities and changes to individual stats.
 * Most of the actual code is stored in the modifier classes of each stat, and fired here.
 */
@Mod.EventBusSubscriber
public class GenericEventHander {
	
	public static void sendUpdate(EntityPlayer player, StatTracker stats, Ghost ghost)
	{
		SurvivalInc.proxy.net.sendTo(new StatUpdateMessage(stats), (EntityPlayerMP) player);
	}
	
	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
	{
		if(!(event.getObject() instanceof EntityPlayer)) return;
		
		event.addCapability(new ResourceLocation(SurvivalInc.MOD_ID, "ghost"), new GhostProvider());
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		event.getRegistry().register(ItemFeatherFan.WHOOSH);
	}

	// When an entity is updated. So, all the time.
	// This also deals with packets to the client.
	@SubscribeEvent
	public static void onPlayerUpdate(LivingUpdateEvent event)
	{
		// Only continue if it's a player.
		if (event.getEntity() instanceof EntityPlayer)
		{
			// Instance of player.
			EntityPlayer player = (EntityPlayer)event.getEntity();
			StatTracker stat = player.getCapability(StatRegister.CAPABILITY, null);
			Ghost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
			
			// Server-side
			if(!player.world.isRemote)
			{
				if(!player.isCreative() && !player.isSpectator())
				{
					stat.update(player);
					ghost.update(player);
					
					if(ModConfig.MECHANICS.enableSanity)
					{
						SanityModifier.applyAdverseEffects(player);
						
						// Fire this if the player is sleeping
						if(player.isPlayerSleeping())
						{
							stat.modifyStat(DefaultStats.SANITY, 0.004f);
							SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "hunger", 20, 4, false, false);
						}
					}

					if(ModConfig.MECHANICS.enableWetness)
					{
						WetnessModifier.onPlayerUpdate(player);
					}
				}
				GenericEventHander.sendUpdate(player, stat, ghost);
			}
		}
	}

	// When a player interacts with a block (usually right clicking something).
	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		if(!ModConfig.MECHANICS.enableThirst || player.isCreative() || player.isSpectator()) return;
		if(!player.world.isRemote)
		{
			if(player.getHeldItem(EnumHand.MAIN_HAND) == ItemStack.EMPTY)
			{
				Vec3d look = player.getPositionEyes(1.0f).add(player.getLookVec().addVector(player.getLookVec().x < 0 ? -0.5 : 0.5, -1, player.getLookVec().z < 0 ? -0.5 : 0.5));
				RayTraceResult raytrace = player.world.rayTraceBlocks(player.getPositionEyes(1.0f), look, true);
				if(raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK)
				{
					BlockPos position = raytrace.getBlockPos();
					if(player.world.getBlockState(position).getMaterial() == Material.WATER)
					{
						StatTracker tracker = player.getCapability(StatRegister.CAPABILITY, null);
						tracker.modifyStat(DefaultStats.HYDRATION, 5F);
					}
				}
			}
		}
	}

	// When a player (kind of) finishes using an item. Technically one tick
	// before it's actually consumed.
	@SubscribeEvent
	public static void onPlayerUseItem(LivingEntityUseItemEvent.Tick event)
	{
		if(event.getEntity() instanceof EntityPlayer)
		{
			// Instance of player.
			EntityPlayer player = (EntityPlayer) event.getEntity();

			// Server-side
			if (!player.world.isRemote && event.getDuration() == 1)
			{
				// Instance of item.
				ItemStack itemUsed = event.getItem();

				// Fire methods.
				if(!player.isCreative())
				{

					if(ModConfig.MECHANICS.enableSanity)
					{
						SanityModifier.onPlayerConsumeItem(player, itemUsed);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerWakeUp(PlayerWakeUpEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();

		if(!player.world.isRemote)
		{
			SanityModifier.onPlayerWakeUp(player);
		}
	}
	
	@SubscribeEvent
	public static void onDropsDropped(LivingDropsEvent event)
	{
		// The entity that was killed.
		Entity entityKilled = event.getEntity();

		// Server-side
		if (!entityKilled.world.isRemote)
		{

			// A list of their drops.
			List<EntityItem> drops = event.getDrops();

			// The looting level of the weapon.
			int lootingLevel = event.getLootingLevel();

			// Damage source.
			DamageSource damageSource = event.getSource();

			// Fire methods.
			if(ModConfig.MECHANICS.enableSanity)
			{
				SanityModifier.onDropsDropped(entityKilled, drops, lootingLevel, damageSource);
			}
		}
	}
}