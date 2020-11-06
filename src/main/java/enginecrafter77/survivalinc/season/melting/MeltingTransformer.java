package enginecrafter77.survivalinc.season.melting;

import enginecrafter77.survivalinc.block.BlockMelting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

/**
 * MeltingTransformer is a simple {@link LayeredChunkFilter} implementation,
 * which checks if the block at the specified position is the freezeTarget,
 * and if the block's {@link BlockMelting#getAction(net.minecraft.world.World, BlockPos)}
 * evaluates to {@link BlockMelting.MeltAction#MELT}, it replaces the block
 * with the instance of {@link BlockMelting}. 
 * @author Enginecrafter77
 */
public class MeltingTransformer extends LayeredChunkFilter {
	
	public BlockMelting meltingblock;
	
	public MeltingTransformer(BlockMelting block)
	{
		this.meltingblock = block;
	}
	
	@Override
	public boolean shouldBlockBeTransformed(Chunk chunk, BlockPos position, IBlockState state)
	{
		return state.getBlock() == this.meltingblock.freezeTarget && this.meltingblock.getAction(chunk.getWorld(), chunk.getPos().getBlock(position.getX(), position.getY(), position.getZ())) == BlockMelting.MeltAction.MELT;
	}
	
	@Override
	public IBlockState transform(Chunk chunk, BlockPos position, IBlockState previous)
	{
		return this.meltingblock.getDefaultState();
	}
	
	@Override
	public String toString()
	{
		return String.format("MeltingTransformer(%s: %s->%s | %s)", this.meltingblock.getLocalizedName(), this.meltingblock.freezeTarget.getLocalizedName(), this.meltingblock.meltTarget.getLocalizedName(), super.toString());
	}
}