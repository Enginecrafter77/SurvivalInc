package enginecrafter77.survivalinc.stats.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.StatRegisterEvent;
import enginecrafter77.survivalinc.stats.effect.ConstantStatEffect;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffect;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffectFilter;
import enginecrafter77.survivalinc.stats.effect.SideEffectFilter;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/*
 * This is where the magic of changing one's wetness occurs. You'll most likely be here.
 * Geez that sounds wrong. I wonder if Klei implemented their wetness mechanic with dumb jokes too.
 * This is also the first mechanic I implemented, so some old code will most likely be here.
 * The newest is temperature, so that'll look more functional.
 * 	-Schoperation
 * 
 * Although this class has been rewritten from the ground up, I still wanted to
 * keep the schoperation's comment above. I find it funny.
 */

public class WetnessModifier {
	public static Map<Block, Float> humiditymap;
	
	public static UUID wetnessSlowdown = UUID.randomUUID();
	
	public static void init()
	{
		DefaultStats.WETNESS.effects.addEffect(new ConstantStatEffect(ConstantStatEffect.Operation.OFFSET, 0.01F), new FunctionalEffectFilter((EntityPlayer player, Float value) -> player.world.isRainingAt(player.getPosition().up())));
		DefaultStats.WETNESS.effects.addEffect(new ConstantStatEffect(ConstantStatEffect.Operation.OFFSET, -0.08F), HydrationModifier.isOutsideOverworld);
		DefaultStats.WETNESS.effects.addEffect(new ConstantStatEffect(ConstantStatEffect.Operation.OFFSET, -0.8F), new FunctionalEffectFilter((EntityPlayer player, Float value) -> player.isBurning()));
		
		DefaultStats.WETNESS.effects.addEffect(new FunctionalEffect(WetnessModifier::scanSurroundings));
		DefaultStats.WETNESS.effects.addEffect(new FunctionalEffect(WetnessModifier::naturalDrying));
		DefaultStats.WETNESS.effects.addEffect(new FunctionalEffect(WetnessModifier::whenInWater));
		
		DefaultStats.WETNESS.effects.addEffect(new FunctionalEffect(WetnessModifier::causeDripping), new SideEffectFilter(Side.CLIENT));
		DefaultStats.WETNESS.effects.addEffect(new FunctionalEffect(WetnessModifier::slowDown), new SideEffectFilter(Side.SERVER));
		
		WetnessModifier.humiditymap = new HashMap<Block, Float>();
		WetnessModifier.humiditymap.put(Blocks.FIRE, -0.5F);
		WetnessModifier.humiditymap.put(Blocks.LAVA, -1F);
		WetnessModifier.humiditymap.put(Blocks.FLOWING_LAVA, -1F);
		WetnessModifier.humiditymap.put(Blocks.LIT_FURNACE, -0.4F);
		WetnessModifier.humiditymap.put(Blocks.MAGMA, -0.4F);
	}
	
	@SubscribeEvent
	public static void registerStat(StatRegisterEvent event)
	{
		event.register(DefaultStats.WETNESS);
	}
	
	public static void slowDown(EntityPlayer player, float current)
	{
		float max = DefaultStats.WETNESS.max;
		float threshold = (float)ModConfig.WETNESS.slowdownThreshold / 100F;
		
		// This is the math part. I am way less worried about impact of this. Mmmm math...
		float mod = 0F;
		if(current > (threshold * max))
		{
			/*
			 * We want to achieve following scenario:
			 * 	When the wetness is just at the slowdown threshold, apply zero slowdown (closest 0)
			 * 	As the wetness is rising, slow the player down in linear manner (which implies using direct relationship function)
			 * 	When the wetness is at maximum, the value should be just above -1 (to avoid total nullification of the speed)
			 * After some experimentation and calculations, I came up with this little equation:
			 * 	      1 - g           g - 1
			 * 	y = ---------- x + t -------
			 * 	     m(t - 1)         t - 1
			 * It may seem a little bit complicated, but you would get to that anyways if you would try to solve it.
			 */
			float correction = (float)ModConfig.WETNESS.minimalWalkSpeed;
			float inclination = (1 - correction) / (max * (threshold - 1)); // The inclination of the graph, aka the A
			float offset = threshold * ((correction - 1) / (threshold - 1)); // The offset of the graph, aka the B
			mod += inclination * current + offset; // Direct relationship formula
		}
		
		// Ugh I really hate this code. It's damn ineffective. So much list IO to handle every single tick.
		IAttributeInstance inst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
		inst.removeModifier(WetnessModifier.wetnessSlowdown);
		inst.applyModifier(new AttributeModifier(WetnessModifier.wetnessSlowdown, "wetnessSlowdown", mod, 1).setSaved(false));
	}
	
	/**
	 * Causes client-side dripping effect
	 * @param player The player to apply for
	 * @param current The current level of wetness
	 */
	public static void causeDripping(EntityPlayer player, float current)
	{
		WorldClient world = Minecraft.getMinecraft().world;
		Random rng = world.rand;
		float coefficient = (current / DefaultStats.WETNESS.max);
		if(rng.nextFloat() < coefficient)
		{
			for(int index = 0; index < 4; index++)
			{
				world.spawnParticle(EnumParticleTypes.DRIP_WATER, player.posX + (rng.nextFloat() * 0.5 - 0.25), player.posY + (rng.nextFloat() * 1 + 0.25), player.posZ + (rng.nextFloat() * 0.5 - 0.25), player.motionX, -0.5, player.motionZ, null);
			}
		}
	}
	
	public static float whenInWater(EntityPlayer player, float value)
	{
		if(player.isInWater())
		{
			Material headBlockMaterial = player.world.getBlockState(new BlockPos(player).up()).getMaterial();
			if(headBlockMaterial == Material.WATER) value += 5F;
			else if(value < 0.4F * DefaultStats.WETNESS.max) value += 1.25F;
		}
		
		return value;
	}
	
	public static float naturalDrying(EntityPlayer player, float value)
	{
		if(!player.isWet())
		{
			float rate = (float)ModConfig.WETNESS.passiveDryRate;
			if(player.world.isDaytime() && player.world.canBlockSeeSky(player.getPosition())) rate *= ModConfig.WETNESS.sunlightMultiplier;
			value -= rate;
		}
		return value;
	}
	
	public static float scanSurroundings(EntityPlayer player, float value)
	{
		Vec3i offset = new Vec3i(2, 1, 2);
		BlockPos origin = new BlockPos(player);
		
		Iterable<BlockPos> blocks = BlockPos.getAllInBox(origin.subtract(offset), origin.add(offset));
		
		float diff = 0;
		for(BlockPos position : blocks)
		{
			Block block = player.world.getBlockState(position).getBlock();
			if(WetnessModifier.humiditymap.containsKey(block))
			{
				float basewetness = WetnessModifier.humiditymap.get(block);
				float proximity = (float)Math.sqrt(position.distanceSq(player.posX, player.posY, player.posZ));
				diff += basewetness / proximity;
			}
		}
		if(player.isWet()) diff /= 2F;
		return value + diff;
	}
}