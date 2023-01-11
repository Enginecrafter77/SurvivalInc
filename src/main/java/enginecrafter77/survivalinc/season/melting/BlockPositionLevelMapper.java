package enginecrafter77.survivalinc.season.melting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public interface BlockPositionLevelMapper {
	public int getLevel(Chunk chunk, BlockPos position);

	public default BlockPos transformBlockPosInChunk(Chunk chunk, BlockPos position)
	{
		if(position instanceof BlockPos.MutableBlockPos)
		{
			BlockPos.MutableBlockPos mbps = (BlockPos.MutableBlockPos)position;
			mbps.setY(this.getLevel(chunk, mbps));
			return mbps;
		}
		return new BlockPos(position.getX(), this.getLevel(chunk, position), position.getZ());
	}
}
