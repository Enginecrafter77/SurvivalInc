package enginecrafter77.survivalinc.season.melting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import java.util.Collection;

/**
 * A simple implementation of {@link ChunkFilter},
 * able to operate on single vertical layer, and
 * uniformly replace blocks of one type with blocks
 * on another type.
 * @author Enginecrafter77
 */
public abstract class LayeredChunkFilter implements ChunkFilter {
	private static final BlockPos chunkBlockRange = new BlockPos(15, 0, 15);

	protected final Collection<BlockPositionLevelMapper> layers;
	
	public LayeredChunkFilter(Collection<BlockPositionLevelMapper> layers)
	{
		this.layers = layers;
	}
	
	/**
	 * Checks if the block at the target position is suitable for transformation using this chunk filter.
	 * @see #transform(Chunk, BlockPos, IBlockState)
	 * @param chunk The chunk to operate in
	 * @param position The relative block position inside the chunk (X&Z capped at 15)
	 * @param state The block state at the target position
	 * @return True if the operation is allowed, false otherwise
	 */
	public abstract boolean shouldBlockBeTransformed(Chunk chunk, BlockPos position, IBlockState state);
	
	/**
	 * Applies whatever transformation this chunk filter should perform on a single block.
	 * If the returned {@link IBlockState} is not null, the block state at the specified
	 * position is set to that state.
	 * @see #shouldBlockBeTransformed(Chunk, BlockPos, IBlockState)
	 * @param chunk The chunk to operate in
	 * @param position The relative block position inside the chunk (X&Z capped in <0;15>)
	 * @param previous The previous {@link IBlockState} of the block
	 * @return The block state that should the block get, or null to disable replacing the block state
	 */
	public abstract IBlockState transform(Chunk chunk, BlockPos position, IBlockState previous);
	
	@Override
	public void processChunk(Chunk chunk)
	{
		for(BlockPositionLevelMapper layer : this.layers)
		{
			this.processChunkLayer(chunk, layer);
		}
	}
	
	/**
	 * Computes and returns a new {@link BlockPos} in the
	 * layer. The resultant BlockPos has the same X and Z
	 * values as the supplied BlockPos, but the Y value
	 * is set to reflect the specified layer, with respect
	 * to ground-relative marked values.
	 * @see #layers
	 * @param chunk The chunk
	 * @param position The position to transform
	 * @param layer The layer mapper
	 * @return BlockPos on the specified layer in the same world column as the source position
	 */
	protected BlockPos moveToLayer(Chunk chunk, BlockPos position, BlockPositionLevelMapper layer)
	{
		return layer.transformBlockPosInChunk(chunk, position);
	}
	
	/**
	 * Just as the name says. This method processes
	 * a certain vertical layer of the specified chunk.
	 * @param chunk The chunk to operate in
	 * @param layer The {@link #layers layer} to process
	 */
	protected void processChunkLayer(Chunk chunk, BlockPositionLevelMapper layer)
	{
		BlockPos.MutableBlockPos manipulationCopy = new BlockPos.MutableBlockPos();
		for(BlockPos position : BlockPos.getAllInBoxMutable(BlockPos.ORIGIN, LayeredChunkFilter.chunkBlockRange))
		{
			manipulationCopy.setPos(position);
			position = this.moveToLayer(chunk, manipulationCopy, layer);

			IBlockState state = chunk.getBlockState(position);
			if(this.shouldBlockBeTransformed(chunk, position, state))
			{
				state = this.transform(chunk, position, state);
				if(state != null)
					chunk.setBlockState(position, state);
			}
		}
	}
	
	@Override
	public String toString()
	{
		return this.layers.toString();
	}

}
