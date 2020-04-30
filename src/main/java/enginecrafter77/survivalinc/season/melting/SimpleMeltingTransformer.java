package enginecrafter77.survivalinc.season.melting;

import enginecrafter77.survivalinc.block.BlockMelting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

/**
 * SimpleMeltingTransformer is a simple extension to {@link SimpleChunkFilter},
 * which additionally checks for whether it's freezing or not at the target
 * position. If it's not, it replaces the target block (pulled from {@link BlockMelting}
 * instance) with the aforementioned melting block.
 * @author Enginecrafter77
 */
public class SimpleMeltingTransformer extends SimpleChunkFilter {	
	public SimpleMeltingTransformer(BlockMelting block, int verticalOffset)
	{
		super(block.from, block, verticalOffset);
	}

	@Override
	public boolean isSubstitutionViable(Chunk chunk, BlockPos position, IBlockState state)
	{
		return super.isSubstitutionViable(chunk, position, state) && !BlockMelting.isFreezingAt(chunk, position);
	}
}