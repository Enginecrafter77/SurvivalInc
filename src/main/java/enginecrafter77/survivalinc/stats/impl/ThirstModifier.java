package enginecrafter77.survivalinc.stats.impl;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeBeach;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.biome.BiomeSwamp;
import java.util.Iterator;

import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;

public class ThirstModifier {

	public void onPlayerInteract(EntityPlayer player)
	{
		// Capability
		StatTracker stat = player.getCapability(StatRegister.CAPABILITY, null);
		
		// Ray trace result for drinking with bare hands. pretty ineffective.
		double vecX = player.getLookVec().x < 0 ? -0.5 : 0.5;
		double vecZ = player.getLookVec().z < 0 ? -0.5 : 0.5;
		
		// Now the actual raytrace.
		Vec3d look = player.getPositionEyes(1.0f).add(player.getLookVec().addVector(vecX, -1, vecZ));
		RayTraceResult raytrace = player.world.rayTraceBlocks(player.getPositionEyes(1.0f), look, true);

		// Is there something?
		if(raytrace != null)
		{
			// Is it a block?
			if(raytrace.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				BlockPos pos = raytrace.getBlockPos();
				Iterator<ItemStack> handItems = player.getHeldEquipment().iterator();

				// If it is water and the player isn't holding jack squat (main
				// hand).
				if(player.world.getBlockState(pos).getMaterial() == Material.WATER && handItems.next().isEmpty())
				{
					// Still more if statements. now see what biome the player
					// is in, and quench thirst accordingly.
					Biome biome = player.world.getBiome(pos);
					
					if(biome instanceof BiomeOcean || biome instanceof BiomeBeach)
					{
						stat.modifyStat(DefaultStats.HYDRATION, 0.5f);
					}
					else if(biome instanceof BiomeSwamp)
					{
						stat.modifyStat(DefaultStats.HYDRATION, 0.25f);
						player.addPotionEffect(new PotionEffect(MobEffects.POISON, 12, 3, false, false));
					}
					else
					{
						stat.modifyStat(DefaultStats.HYDRATION, 0.4f); 
						if(Math.random() <= 0.50)
							player.addPotionEffect(new PotionEffect(MobEffects.POISON, 12, 1, false, false));
					}
					
					if(!player.world.isRemote)
					{
						player.world.spawnParticle(EnumParticleTypes.DRIP_WATER, pos.getX(), pos.getY(), pos.getZ(), 0.3d, 0.5d, 0.3d);
						player.world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.NEUTRAL, 0.5f, 1.5f);
					}
				}
			}
		}
	}
}