package enginecrafter77.survivalinc.season.melting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class SurfaceLevelMapper implements BlockPositionLevelMapper {
	private final int offset;

	public SurfaceLevelMapper(int offset)
	{
		this.offset = offset;
	}

	@Override
	public int getLevel(Chunk chunk, BlockPos position)
	{
		return chunk.getPrecipitationHeight(position).getY() - 1 + this.offset;
	}

	@Override
	public String toString()
	{
		return String.format("L:S%+d", this.offset);
	}
}
