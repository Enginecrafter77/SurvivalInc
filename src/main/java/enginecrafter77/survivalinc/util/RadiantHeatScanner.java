package enginecrafter77.survivalinc.util;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class RadiantHeatScanner {
	private final Map<Block, Float> blockHeatMap;

	private float gaussScaleFactor;
	private boolean surplusHeat;

	public RadiantHeatScanner()
	{
		this.blockHeatMap = new HashMap<Block, Float>();
		this.gaussScaleFactor = 1F;
		this.surplusHeat = false;
	}

	public void registerBlock(Block block, float coreHeat)
	{
		this.blockHeatMap.put(block, coreHeat);
	}

	@Nullable
	public Float getCoreHeatOf(Block block)
	{
		return this.blockHeatMap.get(block);
	}

	public void setGaussScaleFactor(float gaussScaleFactor)
	{
		this.gaussScaleFactor = gaussScaleFactor;
	}

	public void setSurplusHeatEnables(boolean surplusHeat)
	{
		this.surplusHeat = surplusHeat;
	}

	public float getDistanceRadiationLossFactor(float coreHeat, float distance)
	{
		return (float)(this.gaussScaleFactor / (Math.pow(distance, 2) + this.gaussScaleFactor));
	}

	public float getHeatReceivedFromBlock(Block block, BlockPos position, Vec3d target)
	{
		Float coreHeat = this.getCoreHeatOf(block);
		if(coreHeat == null)
			return 0F;

		float distance = (float)Math.sqrt(position.distanceSqToCenter(target.x, target.y, target.z));
		return coreHeat * this.getDistanceRadiationLossFactor(coreHeat, distance);
	}

	public float scanPosition(IBlockAccess world, Vec3d origin, float range)
	{
		Vec3i offset = new Vec3i(range, 1, range);
		BlockPos originblock = new BlockPos(origin);

		Iterable<BlockPos> blocks = BlockPos.getAllInBox(originblock.subtract(offset), originblock.add(offset));

		float heat = 0F;
		for(BlockPos position : blocks)
		{
			Block block = world.getBlockState(position).getBlock();
			float receivedHeat = this.getHeatReceivedFromBlock(block, position, origin);
			if(receivedHeat > heat)
				heat = receivedHeat;
			else if(this.surplusHeat)
				heat += receivedHeat * 0.1;
		}

		return heat;
	}
}
