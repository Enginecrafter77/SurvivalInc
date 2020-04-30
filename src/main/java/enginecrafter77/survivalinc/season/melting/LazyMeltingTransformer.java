package enginecrafter77.survivalinc.season.melting;

import enginecrafter77.survivalinc.block.BlockMelting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * LazyMeltingTransformer is designed for situations where
 * BlockMelting doesn't tick randomly, but instead needs
 * to receive ticks manually. This class provides just that.
 * On every call to {@link #applyToChunk(Chunk, BlockPos)},
 * in addition to checking for the substituted block, it
 * is also checked if there is a {@link BlockMelting melting block}.
 * If it is, it calls it's {@link Block#updateTick(World, BlockPos, IBlockState, java.util.Random)}
 * with 90% probability to update it's melt property.
 * @author Enginecrafter77
 */
public class LazyMeltingTransformer extends SimpleMeltingTransformer {

	public LazyMeltingTransformer(BlockMelting block, int level)
	{
		super(block, level);
	}

	@Override
	public void applyToChunk(Chunk chunk, BlockPos position)
	{
		super.applyToChunk(chunk, position);
		
		IBlockState state = chunk.getBlockState(position);
		Block block = state.getBlock();
		World world = chunk.getWorld();
		if(block == this.to && world.rand.nextFloat() > 0.1F)
		{
			block.updateTick(world, chunk.getPos().getBlock(position.getX(), position.getY(), position.getZ()), state, world.rand);
		}
	}

}
