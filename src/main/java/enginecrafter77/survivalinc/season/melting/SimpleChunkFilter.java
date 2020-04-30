package enginecrafter77.survivalinc.season.melting;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;

/**
 * A simple implementation of {@link ChunkFilter},
 * able to operate on single vertical layer, and
 * uniformly replace blocks of one type with blocks
 * on another type.
 * @author Enginecrafter77
 */
public class SimpleChunkFilter implements ChunkFilter {
	
	/** The block to replace */
	public final Block from;
	
	/** The block to replace with */
	public final Block to;
	
	/** The Y offset of the original position */
	protected final int verticalOffset;
	
	/** True if the vertical offset is precipitation height relative, false if it's absolute (i.e. from bedrock)*/
	protected boolean surfaceRelative;
	
	public SimpleChunkFilter(Block from, Block to, int verticalOffset)
	{
		this.verticalOffset = verticalOffset;
		this.from = from;
		this.to = to;
		this.surfaceRelative = false;
	}
	
	/**
	 * Controls the value of {@link #surfaceRelative}
	 * @param surfaceRelative see {@link #surfaceRelative}
	 * @return The instance of the current class
	 */
	public SimpleChunkFilter setSurfaceRelative(boolean surfaceRelative)
	{
		this.surfaceRelative = surfaceRelative;
		return this;
	}
	
	/**
	 * Checks if the filter is allowed to continue
	 * operation with the specified parameters.
	 * @param chunk The chunk to operate in
	 * @param position The position relative to chunk border
	 * @param state The block state at the target position
	 * @return True if the operation is allowed, false otherwise
	 */
	public boolean isSubstitutionViable(Chunk chunk, BlockPos position, IBlockState state)
	{
		return state.getBlock() == this.from;
	}
	
	@Override
	public BlockPos offsetPosition(Chunk chunk, BlockPos position)
	{
		if(this.surfaceRelative)
		{
			position = chunk.getPrecipitationHeight(position);
		}
		
		return position.add(new Vec3i(0, this.verticalOffset, 0));
	}
	
	@Override
	public void applyToChunk(Chunk chunk, BlockPos position)
	{
		IBlockState state = chunk.getBlockState(position);
		if(this.isSubstitutionViable(chunk, position, state))
		{
			chunk.setBlockState(position, this.to.getDefaultState());
		}
	}

}
