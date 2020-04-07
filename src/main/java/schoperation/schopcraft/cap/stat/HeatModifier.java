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
import schoperation.schopcraft.util.OperationType;
import schoperation.schopcraft.util.ModifierCalculator;

public class HeatModifier implements StatProvider {
	private static final long serialVersionUID = 6260092840749029918L;
	
	//Temporary debug trick
	public int tick;
	
	public static final HeatModifier instance = new HeatModifier();
	
	public Map<Block, Float> heatmap;
	public ModifierCalculator<EntityPlayer> targettemp;
	public ModifierCalculator<EntityPlayer> exchangerate;
	
	public HeatModifier()
	{
		this.heatmap = new HashMap<Block, Float>();
		this.targettemp = new ModifierCalculator<EntityPlayer>();
		this.exchangerate = new ModifierCalculator<EntityPlayer>();
		
		this.heatmap.put(Blocks.LAVA, 10F);
		this.heatmap.put(Blocks.FIRE, 5F);
		
		this.targettemp.addModifier(HeatModifier::whenNearHotBlock, OperationType.OFFSET);
		this.exchangerate.addModifier(HeatModifier::applyWetnessCooldown, OperationType.SCALE);
		
		this.tick = 0;
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
		float rate = difference * (float)SchopConfig.MECHANICS.heatExchangeFactor;
		rate = this.exchangerate.apply(player, rate);
		
		if(current > target) rate *= -1;
		
		// Debugging
		if(tick++ % 20 == 0)
			System.out.format("H: %f (+- %f) --> T: %f\n", current, rate, target);
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
		IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
		return 1F + (wetness.getWetness() / wetness.getMaxWetness());
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
			if(HeatModifier.instance.heatmap.containsKey(block))
			{
				float baseheat = HeatModifier.instance.heatmap.get(block);
				float proximity = (float)Math.sqrt(origin.distanceSq(position));
				heat += baseheat * (1F - (proximity / max_proximity));
			}
		}
		
		return heat;
	}
}
