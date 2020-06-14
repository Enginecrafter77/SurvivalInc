package enginecrafter77.survivalinc.stats.impl;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.modifier.ChanceModifier;
import enginecrafter77.survivalinc.stats.modifier.ConditionalModifier;
import enginecrafter77.survivalinc.stats.modifier.FunctionalModifier;
import enginecrafter77.survivalinc.stats.modifier.ModifierApplicator;
import enginecrafter77.survivalinc.stats.modifier.OperationType;

public class SanityModifier {
	
	public static final Predicate<EntityPlayer> isOutsideOverworld = (EntityPlayer player) -> Math.abs(player.dimension) == 1;
	public static final Map<Item, Float> foodSanityMap = new HashMap<Item, Float>();
	
	public static ModifierApplicator<EntityPlayer> hallucinations = new ModifierApplicator<EntityPlayer>();
	public static List<SoundEvent> scarysounds = new ArrayList<SoundEvent>();
	
	public static void init()
	{
		if(ModConfig.WETNESS.enabled) DefaultStats.SANITY.modifiers.add(new FunctionalModifier<EntityPlayer>(SanityModifier::whenWet), OperationType.OFFSET);
		
		DefaultStats.SANITY.modifiers.add(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.isPlayerSleeping(), 0.004F), OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.add(new ConditionalModifier<EntityPlayer>(SanityModifier.isOutsideOverworld, -0.004F), OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.add(new FunctionalModifier<EntityPlayer>(SanityModifier::whenNearEntities), OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.add(new FunctionalModifier<EntityPlayer>(SanityModifier::duringNight), OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.add(new FunctionalModifier<EntityPlayer>(SanityModifier::whenInDark), OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.add(new FunctionalModifier<EntityPlayer>(SanityModifier::onLowSanity));
		
		//TODO remove (will be completely reworked in Sanity Overhaul)
		SanityModifier.hallucinations.singleCatch = true;
		SanityModifier.hallucinations.add(new ChanceModifier<EntityPlayer>(new FunctionalModifier<EntityPlayer>(SanityModifier::playRandomSounds), 0.2F));
		SanityModifier.hallucinations.add(new ChanceModifier<EntityPlayer>(new FunctionalModifier<EntityPlayer>(SanityModifier::spawnGuardianParticle), 0.2F));
		SanityModifier.hallucinations.add(new ChanceModifier<EntityPlayer>(new FunctionalModifier<EntityPlayer>(SanityModifier::applyBlindness), 0.1F));
		
		SanityModifier.scarysounds.add(SoundEvents.ENTITY_VILLAGER_AMBIENT);
		SanityModifier.scarysounds.add(SoundEvents.ENTITY_ENDERMEN_AMBIENT);
		SanityModifier.scarysounds.add(SoundEvents.ENTITY_ENDERMEN_TELEPORT);
		SanityModifier.scarysounds.add(SoundEvents.BLOCK_LAVA_AMBIENT);
		SanityModifier.scarysounds.add(SoundEvents.BLOCK_STONE_STEP);
		
		// Compile food value list
		for(String entry : ModConfig.SANITY.foodSanityMap)
		{
			int separator = entry.lastIndexOf(' ');
			Item target = Item.getByNameOrId(entry.substring(0, separator));
			Float value = Float.parseFloat(entry.substring(separator + 1));
			SanityModifier.foodSanityMap.put(target, value);
		}
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
	
	public static void spawnGuardianParticle(EntityPlayer player)
	{
		WorldClient world = (WorldClient)player.world; //EntityEnderman
		Vec3d eyes = player.getPositionEyes(1F);
		world.spawnParticle(EnumParticleTypes.MOB_APPEARANCE, eyes.x, eyes.y, eyes.z, 0, 0, 0, null);
	}
	
	public static void playRandomSounds(EntityPlayer player)
	{
		WorldClient world = (WorldClient)player.world;
		BlockPos position = player.getPosition();
		SoundEvent sound = SanityModifier.scarysounds.get(world.rand.nextInt(SanityModifier.scarysounds.size()));
		position = position.add(new Vec3i(world.rand.nextInt(10) - 5, world.rand.nextInt(4) - 2, world.rand.nextInt(10) - 5));
		world.playSound(position, sound, SoundCategory.AMBIENT, 0.5F, 1F, false);
	}
	
	public static void applyBlindness(EntityPlayer player)
	{
		player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100));
	}
	
	public static float whenInDark(EntityPlayer player)
	{
		// If we are not in overworld, skip this function
		if(isOutsideOverworld.test(player)) return 0F;
		int lightlevel = player.world.getLight(player.getPosition());
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
	
	public static float onLowSanity(EntityPlayer player, float level)
	{
		if(player.world.isRemote) // Only do so on client side
		{
			if(level < (DefaultStats.SANITY.getMaximum() * ModConfig.SANITY.hallucinationThreshold) && player.ticksExisted % 20 == 0) // Spawn hallucinations if feasible
				SanityModifier.hallucinations.apply(player, level);
		}
		return level;
	}
	
	@SubscribeEvent
	public static void onPlayerWakeUp(PlayerWakeUpEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();		
		if(event.shouldSetSpawn()) // If the "lying in bed" was successful (the player actually fell asleep)
		{
			StatTracker stats = player.getCapability(StatCapability.target, null);
			stats.modifyStat(DefaultStats.SANITY, DefaultStats.SANITY.getMaximum() * (float)ModConfig.SANITY.sleepResoration);
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