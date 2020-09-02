package enginecrafter77.survivalinc.stats.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
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
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatRegisterEvent;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.effect.ConstantStatEffect;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffect;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffectFilter;
import enginecrafter77.survivalinc.stats.effect.SideEffectFilter;

public class SanityModifier {
	
	public static final ResourceLocation distortshader = new ResourceLocation(SurvivalInc.MOD_ID, "shaders/distort.json");
	public static final SoundEvent staticbuzz = new SoundEvent(new ResourceLocation(SurvivalInc.MOD_ID, "staticbuzz"));
	
	public static final Map<Item, Float> foodSanityMap = new HashMap<Item, Float>();
	
	public static void init()
	{
		if(ModConfig.WETNESS.enabled) DefaultStats.SANITY.effects.addEffect(new FunctionalEffect(SanityModifier::whenWet));
		
		DefaultStats.SANITY.effects.addEffect(new ConstantStatEffect(ConstantStatEffect.Operation.OFFSET, 0.004F), FunctionalEffectFilter.byPlayer(EntityPlayer::isPlayerSleeping));
		DefaultStats.SANITY.effects.addEffect(new FunctionalEffect(SanityModifier::whenNearEntities));
		DefaultStats.SANITY.effects.addEffect(new FunctionalEffect(SanityModifier::duringNight), HydrationModifier.isOutsideOverworld.invert());
		DefaultStats.SANITY.effects.addEffect(new FunctionalEffect(SanityModifier::whenInDark), HydrationModifier.isOutsideOverworld.invert());
		DefaultStats.SANITY.effects.addEffect(new FunctionalEffect(SanityModifier::playStaticNoise), new SideEffectFilter(Side.CLIENT));
		
		// Compile food value list
		for(String entry : ModConfig.SANITY.foodSanityMap)
		{
			int separator = entry.lastIndexOf(' ');
			Item target = Item.getByNameOrId(entry.substring(0, separator));
			Float value = Float.parseFloat(entry.substring(separator + 1));
			SanityModifier.foodSanityMap.put(target, value);
		}
	}
	
	@SubscribeEvent
	public static void registerStat(StatRegisterEvent event)
	{
		event.register(DefaultStats.SANITY);
	}
	
	public static void playStaticNoise(EntityPlayer player, float current)
	{
		float threshold = (float)ModConfig.SANITY.hallucinationThreshold * DefaultStats.SANITY.max;
		if(player.world.getWorldTime() % 160 == 0)
		{
			/**
			 * This effect should only apply to the client player.
			 * Other player entities on client-side world should
			 * not be evaluated.
			 */
			Minecraft client = Minecraft.getMinecraft();
			if(player != client.player) return;
			
			if(player.world.rand.nextFloat() < 0.25F && current < threshold)
			{
				// 1F - current / threshold => this calculation is used to increase the volume for "more insane" players, up to 100% original volume (applied at sanity 0)
				float volume = (1F - current / threshold) * (float)ModConfig.SANITY.staticBuzzIntensity;
				player.world.playSound(player.posX, player.posY, player.posZ, staticbuzz, SoundCategory.AMBIENT, volume, 1, false);
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
	}
	
	public static float duringNight(EntityPlayer player, float value)
	{
		boolean night;
		if(player.world.isRemote)
		{
			float angle = player.world.getCelestialAngle(1F);
			night = angle < 0.75F && angle > 0.25F;
		}
		else night = !player.world.isDaytime();
		
		if(night) value -= (float)ModConfig.SANITY.nighttimeDrain;
		return value;
	}
	
	public static float whenInDark(EntityPlayer player, float value)
	{
		int lightlevel = player.world.getLight(new BlockPos(player));
		
		// If there is not enough light, steve/alex feels anxious
		if(lightlevel < ModConfig.SANITY.comfortLightLevel)
		{
			float darknesslevel = (float)(ModConfig.SANITY.comfortLightLevel - lightlevel) / (float)ModConfig.SANITY.comfortLightLevel;
			value += (float)ModConfig.SANITY.darkSpookFactorBase * -darknesslevel;
		}
		return value;
	}
	
	public static float whenWet(EntityPlayer player, float value)
	{
		float boundary = (float)ModConfig.SANITY.wetnessAnnoyanceThreshold;
		StatTracker stats = player.getCapability(StatCapability.target, null);
		SimpleStatRecord wetness = (SimpleStatRecord)stats.getRecord(DefaultStats.WETNESS);
		float annoyance = (wetness.getValue() - wetness.valuerange.upperEndpoint()) / 10000;
		annoyance = (boundary / -10000) - annoyance;
		
		if(wetness.getValue() >= boundary) value -= annoyance;
		return value;
	}
	
	public static float whenNearEntities(EntityPlayer player, float value)
	{
		BlockPos origin = player.getPosition();
		Vec3i offset = new Vec3i(3, 3, 3);
		
		AxisAlignedBB box = new AxisAlignedBB(origin.subtract(offset), origin.add(offset));
		List<EntityCreature> entities = player.world.getEntitiesWithinAABB(EntityCreature.class, box);
		
		for(EntityCreature creature : entities)
		{
			if(creature instanceof EntityTameable)
			{
				EntityTameable pet = (EntityTameable)creature;
				// 4x bonus for tamed creatures. Having pets has it's perks :D
				float bonus = pet.isTamed() ? (float)ModConfig.SANITY.tamedMobMultiplier : 1;
				value += ModConfig.SANITY.friendlyMobBonus * bonus;
			}
			else if(creature instanceof EntityAnimal)
				value += ModConfig.SANITY.friendlyMobBonus;
			else if(creature instanceof EntityMob)
				value -= ModConfig.SANITY.hostileMobModifier;
		}
		return value;
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onClientWorldTick(TickEvent.ClientTickEvent event)
	{
		Minecraft client = Minecraft.getMinecraft();
		ShaderGroup shader = client.entityRenderer.getShaderGroup();
		if(shader != null && client.world.getWorldTime() % 160 == 0 && (client.player.isCreative() || client.player.isSpectator()) && shader.getShaderGroupName().equals(distortshader.toString()))
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
			SimpleStatRecord sanity = (SimpleStatRecord)stats.getRecord(DefaultStats.SANITY);
			sanity.addToValue(sanity.valuerange.upperEndpoint() * (float)ModConfig.SANITY.sleepResoration);
			SurvivalInc.proxy.net.sendToAll(new StatSyncMessage(player));
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
				SimpleStatRecord sanity = (SimpleStatRecord)stats.getRecord(DefaultStats.SANITY);
				sanity.addToValue(mod);
			}
			catch(NullPointerException exc)
			{
				// Food simply doesn't have any sanity mapping associated
			}
		}
	}
	
	@SubscribeEvent
	public static void onTame(AnimalTameEvent event)
	{
		Entity ent = event.getEntity();
		if(ent instanceof EntityPlayer)
		{
			StatTracker stat = ent.getCapability(StatCapability.target, null);
			SimpleStatRecord sanity = (SimpleStatRecord)stat.getRecord(DefaultStats.SANITY);
			sanity.addToValue((float)ModConfig.SANITY.animalTameBoost);
		}
	}
}