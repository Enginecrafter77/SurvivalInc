package enginecrafter77.survivalinc.stats.impl;

import java.util.HashMap;
import java.util.Map;

import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.util.SchopServerParticles;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

/*
 * This is where the magic of changing one's wetness occurs. You'll most likely be here.
 * Geez that sounds wrong. I wonder if Klei implemented their wetness mechanic with dumb jokes too.
 * This is also the first mechanic I implemented, so some old code will most likely be here.
 * The newest is temperature, so that'll look more functional.
 * 	-Schoperation
 */

public class WetnessModifier {
	
	public static Map<Block, Float> humiditymap;
	
	public static void initHumidityMap()
	{
		WetnessModifier.humiditymap = new HashMap<Block, Float>();
		//TODO extend StatCalculator, make block space iterator
		WetnessModifier.humiditymap.put(Blocks.FIRE, -0.5F);
		WetnessModifier.humiditymap.put(Blocks.LAVA, -1F);
		WetnessModifier.humiditymap.put(Blocks.FLOWING_LAVA, -1F);
		WetnessModifier.humiditymap.put(Blocks.LIT_FURNACE, -0.4F);
		WetnessModifier.humiditymap.put(Blocks.MAGMA, -0.4F);
	}
	
	public static float whenInWater(EntityPlayer player)
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
				StatTracker stats = player.getCapability(StatRegister.CAPABILITY, null);
				if(stats.getStat(DefaultStats.WETNESS) < 40) return 1.25F;
			}
			else return 5F;
		}
		return 0F;
	}
	
	public static float naturalDrying(EntityPlayer player)
	{
		float rate = -0.005F;
		if(player.world.isDaytime() && player.world.canBlockSeeSky(player.getPosition())) rate *= 2;
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
	
	public static void onPlayerUpdate(Entity player)
	{
		SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "WetnessParticles", player.posX, player.posY, player.posZ);
	}
}