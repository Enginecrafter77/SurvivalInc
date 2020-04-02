package schoperation.schopcraft.cap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import schoperation.schopcraft.CommonProxy;
import schoperation.schopcraft.cap.ghost.GhostMain;
import schoperation.schopcraft.cap.ghost.GhostProvider;
import schoperation.schopcraft.cap.ghost.IGhost;
import schoperation.schopcraft.cap.sanity.ISanity;
import schoperation.schopcraft.cap.sanity.SanityModifier;
import schoperation.schopcraft.cap.sanity.SanityProvider;
import schoperation.schopcraft.cap.temperature.ITemperature;
import schoperation.schopcraft.cap.temperature.TemperatureModifier;
import schoperation.schopcraft.cap.temperature.TemperatureProvider;
import schoperation.schopcraft.cap.thirst.IThirst;
import schoperation.schopcraft.cap.thirst.ThirstModifier;
import schoperation.schopcraft.cap.thirst.ThirstProvider;
import schoperation.schopcraft.cap.wetness.IWetness;
import schoperation.schopcraft.cap.wetness.WetnessModifier;
import schoperation.schopcraft.cap.wetness.WetnessProvider;
import schoperation.schopcraft.config.SchopConfig;
import schoperation.schopcraft.packet.HUDRenderPacket;
import java.util.List;

/*
 * This is the event handler regarding capabilities and changes to individual stats.
 * Most of the actual code is stored in the modifier classes of each stat, and fired here.
 */
@Mod.EventBusSubscriber
public class CapEvents {

	// Modifiers
	private final TemperatureModifier tempMod = new TemperatureModifier();
	private final ThirstModifier thirstMod = new ThirstModifier();
	private final SanityModifier sanityMod = new SanityModifier();
	private final WetnessModifier wetnessMod = new WetnessModifier();
	private final GhostMain ghostMain = new GhostMain();

	// When a player logs on, give them their stats stored on the server.
	@SubscribeEvent
	public void onPlayerLogsIn(PlayerLoggedInEvent event)
	{

		EntityPlayer player = event.player;

		if (player instanceof EntityPlayerMP)
		{

			// Capabilities
			IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
			IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
			ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
			ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
			IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);

			// Send data to client for rendering.
			IMessage msgGui = new HUDRenderPacket.HUDRenderMessage(temperature.getTemperature(),
					temperature.getMaxTemperature(), thirst.getThirst(), thirst.getMaxThirst(), sanity.getSanity(),
					sanity.getMaxSanity(), wetness.getWetness(), wetness.getMaxWetness(), ghost.status(),
					ghost.getEnergy());
			CommonProxy.net.sendTo(msgGui, (EntityPlayerMP) player);
		}
	}

	// When an entity is updated. So, all the time.
	// This also deals with packets to the client.
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event)
	{

		// Only continue if it's a player.
		if (event.getEntity() instanceof EntityPlayer)
		{

			// Instance of player.
			EntityPlayer player = (EntityPlayer) event.getEntity();

			// Server-side
			if (!player.world.isRemote)
			{

				// Whether the player's stats should be changed. Not in creative
				// and spectator mode.
				boolean shouldStatsChange = true;

				if (player.isCreative() || player.isSpectator())
				{

					shouldStatsChange = false;
				}

				// Now fire every method that should be fired here, passing the
				// player as a parameter.
				// If in creative mode (or if the mechanic is disabled in the
				// config), don't fire these at all.
				if (shouldStatsChange)
				{

					if (SchopConfig.MECHANICS.enableTemperature)
					{

						tempMod.onPlayerUpdate(player);
					}

					if (SchopConfig.MECHANICS.enableThirst)
					{

						thirstMod.onPlayerUpdate(player);
					}

					if (SchopConfig.MECHANICS.enableSanity)
					{

						sanityMod.onPlayerUpdate(player);

						// Fire this if the player is sleeping
						if (player.isPlayerSleeping())
						{

							sanityMod.onPlayerSleepInBed(player);
						}
					}

					if (SchopConfig.MECHANICS.enableWetness)
					{

						wetnessMod.onPlayerUpdate(player);
					}

					if (SchopConfig.MECHANICS.enableGhost)
					{

						ghostMain.onPlayerUpdate(player);
					}
				}

				// Send capability data to clients for rendering
				IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
				IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
				ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
				ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
				IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);

				IMessage msgGui = new HUDRenderPacket.HUDRenderMessage(temperature.getTemperature(),
						temperature.getMaxTemperature(), thirst.getThirst(), thirst.getMaxThirst(), sanity.getSanity(),
						sanity.getMaxSanity(), wetness.getWetness(), wetness.getMaxWetness(), ghost.status(),
						ghost.getEnergy());
				CommonProxy.net.sendTo(msgGui, (EntityPlayerMP) player);
			}
		}
	}

	// When a player interacts with a block (usually right clicking something).
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{

		// Instance of player.
		EntityPlayer player = event.getEntityPlayer();

		// Server-side
		if (!player.world.isRemote)
		{

			// Cancel interacting with blocks if the player is a ghost. This
			// must be done here.
			IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);

			if(ghost.status() && event.isCancelable())
			{

				event.setCanceled(true);
			}

			// Fire methods.
			if (SchopConfig.MECHANICS.enableThirst && !player.isCreative() && !player.isSpectator())
			{

				thirstMod.onPlayerInteract(player);
			}
		}
	}

	// When a player (kind of) finishes using an item. Technically one tick
	// before it's actually consumed.
	@SubscribeEvent
	public void onPlayerUseItem(LivingEntityUseItemEvent.Tick event)
	{

		if (event.getEntity() instanceof EntityPlayer)
		{

			// Instance of player.
			EntityPlayer player = (EntityPlayer) event.getEntity();

			// Server-side
			if (!player.world.isRemote && event.getDuration() == 1)
			{

				// Instance of item.
				ItemStack itemUsed = event.getItem();

				// Fire methods.
				if (!player.isCreative())
				{

					if (SchopConfig.MECHANICS.enableTemperature)
					{

						tempMod.onPlayerConsumeItem(player, itemUsed);
					}

					if (SchopConfig.MECHANICS.enableSanity)
					{

						sanityMod.onPlayerConsumeItem(player, itemUsed);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerWakeUp(PlayerWakeUpEvent event)
	{

		// Instance of player.
		EntityPlayer player = event.getEntityPlayer();

		if (!player.world.isRemote)
		{

			// Fire methods
			sanityMod.onPlayerWakeUp(player);
		}
	}
	
	@SubscribeEvent
	public void onDropsDropped(LivingDropsEvent event)
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
			if (SchopConfig.MECHANICS.enableSanity)
			{

				sanityMod.onDropsDropped(entityKilled, drops, lootingLevel, damageSource);
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		// Instance of player.
		EntityPlayer player = event.player;

		/* 
		 * Check if we are operating on server side.
		 * Also, going through the end portal back to
		 * the overworld counts as respawning. This
		 * shouldn't make you a ghost.
		 */
		if(!(player.world.isRemote || event.isEndConquered()))
		{
			ghostMain.onPlayerRespawn(player);
			
			// This should be handled by vanilla codes. No need to get our hands dirty.
			/*else
			{
				// Set no gravity.
				player.setNoGravity(true);

				// Move player away from portal.
				player.setLocationAndAngles(player.posX + 3, player.posY + 1, player.posZ + 3, 0.0f, 0.0f);

				// Set gravity back.
				player.setNoGravity(false);
			}*/
		}
	}
	
	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event)
	{

		// Cancel picking up the items if the player is a ghost. This has to be
		// done here.
		EntityPlayer player = event.getEntityPlayer();
		IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);

		if(ghost.status())
		{

			event.setCanceled(true);
		}
	}
}