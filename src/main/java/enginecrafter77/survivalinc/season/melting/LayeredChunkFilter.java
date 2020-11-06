package enginecrafter77.survivalinc.season.melting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

/**
 * A simple implementation of {@link ChunkFilter},
 * able to operate on single vertical layer, and
 * uniformly replace blocks of one type with blocks
 * on another type.
 * @author Enginecrafter77
 */
public abstract class LayeredChunkFilter implements ChunkFilter {
	private static final BlockPos chunkBlockRange = new BlockPos(15, 0, 15);
	
	/**
	 * The internal list of layers. A layer is a number which
	 * can represent 2 states using single number. The algorithm
	 * takes advantage of the fact that minecraft height is always
	 * positive. Numbers greater than 0 are treated as absolute
	 * Y coordinates in the world. Negative layer values are used
	 * to represent the Y coordinates relative to the top-most solid
	 * block in a column of blocks (i.e. the precipitation height - 1).
	 * But since the offset from the ground block can be both positive
	 * and negative, a problem arises with maths. As such, it was solved
	 * by adding a {@link #surface_offset common negative number} to the
	 * surface relative numbers. This way, the surface-relative values
	 * are easily distinguished by their sign and can also take both
	 * positive and negative values.
	 */
	protected final List<Integer> layers;
	
	/**
	 * A common number used to separate the surface-relative
	 * numbers from others in the list. The absolute value of
	 * this number is the maximum positive offset to the ground
	 * that can be represented this way.
	 * @see #layers
	 */
	private final int surface_offset;
	
	/**
	 * Constructs a new layered chunk filter with the specified surface offset
	 * @param surface_offset The surface offset denominator
	 */
	public LayeredChunkFilter(int surface_offset)
	{
		this.layers = new ArrayList<Integer>();
		this.surface_offset = surface_offset;
	}
	
	/**
	 * Constructs a new layered chunk filter with the {@link #surface_offset surface offset denominator} of -1024.
	 * @see #surface_offset
	 */
	public LayeredChunkFilter()
	{
		this(-1024);
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
	
	/**
	 * Adds a layer referencing uniform absolute Y coordinate in world.
	 * @param height The absolute Y coordinate to be processed
	 * @return The instance of the current class
	 */
	public LayeredChunkFilter addAbsoluteLayer(int height) throws IndexOutOfBoundsException
	{
		if(height < 0) throw new IndexOutOfBoundsException("Height cannot be less than 0!");
		return this.addLayer(height);
	}
	
	/**
	 * Adds a layer referencing blocks relative to the ground (top-most solid block)
	 * on the specific world block column.
	 * @param offset The relative Y offset from the ground block
	 * @return The instance of the current class
	 */
	public LayeredChunkFilter addSurfaceRelativeLayer(int offset) throws IndexOutOfBoundsException
	{
		int layer = this.surfaceOffsetToLayer(offset);
		if(layer > 0) throw new IndexOutOfBoundsException("Surface relative offset overflown into positive range!");
		return this.addLayer(layer);
	}
	
	/**
	 * Adds the specified layer to the {@link #layers internal list}.
	 * Please be aware that ABSOLUTELY no bounds checking is
	 * done to the numbers. The numbers are expected to strictly
	 * follow the specification, or else the call will result
	 * in undefined behavior.
	 * @param layer The layer according to the {@link #layers specification}
	 * @return Instance of the current class
	 */
	public LayeredChunkFilter addLayer(int layer)
	{
		this.layers.add(layer);
		return this;
	}
	
	/**
	 * Calculates the layer index of the offset relative to surface.
	 * This method can be used to supply numbers to {@link #addLayer(int)}
	 * directly without invoking separate methods.
	 * @param offset The offset from the surface.
	 * @return Layer index with regards to the input parameter as offset from surface
	 */
	public int surfaceOffsetToLayer(int offset)
	{
		return this.surface_offset + offset;
	}
	
	protected int layerToSurfaceOffset(int layer)
	{
		return layer - this.surface_offset;
	}
	
	@Override
	public void processChunk(Chunk chunk)
	{
		for(int layer : this.layers)
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
	 * @param position
	 * @param layer
	 * @return BlockPos on the specified layer in the same world column as the source position
	 */
	protected BlockPos moveToLayer(Chunk chunk, BlockPos position, int layer)
	{
		if(layer < 0)
			layer = chunk.getPrecipitationHeight(position).getY() - 1 + this.layerToSurfaceOffset(layer);
		
		return new BlockPos(position.getX(), layer, position.getZ());
	}
	
	/**
	 * Just as the name says. This method processes
	 * a certain vertical layer of the specified chunk.
	 * @param chunk The chunk to operate in
	 * @param layer The {@link #layers layer} to process
	 */
	protected void processChunkLayer(Chunk chunk, int layer)
	{
		for(BlockPos position : BlockPos.getAllInBox(BlockPos.ORIGIN, LayeredChunkFilter.chunkBlockRange))
		{
			position = this.moveToLayer(chunk, position, layer);
			IBlockState state = chunk.getBlockState(position);
			if(this.shouldBlockBeTransformed(chunk, position, state))
			{
				state = this.transform(chunk, position, state);
				if(state != null) chunk.setBlockState(position, state);
			}
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		for(Integer layer : this.layers)
		{
			// If the layer is surface-relative
			if(layer < 0)
			{
				layer = this.layerToSurfaceOffset(layer); // Overwrite it with the offset
				builder.append('S'); // Add S to indicate surface
				if(layer >= 0) builder.append('+'); // Add + sign if the offset is 0 or more (minis would be added automatically)
			}
			builder.append(layer);
			builder.append(", ");
		}
		builder.setLength(builder.length() - 2);
		builder.append(']');
		return builder.toString();
	}

}
