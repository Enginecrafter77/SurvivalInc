package enginecrafter77.survivalinc.stats.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.modifier.ConditionalModifier;
import enginecrafter77.survivalinc.stats.modifier.FunctionalModifier;
import enginecrafter77.survivalinc.stats.modifier.OperationType;

public class SanityModifier {
	
	public static final ResourceLocation distortshader = new ResourceLocation(SurvivalInc.MOD_ID, "shaders/distort.json");
	public static final SoundEvent staticbuzz = new SoundEvent(new ResourceLocation(SurvivalInc.MOD_ID, "staticbuzz"));
	
	public static final Predicate<EntityPlayer> isOutsideOverworld = (EntityPlayer player) -> Math.abs(player.dimension) == 1;
	public static final Map<Item, Float> foodSanityMap = new HashMap<Item, Float>();
	
	public static void init()
	{
		if(ModConfig.WETNESS.enabled) DefaultStats.SANITY.modifiers.add(new FunctionalModifier<EntityPlayer>(SanityModifier::whenWet), OperationType.OFFSET);
		
		DefaultStats.SANITY.modifiers.add(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.isPlayerSleeping(), 0.004F), OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.add(new ConditionalModifier<EntityPlayer>(SanityModifier.isOutsideOverworld, -0.004F), OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.add(new FunctionalModifier<EntityPlayer>(SanityModifier::whenNearEntities), OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.add(new FunctionalModifier<EntityPlayer>(SanityModifier::duringNight), OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.add(new FunctionalModifier<EntityPlayer>(SanityModifier::whenInDark), OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.add(new FunctionalModifier<EntityPlayer>(SanityModifier::playStaticNoise), OperationType.NOOP);
		
		// Compile food value list
		for(String entry : ModConfig.SANITY.foodSanityMap)
		{
			int separator = entry.lastIndexOf(' ');
			Item target = Item.getByNameOrId(entry.substring(0, separator));
			Float value = Float.parseFloat(entry.substring(separator + 1));
			SanityModifier.foodSanityMap.put(target, value);
		}
	}
	
	public static float playStaticNoise(EntityPlayer player, float current)
	{
		float threshold = (float)ModConfig.SANITY.hallucinationThreshold * DefaultStats.SANITY.getMaximum();
		if(player.world.isRemote && player.world.getWorldTime() % 160 == 0)
		{
			/**
			 * This effect should only apply to the client player.
			 * Other player entities on client-side world should
			 * not be evaluated.
			 */
			Minecraft client = Minecraft.getMinecraft();
			if(player != client.player) return 0F;
			
			if(player.world.rand.nextFloat() < 0.25F && current < threshold)
			{
				// 1F - current / threshold => this calculation is used to increase the volume for "more insane" players, up to 100% original volume (applied at sanity 0)
				player.world.playSound(player.posX, player.posY, player.posZ, staticbuzz, SoundCategory.AMBIENT, 1F - current / threshold, 1, false);
				client.entityRenderer.loadShader(distortshader);
			}
			else
			{
				// Check if the current shader is our shader, and if so, stop using it.
				ShaderGroup shader = client.entityRenderer.getShaderGroup();
				if(shader != null && shader.getShaderGroupName().equals(distortshader.toString()))
				{
					client.entityRenderer.stopUseShader();
				}
			}
		}
		
		return 0F;
	}
	
	public static float duringNight(EntityPlayer player)
	{
		boolean night;
		if(player.world.isRemote)
		{
			float angle = player.world.getCelestialAngle(1F);
			night = angle < 0.75F && angle > 0.25F;
		}
		else night = !player.world.isDaytime();
		
		return night ? -(float)ModConfig.SANITY.nighttimeDrain : 0F;
	}
	
	public static float whenInDark(EntityPlayer player)
	{
		// If we are not in overworld, skip this function
		if(isOutsideOverworld.test(player)) return 0F;
		
		// Determine the position
		BlockPos position = player.getPosition();
		if(player.world.isRemote)
		{
			// Workaround to avoid client reporting light level 0
			Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
			position = new BlockPos(viewer.posX, viewer.getEntityBoundingBox().minY, viewer.posZ);
		}
		
		// Determine the light level at the target position
		int lightlevel = player.world.getChunk(position).getLightSubtracted(position, 0);
		
		// If there is enough light, steve/alex is happy
		if(lightlevel >= ModConfig.SANITY.comfortLightLevel) return 0F;
		float darknesslevel = (float)(ModConfig.SANITY.comfortLightLevel - lightlevel) / (float)ModConfig.SANITY.comfortLightLevel;
		return (float)ModConfig.SANITY.darkSpookFactorBase * -darknesslevel;
	}
	
	public static float whenWet(EntityPlayer player)
	{
		float boundary = (float)ModConfig.SANITY.wetnessAnnoyanceThreshold;
		StatTracker stats = player.getCapability(StatCapability.target, null);
		float wetness = stats.getStat(DefaultStats.WETNESS);
		float annoyance = (wetness - DefaultStats.WETNESS.getMaximum()) / 10000;
		annoyance = (boundary / -10000) - annoyance;
		return wetness < boundary ? 0F : annoyance;
	}
	
	public static float whenNearEntities(EntityPlayer player)
	{
		BlockPos origin = player.getPosition();
		Vec3i offset = new Vec3i(3, 3, 3);
		
		AxisAlignedBB box = new AxisAlignedBB(origin.subtract(offset), origin.add(offset));
		List<EntityCreature> entities = player.world.getEntitiesWithinAABB(EntityCreature.class, box);
		
		float mod = 0;
		for(EntityCreature creature : entities)
		{
			if(creature instanceof EntityTameable)
			{
				EntityTameable pet = (EntityTameable)creature;
				// 4x bonus for tamed creatures. Having pets has it's perks :D
				float bonus = pet.isTamed() ? (float)ModConfig.SANITY.tamedMobMultiplier : 1;
				mod += ModConfig.SANITY.friendlyMobBonus * bonus;
			}
			else if(creature instanceof EntityAnimal)
				mod += ModConfig.SANITY.friendlyMobBonus;
			else if(creature instanceof EntityMob)
				mod -= ModConfig.SANITY.hostileMobModifier;
		}
		return mod;
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onClientWorldTick(TickEvent.ClientTickEvent event)
	{
		Minecraft client = Minecraft.getMinecraft();
		ShaderGroup shader = client.entityRenderer.getShaderGroup();
		if(shader != null && client.world.getWorldTime() % 160 == 0 && !DefaultStats.SANITY.isAcitve(client.player) && shader.getShaderGroupName().equals(distortshader.toString()))
		{
			client.entityRenderer.stopUseShader();
		}
	}
	
	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		event.getRegistry().register(SanityModifier.staticbuzz);
	}
	
	@SubscribeEvent
	public static void onPlayerWakeUp(PlayerWakeUpEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		if(event.shouldSetSpawn()) // If the "lying in bed" was successful (the player actually fell asleep)
		{
			StatTracker stats = player.getCapability(StatCapability.target, null);
			stats.modifyStat(DefaultStats.SANITY, DefaultStats.SANITY.getMaximum() * (float)ModConfig.SANITY.sleepResoration);
			SurvivalInc.proxy.net.sendTo(new StatSyncMessage(stats), (EntityPlayerMP)player);
			player.getFoodStats().setFoodLevel(player.getFoodStats().getFoodLevel() - 8);
		}
	}
	
	@SubscribeEvent
	public static void onConsumeItem(LivingEntityUseItemEvent.Tick event)
	{
		Entity ent = event.getEntity();		
		if(ent instanceof EntityPlayer && event.getDuration() == 1)
		{
			try
			{
				// Try to get the modifier from the map (throws NPE when no such mapping exists)
				float mod = SanityModifier.foodSanityMap.get(event.getItem().getItem());
				
				// Modify the sanity value
				EntityPlayer player = (EntityPlayer)ent;
				StatTracker stats = player.getCapability(StatCapability.target, null);
				stats.modifyStat(DefaultStats.SANITY, mod);
			}
			catch(NullPointerException exc) {} // Food simply doesn't have sanity mapping
		}
	}
	
	@SubscribeEvent
	public static void onTame(AnimalTameEvent event)
	{
		Entity ent = event.getEntity();
		if(ent instanceof EntityPlayer)
		{
			StatTracker stat = ent.getCapability(StatCapability.target, null);
			stat.modifyStat(DefaultStats.SANITY, (float)ModConfig.SANITY.animalTameBoost); // Solid 5 points for taming any animal
		}
	}
}