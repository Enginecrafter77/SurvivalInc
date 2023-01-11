package enginecrafter77.survivalinc.season.melting;

import com.google.common.collect.ImmutableList;
import enginecrafter77.survivalinc.block.BlockMelting;

import java.util.Collection;

/**
 * Describes an entry used in the compilation
 * of the transformer list. It serves as a base
 * for specifying the necessary information used
 * to initialize a new chunk filter for every
 * entry specified by this class.
 *
 * @author Enginecrafter77
 */
public class MelterEntry {
	private final Collection<BlockPositionLevelMapper> levelmap;
	private final BlockMelting block;

	protected MelterEntry(BlockMelting block, Collection<BlockPositionLevelMapper> levelmap)
	{
		this.levelmap = levelmap;
		this.block = block;
	}

	public BlockMelting getBlock()
	{
		return this.block;
	}

	public Collection<BlockPositionLevelMapper> getLevelMap()
	{
		return this.levelmap;
	}

	public static MelterEntryBuilder with(BlockMelting block)
	{
		return new MelterEntryBuilder(block);
	}

	public static class MelterEntryBuilder
	{
		private final BlockMelting block;
		private final ImmutableList.Builder<BlockPositionLevelMapper> levelmap;

		public MelterEntryBuilder(BlockMelting block)
		{
			this.levelmap = ImmutableList.builder();
			this.block = block;
		}

		public MelterEntryBuilder onSurface(int offset)
		{
			this.levelmap.add(new SurfaceLevelMapper(offset));
			return this;
		}

		public MelterEntryBuilder onLevel(int level)
		{
			this.levelmap.add(new AbsoluteLevelMapper(level));
			return this;
		}

		public MelterEntryBuilder on(BlockPositionLevelMapper mapper)
		{
			this.levelmap.add(mapper);
			return this;
		}

		public MelterEntry build()
		{
			return new MelterEntry(this.block, this.levelmap.build());
		}
	}
}
