package enginecrafter77.survivalinc.season.melting;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;

public class SimpleChunkFilter implements ChunkFilter {
	
	public final Block from, to;
	
	protected final int verticalOffset;
	protected boolean surfaceRelative;
	
	public SimpleChunkFilter(Block from, Block to, int verticalOffset)
	{
		this.verticalOffset = verticalOffset;
		this.from = from;
		this.to = to;
		this.surfaceRelative = false;
	}
	
	public SimpleChunkFilter setSurfaceRelative(boolean surfaceRelative)
	{
		this.surfaceRelative = surfaceRelative;
		return this;
	}
	
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
