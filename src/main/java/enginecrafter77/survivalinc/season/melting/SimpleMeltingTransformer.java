package enginecrafter77.survivalinc.season.melting;

import enginecrafter77.survivalinc.block.BlockMelting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

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