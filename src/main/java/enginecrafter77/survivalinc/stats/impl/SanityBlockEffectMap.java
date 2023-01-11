package enginecrafter77.survivalinc.stats.impl;

import enginecrafter77.survivalinc.util.blockprop.BlockPropertyView;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;

public class SanityBlockEffectMap {
	private final BlockPropertyView<Float> sanityValues;

	public SanityBlockEffectMap(BlockPropertyView<Float> sanityValues)
	{
		this.sanityValues = sanityValues;
	}

	public float getBlockCoreSanityEffect(Block block)
	{
		return this.sanityValues.getValueFor(block).orElse(0F);
	}

	public float getBlockEffectAtDistance(Block block, float distance)
	{
		return this.getBlockCoreSanityEffect(block) * this.getDistanceScaleFactor(distance);
	}

	public float getDistanceScaleFactor(float distance)
	{
		return 0.05F / (1F + (float)Math.pow(distance, 2F));
	}

	public float calculateSurroundingsSanityEffect(IBlockAccess world, Vec3d position, float scanDistance)
	{
		Vec3i offset = new Vec3i(scanDistance, 1, scanDistance);
		BlockPos originblock = new BlockPos(position);
		Iterable<BlockPos.MutableBlockPos> blocks = BlockPos.getAllInBoxMutable(originblock.subtract(offset), originblock.add(offset));

		float effect = 0F;
		for(BlockPos blockPos : blocks)
		{
			Block block = world.getBlockState(blockPos).getBlock();
			float distance = (float)Math.sqrt(blockPos.distanceSqToCenter(position.x, position.y, position.z));
			effect += this.getBlockEffectAtDistance(block, distance);
		}
		return effect;
	}
}
