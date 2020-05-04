package enginecrafter77.survivalinc.stats.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.modifier.ConditionalModifier;
import enginecrafter77.survivalinc.stats.modifier.FunctionalModifier;
import enginecrafter77.survivalinc.stats.modifier.OperationType;
import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldServer;

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
		DefaultStats.WETNESS.modifiers.add(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.world.isRainingAt(player.getPosition().up()), 0.01F), OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.add(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.dimension == -1, -0.08F), OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.add(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.isBurning(), -0.8F), OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.add(new FunctionalModifier<EntityPlayer>(WetnessModifier::scanSurroundings), OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.add(new FunctionalModifier<EntityPlayer>(WetnessModifier::naturalDrying), OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.add(new FunctionalModifier<EntityPlayer>(WetnessModifier::whenInWater), OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.add(new FunctionalModifier<EntityPlayer>(WetnessModifier::causeDripping));
		DefaultStats.WETNESS.modifiers.add(new FunctionalModifier<EntityPlayer>(WetnessModifier::slowDown));
		
		WetnessModifier.humiditymap = new HashMap<Block, Float>();
		WetnessModifier.humiditymap.put(Blocks.FIRE, -0.5F);
		WetnessModifier.humiditymap.put(Blocks.LAVA, -1F);
		WetnessModifier.humiditymap.put(Blocks.FLOWING_LAVA, -1F);
		WetnessModifier.humiditymap.put(Blocks.LIT_FURNACE, -0.4F);
		WetnessModifier.humiditymap.put(Blocks.MAGMA, -0.4F);
	}
	
	public static float slowDown(EntityPlayer player, float current)
	{
		// We only want to run this on server side
		if(!player.world.isRemote)
		{
			float max = DefaultStats.WETNESS.getMaximum();
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
		return current;
	}
	
	public static float causeDripping(EntityPlayer player, float current)
	{
		if(!player.world.isRemote)
		{
			WorldServer serverworld = (WorldServer)player.world;
			float particle_amount = 20 * (current / DefaultStats.WETNESS.getMaximum());
			serverworld.spawnParticle(EnumParticleTypes.DRIP_WATER, player.posX, player.posY, player.posZ, Math.round((float)Math.floor(particle_amount)), 0.25, 0.5, 0.25, 0.1, null);
		}
		return current;
	}
	
	public static float whenInWater(EntityPlayer player, float value)
	{
		if(player.isInWater())
		{
			/*
			 * Hey now, the player could just be in one block of water at
			 * their feet. If so, just go to 40% wetness Or somewhere around there.
			 * We'll check if water is in the player's face. If so, 100%. If not, 40%.
			 */
			Block headblock = player.world.getBlockState(player.getPosition().up()).getBlock();
			if(headblock != Blocks.WATER && headblock != Blocks.FLOWING_WATER)
			{
				if(value < 40F) return 1.25F;
			}
			else return 5F;
		}
		return 0F;
	}
	
	public static float naturalDrying(EntityPlayer player)
	{
		float rate = -(float)ModConfig.WETNESS.passiveDryRate;
		if(player.world.isDaytime() && player.world.canBlockSeeSky(player.getPosition())) rate *= ModConfig.WETNESS.sunlightMultiplier;
		return rate;
	}
	
	public static float scanSurroundings(EntityPlayer player)
	{
		Vec3i offset = new Vec3i(2, 1, 2);
		BlockPos origin = player.getPosition();
		
		Iterable<BlockPos> blocks = BlockPos.getAllInBox(origin.subtract(offset), origin.add(offset));
		
		float diff = 0;
		for(BlockPos position : blocks)
		{
			Block block = player.world.getBlockState(position).getBlock();
			if(WetnessModifier.humiditymap.containsKey(block))
			{
				float basewetness = WetnessModifier.humiditymap.get(block);
				float proximity = (float)Math.sqrt(origin.distanceSq(position));
				diff += basewetness / proximity;
			}
		}
		if(player.isWet()) diff /= 2F;
		return diff;
	}
}