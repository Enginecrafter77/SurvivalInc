package enginecrafter77.survivalinc.season.melting;

import enginecrafter77.survivalinc.block.BlockMelting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

/**
 * LazyMeltingTransformer is designed for situations where
 * BlockMelting doesn't tick randomly, but instead needs
 * to receive ticks manually.
 * @author Enginecrafter77
 */
public class LazyMeltingTransformer extends MeltingTransformer {

	public LazyMeltingTransformer(BlockMelting block)
	{
		super(block);
	}

	@Override
	public boolean shouldReplace(Chunk chunk, BlockPos position, IBlockState state)
	{
		return super.shouldReplace(chunk, position, state) || state.getBlock() == this.meltingblock && chunk.getWorld().rand.nextFloat() > 0.1F;
	}
	
	@Override
	public IBlockState getReplacement(Chunk chunk, BlockPos position, IBlockState previous)
	{
		if(previous == this.meltingblock.predecessor)
			super.getReplacement(chunk, position, previous);
		else
			this.meltingblock.updateTick(chunk.getWorld(), chunk.getPos().getBlock(position.getX(), position.getY(), position.getZ()), previous, chunk.getWorld().rand);
		
		return null;
	}

}
