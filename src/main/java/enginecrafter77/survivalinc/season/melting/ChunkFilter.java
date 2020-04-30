package enginecrafter77.survivalinc.season.melting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public interface ChunkFilter
{
	public BlockPos offsetPosition(Chunk chunk, BlockPos position);
	public void applyToChunk(Chunk chunk, BlockPos position);
}