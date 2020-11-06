package enginecrafter77.survivalinc.season.melting;

import enginecrafter77.survivalinc.block.BlockMelting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class MinimalMeltingTransformer extends MeltingTransformer {

	public MinimalMeltingTransformer(BlockMelting block)
	{
		super(block);
	}
	
	@Override
	public boolean shouldBlockBeTransformed(Chunk chunk, BlockPos position, IBlockState state)
	{
		return state.getBlock() instanceof BlockMelting || super.shouldBlockBeTransformed(chunk, position, state);
	}
	
	@Override
	public IBlockState transform(Chunk chunk, BlockPos position, IBlockState previous)
	{
		return this.meltingblock.meltTarget.getDefaultState();
	}

}
