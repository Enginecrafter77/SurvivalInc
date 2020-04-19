package enginecrafter77.survivalinc.stats.impl;

import enginecrafter77.survivalinc.ModDamageSources;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.modifier.ConditionalModifier;
import enginecrafter77.survivalinc.stats.modifier.DamagingModifier;
import enginecrafter77.survivalinc.stats.modifier.FunctionalModifier;
import enginecrafter77.survivalinc.stats.modifier.OperationType;
import enginecrafter77.survivalinc.stats.modifier.PotionEffectModifier;
import enginecrafter77.survivalinc.stats.modifier.ThresholdModifier;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HydrationModifier {
	
	public static void init()
	{
		DefaultStats.HYDRATION.modifiers.add(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.dimension == -1, -0.006F), OperationType.OFFSET);
		DefaultStats.HYDRATION.modifiers.add(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.isInLava(), -0.5F), OperationType.OFFSET);
		DefaultStats.HYDRATION.modifiers.add(new FunctionalModifier<EntityPlayer>(HydrationModifier::naturalDrain), OperationType.OFFSET);
		DefaultStats.HYDRATION.modifiers.add(new ThresholdModifier<EntityPlayer>(new DamagingModifier(ModDamageSources.DEHYDRATION, 4F, 0), 5F, ThresholdModifier.LOWER));
		DefaultStats.HYDRATION.modifiers.add(new ThresholdModifier<EntityPlayer>(new PotionEffectModifier(MobEffects.NAUSEA, 5), 15F, ThresholdModifier.LOWER));
		DefaultStats.HYDRATION.modifiers.add(new ThresholdModifier<EntityPlayer>(new PotionEffectModifier(MobEffects.MINING_FATIGUE, 3), 15F, ThresholdModifier.LOWER));
		DefaultStats.HYDRATION.modifiers.add(new ThresholdModifier<EntityPlayer>(new PotionEffectModifier(MobEffects.SLOWNESS, 3), 40F, ThresholdModifier.LOWER));
		DefaultStats.HYDRATION.modifiers.add(new ThresholdModifier<EntityPlayer>(new PotionEffectModifier(MobEffects.WEAKNESS, 2), 40F, ThresholdModifier.LOWER));
	}
	
	public static float naturalDrain(EntityPlayer player, float value)
	{
		StatTracker tracker = player.getCapability(StatCapability.target, null);
		float fraction = tracker.getStat(HeatModifier.instance) / HeatModifier.instance.getMaximum();
		
		float rate = -0.003F;
		
		// Cap the chance to 1/2
		float chance = fraction < 0.5F ? fraction : 0.5F;
		
		if(player.world.rand.nextFloat() < chance)
		{
			if(fraction > 0.5F) rate *= 10 * (fraction + 0.5F);
			return rate;
		}
		
		return 0F;
	}
	
	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		if(player.world.isRemote || player.isCreative() || player.isSpectator()) return;
		
		if(player.getHeldItem(EnumHand.MAIN_HAND) == ItemStack.EMPTY)
		{
			Vec3d look = player.getPositionEyes(1.0f).add(player.getLookVec().add(player.getLookVec().x < 0 ? -0.5 : 0.5, -1, player.getLookVec().z < 0 ? -0.5 : 0.5));
			RayTraceResult raytrace = player.world.rayTraceBlocks(player.getPositionEyes(1.0f), look, true);
			if(raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				BlockPos position = raytrace.getBlockPos();
				if(player.world.getBlockState(position).getMaterial() == Material.WATER)
				{
					WorldServer world = (WorldServer)player.world;
					world.spawnParticle(EnumParticleTypes.WATER_SPLASH, raytrace.hitVec.x, raytrace.hitVec.y + 0.1, raytrace.hitVec.z, 20, 0.5, 0.1, 0.5, 0.1, null);
					world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, raytrace.hitVec.x, raytrace.hitVec.y + 0.1, raytrace.hitVec.z, 20, 0.5, 0.1, 0.5, 0.1, null);
					world.playSound(null, position, SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.AMBIENT, 0.5f, 1.5f);
					
					StatTracker tracker = player.getCapability(StatCapability.target, null);
					tracker.modifyStat(DefaultStats.HYDRATION, 5F);
				}
			}
		}
	}
	
}
