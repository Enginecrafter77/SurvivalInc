package enginecrafter77.survivalinc.season.melting;

import enginecrafter77.survivalinc.block.BlockMelting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

/**
 * SimpleMeltingTransformer is a simple extension to {@link LayeredChunkFilter},
 * which additionally checks for whether it's freezing or not at the target
 * position. If it's not, it replaces the target block (pulled from {@link BlockMelting}
 * instance) with the aforementioned melting block.
 * @author Enginecrafter77
 */
public class MeltingTransformer extends LayeredChunkFilter {
	
	public BlockMelting meltingblock;
	
	public MeltingTransformer(BlockMelting block)
	{
		this.meltingblock = block;
	}
	
	@Override
	public boolean shouldReplace(Chunk chunk, BlockPos position, IBlockState state)
	{
		return state.getBlock() == this.meltingblock.predecessor && this.meltingblock.shouldMelt(chunk.getWorld(), chunk.getPos().getBlock(position.getX(), position.getY(), position.getZ()));
	}
	
	@Override
	public IBlockState getReplacement(Chunk chunk, BlockPos position, IBlockState previous)
	{
		return this.meltingblock.getDefaultState();
	}
	
	@Override
	public String toString()
	{
		return String.format("MeltingTransformer(%s: %s->%s | %s)", this.meltingblock.getLocalizedName(), this.meltingblock.predecessor.getLocalizedName(), this.meltingblock.successor.getLocalizedName(), super.toString());
	}
}