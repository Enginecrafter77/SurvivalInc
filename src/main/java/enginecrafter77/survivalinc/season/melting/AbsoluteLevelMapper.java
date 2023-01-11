package enginecrafter77.survivalinc.season.melting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class AbsoluteLevelMapper implements BlockPositionLevelMapper {
	private final int level;

	public AbsoluteLevelMapper(int level)
	{
		this.level = level;
	}

	@Override
	public int getLevel(Chunk chunk, BlockPos position)
	{
		return this.level;
	}

	@Override
	public String toString()
	{
		return String.format("L:%d", this.level);
	}
}
