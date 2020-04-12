package enginecrafter77.survivalinc.stats.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.modifier.ConditionalModifier;
import enginecrafter77.survivalinc.stats.modifier.FunctionalModifier;
import enginecrafter77.survivalinc.stats.modifier.OperationType;

@Mod.EventBusSubscriber
public class SanityModifier {
	
	public static final Predicate<EntityPlayer> isOutsideOverworld = (EntityPlayer player) -> Math.abs(player.dimension) == 1;
	public static final Map<Item, Float> foodSanityMap = new HashMap<Item, Float>();
	
	public static void init()
	{
		DefaultStats.SANITY.modifiers.add(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> !player.world.isDaytime() && !player.isPlayerSleeping(), -0.0015F), OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.add(new ConditionalModifier<EntityPlayer>(SanityModifier.isOutsideOverworld, -0.004F), OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.add(new FunctionalModifier<EntityPlayer>(SanityModifier::whenNearEntities), OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.add(new FunctionalModifier<EntityPlayer>(SanityModifier::whenInDark), OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.add(new FunctionalModifier<EntityPlayer>(SanityModifier::whenWet), OperationType.OFFSET);
		
		SanityModifier.foodSanityMap.put(Items.CHICKEN, -5F);
		SanityModifier.foodSanityMap.put(Items.BEEF, -5F);
		SanityModifier.foodSanityMap.put(Items.RABBIT, -5F);
		SanityModifier.foodSanityMap.put(Items.MUTTON, -5F);
		SanityModifier.foodSanityMap.put(Items.PORKCHOP, -5F);
		SanityModifier.foodSanityMap.put(Items.FISH, -5F);
		SanityModifier.foodSanityMap.put(Items.ROTTEN_FLESH, -10F);
		SanityModifier.foodSanityMap.put(Items.SPIDER_EYE, -15F);
		SanityModifier.foodSanityMap.put(Items.COOKED_CHICKEN, 2F);
		SanityModifier.foodSanityMap.put(Items.COOKED_BEEF, 2F);
		SanityModifier.foodSanityMap.put(Items.COOKED_RABBIT, 2F);
		SanityModifier.foodSanityMap.put(Items.COOKED_MUTTON, 2F);
		SanityModifier.foodSanityMap.put(Items.COOKED_PORKCHOP, 2F);
		SanityModifier.foodSanityMap.put(Items.COOKED_FISH, 2F);
		SanityModifier.foodSanityMap.put(Items.PUMPKIN_PIE, 15F);
		SanityModifier.foodSanityMap.put(Items.COOKIE, 2F);
		SanityModifier.foodSanityMap.put(Items.RABBIT_STEW, 15F);
		SanityModifier.foodSanityMap.put(Items.MUSHROOM_STEW, 10F);
		SanityModifier.foodSanityMap.put(Items.BEETROOT_SOUP, 10F);
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
		StatTracker stats = player.getCapability(StatRegister.CAPABILITY, null);
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
				float bonus = pet.isTamed() ? 4 : 1;
				mod += 0.006F * bonus;
			}
			else if(creature instanceof EntityAnimal)
				mod += 0.004F;
			else if(creature instanceof EntityMob)
				mod -= 0.003F;
		}
		return mod;
	}
	
	@SubscribeEvent
	public static void onPlayerWakeUp(PlayerWakeUpEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		if(player.world.isRemote) return;
		
		if(event.shouldSetSpawn()) // If the "lying in bed" was successful (the player actually fell asleep)
		{
			StatTracker stats = player.getCapability(StatRegister.CAPABILITY, null);
			// Replenish 33% of total sanity!
			stats.modifyStat(DefaultStats.SANITY, DefaultStats.SANITY.getMaximum() / 3F);
			player.getFoodStats().setFoodLevel(player.getFoodStats().getFoodLevel() - 8);
		}
	}
	
	@SubscribeEvent
	public static void onConsumeItem(LivingEntityUseItemEvent.Tick event)
	{
		Entity ent = event.getEntity();
		if(ent.world.isRemote) return; // Sorry, we don't operate on client side here.
		
		if(ent instanceof EntityPlayer && event.getDuration() == 1)
		{
			try
			{
				// Try to get the modifier from the map (throws NPE when no such mapping exists)
				float mod = SanityModifier.foodSanityMap.get(event.getItem().getItem());
				
				// Modify the sanity value
				EntityPlayer player = (EntityPlayer)ent;
				StatTracker stats = player.getCapability(StatRegister.CAPABILITY, null);
				stats.modifyStat(DefaultStats.SANITY, mod);
			}
			catch(NullPointerException exc) {} // Food simply doesn't have sanity mapping
		}
	}
	
	@SubscribeEvent
	public static void onPlayerUpdate(LivingUpdateEvent event)
	{
		Entity ent = event.getEntity();//EntityWolf
		if(ent.world.isRemote) return;
		
		if(ent instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)ent;
			
			if(!player.isCreative() && !player.isSpectator() && player.isPlayerSleeping())
			{
				StatTracker stat = player.getCapability(StatRegister.CAPABILITY, null);
				stat.modifyStat(DefaultStats.SANITY, 0.004f);
				player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 20, 4, false, false));
			}
		}
	}
	
	@SubscribeEvent
	public static void onTame(AnimalTameEvent event)
	{
		Entity ent = event.getEntity();
		if(ent instanceof EntityPlayer && !ent.world.isRemote)
		{
			StatTracker stat = ent.getCapability(StatRegister.CAPABILITY, null);
			stat.modifyStat(DefaultStats.SANITY, 5F); // Solid 5 points for taming any animal
		}
	}
}