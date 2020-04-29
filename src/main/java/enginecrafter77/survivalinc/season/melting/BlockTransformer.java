package enginecrafter77.survivalinc.season.melting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

interface BlockTransformer
{
	public IBlockState applyToBlock(Chunk chunk, BlockPos position, IBlockState source);
}