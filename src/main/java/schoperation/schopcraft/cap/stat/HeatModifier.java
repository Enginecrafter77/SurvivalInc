package schoperation.schopcraft.cap.stat;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.biome.Biome;
import schoperation.schopcraft.cap.wetness.IWetness;
import schoperation.schopcraft.cap.wetness.WetnessProvider;
import schoperation.schopcraft.config.SchopConfig;

public class HeatModifier {
	
	public static Map<Block, Float> heatmap = new HashMap<Block, Float>();
	
	public static void initHeatMap()
	{
		HeatModifier.heatmap.put(Blocks.LAVA, 0.25F);
		HeatModifier.heatmap.put(Blocks.FIRE, 0.1F);
	}
	
	public static float equalizeWithEnvironment(EntityPlayer player)
	{
		float target;
		if(player.posY < player.world.getSeaLevel()) target = 0.7F; // Cave
		else
		{
			Biome biome = player.world.getBiome(player.getPosition());
			target = biome.getTemperature(player.getPosition());
			if(target < -0.2F) target = -0.2F;
			if(target > 1.5F) target = 1.5F;
		}
		target *= 78; // Schoperation's constant
		
		StatTracker stats = player.getCapability(StatRegister.CAPABILITY, null);
		float rate = (float)SchopConfig.MECHANICS.heatExchangeFactor;
		float current = stats.getStat(DefaultStats.HEAT);
		
		// Is the current temperature in correct range?
		if(current < (target + rate) && current > (target - rate)) rate = 0;
		if(current > target) rate *= -1;
		return rate;
	}
	
	public static float applyWetnessCooldown(EntityPlayer player)
	{
		IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
		return -0.05F * (wetness.getWetness() / wetness.getMaxWetness());
	}
	
	public static float whenNearHotBlock(EntityPlayer player)
	{
		Vec3i offset = new Vec3i(2, 1, 2);
		BlockPos origin = player.getPosition();
		
		float max_proximity = (float)Math.sqrt(offset.distanceSq(0, 0, 0));
		
		Iterable<BlockPos> blocks = BlockPos.getAllInBox(origin.subtract(offset), origin.add(offset));
		
		float heat = 0;
		for(BlockPos position : blocks)
		{
			Block block = player.world.getBlockState(position).getBlock();
			if(HeatModifier.heatmap.containsKey(block))
			{
				float baseheat = HeatModifier.heatmap.get(block);
				float proximity = (float)Math.sqrt(origin.distanceSq(position));
				heat += baseheat * Math.pow(1F - (proximity / max_proximity), 1.5F);
			}
		}
		
		return heat;
	}
}
