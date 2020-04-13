package enginecrafter77.survivalinc.stats.impl;

import java.util.HashMap;
import java.util.Map;

import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.OverflowHandler;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.modifier.FunctionalModifier;
import enginecrafter77.survivalinc.stats.modifier.ModifierApplicator;
import enginecrafter77.survivalinc.stats.modifier.OperationType;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.biome.Biome;

/**
 * The class that handles heat radiation and
 * it's associated interactions with the
 * player entity.
 * @author Enginecrafter77
 */
public class HeatModifier implements StatProvider {
	private static final long serialVersionUID = 6260092840749029918L;
	
	public static final HeatModifier instance = new HeatModifier();
	
	public Map<Block, Float> heatmap;
	public ModifierApplicator<EntityPlayer> targettemp;
	public ModifierApplicator<EntityPlayer> exchangerate;
	
	public HeatModifier()
	{
		this.heatmap = new HashMap<Block, Float>();
		this.targettemp = new ModifierApplicator<EntityPlayer>();
		this.exchangerate = new ModifierApplicator<EntityPlayer>();
		
		this.heatmap.put(Blocks.LAVA, 400F);
		this.heatmap.put(Blocks.FLOWING_LAVA, 350F);
		this.heatmap.put(Blocks.MAGMA, 300F);
		this.heatmap.put(Blocks.FIRE, 200F);
		this.heatmap.put(Blocks.LIT_FURNACE, 80F);
		this.heatmap.put(Blocks.LIT_PUMPKIN, 10F);
		
		this.targettemp.add(new FunctionalModifier<EntityPlayer>(HeatModifier::whenNearHotBlock), OperationType.OFFSET);
		this.exchangerate.add(new FunctionalModifier<EntityPlayer>(HeatModifier::applyWetnessCooldown), OperationType.SCALE);
	}
	
	@Override
	public float updateValue(EntityPlayer player, float current)
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
		target = targettemp.apply(player, target * 78); // 78 = Schoperation's body heat constant
		
		float difference = Math.abs(target - current);
		float rate = difference * (float)ModConfig.HEAT.heatExchangeFactor;
		rate = this.exchangerate.apply(player, rate);
		
		// If the current value is higher than the target, go down instead of up
		if(current > target) rate *= -1;
		return current + rate;
	}

	@Override
	public String getStatID()
	{
		return "heat";
	}

	@Override
	public float getMaximum()
	{
		return 120;
	}

	@Override
	public float getMinimum()
	{
		return 0;
	}

	@Override
	public float getDefault()
	{
		return 75;
	}
	
	public static float applyWetnessCooldown(EntityPlayer player)
	{
		StatTracker stats = player.getCapability(StatRegister.CAPABILITY, null);
		return 1F + (stats.getStat(DefaultStats.WETNESS) / DefaultStats.WETNESS.getMaximum());
	}
	
	/**
	 * Applies the highest heat emmited by the neighboring blocks.
	 * Note that this method does NOT account blocks inbetween, as
	 * that would need to involve costly raytracing. Also, only the
	 * heat Anyway, the
	 * way the heat delivered to the player is calculated by the
	 * following formula:
	 * <pre>
	 *                   s
	 * f(x): y = t * ---------
	 *                x^2 + s
	 * </pre>
	 * Where t is the base heat of the block (the heat delivered when
	 * the distance to the source is 0), s is a special so-called
	 * "gaussian constant" and x is the distance to the player. The
	 * "gaussian constant" has got it's name because the graph of
	 * that function roughly resembles gauss's curve. The constant
	 * in itself is a special value that indicates the scaling of
	 * the heat given. The higher the value is the slower the heat
	 * decline with distance is. A fairly reasonable value is 1.5,
	 * but this value can be specified in the config. It is recommended
	 * that players that use low block scan range to also use lower
	 * gaussian constant.
	 * @author Enginecrafter77
	 * @param player The player to apply this function to
	 * @return The addition to the heat stat value
	 */
	public static float whenNearHotBlock(EntityPlayer player)
	{
		Vec3i offset = new Vec3i(ModConfig.HEAT.blockScanRange, 1, ModConfig.HEAT.blockScanRange);
		BlockPos originblock = player.getPosition();
		
		Iterable<BlockPos> blocks = BlockPos.getAllInBox(originblock.subtract(offset), originblock.add(offset));
		
		float heat = 0;
		for(BlockPos position : blocks)
		{
			Block block = player.world.getBlockState(position).getBlock();
			if(HeatModifier.instance.heatmap.containsKey(block))
			{
				float currentheat = HeatModifier.instance.heatmap.get(block);
				float proximity = (float)Math.sqrt(player.getPositionVector().squareDistanceTo(new Vec3d(position)));
				currentheat *= (float)(ModConfig.HEAT.gaussScaling / (Math.pow(proximity, 2) + ModConfig.HEAT.gaussScaling));
				if(currentheat > heat) heat = currentheat; // Use only the maximum value
			}
		}
		
		return heat;
	}

	@Override
	public OverflowHandler getOverflowHandler()
	{
		return OverflowHandler.NONE;
	}
}
