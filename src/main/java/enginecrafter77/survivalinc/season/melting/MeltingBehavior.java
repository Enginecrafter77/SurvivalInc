package enginecrafter77.survivalinc.season.melting;

import enginecrafter77.survivalinc.block.BlockMelting;
import enginecrafter77.survivalinc.season.SeasonChangedEvent;

import javax.annotation.Nullable;

/**
 * Controls the strategy employed to melt down the blocks
 * created by winter or cold weather, such as snow, ice
 * or another winter-related blocks.
 * @author Enginecrafter77
 */
public enum MeltingBehavior implements MeltingFilterCompiler {
	/**
	 * The fancy controller relies on random update ticks applied to {@link BlockMelting}.
	 * It's chunk filter basically replaces every block of type {@link BlockMelting#freezeTarget}
	 * with the melting block's own type. Then, it does nothing and relies on the BlockMelting
	 * to tick itself.
	 */
	FANCY(MeltingTransformer::new),

	/**
	 * The lazy controller is very similar to the fancy controller with a single exception.
	 * The lazy controller does not rely on random block ticks. In fact, its {@link #requiresRandomTicks()}
	 * method disallows this behavior. Instead, the melting blocks only update their state when
	 * a {@link SeasonChangedEvent} is fired (which is roughly every day). This allows for less-dynamic
	 * but also way less laggy way of melting the {@link BlockMelting meltable} blocks.
	 */
	LAZY(LazyMeltingTransformer::new),

	/**
	 * The simple controller represents the most lightweight form of melting snow. It basically replaces every
	 * block specified by {@link BlockMelting#freezeTarget} with {@link BlockMelting#meltTarget}. For example, snow is immediately
	 * replaced by air, and so on. Not very dramatic, but it can increase performance for very slow servers.
	 */
	SIMPLE(MinimalMeltingTransformer::new),

	/** This melting controller simply disables any sort of melting whatsoever. */
	NONE(null);

	/** A function that should a new chunk filter for the specified melter entry. */
	@Nullable
	private final MeltingTransformerFactory factory;

	private MeltingBehavior(@Nullable MeltingTransformerFactory factory)
	{
		this.factory = factory;
	}

	/**
	 * @return True if {@link BlockMelting melting blocks} should receive random ticks, false otherwise
	 */
	public boolean requiresRandomTicks()
	{
		return this == FANCY;
	}

	@Nullable
	public ChunkFilter compile(MelterEntry entry)
	{
		if(this.factory == null)
			return null;
		return this.factory.create(entry);
	}

	@FunctionalInterface
	private static interface MeltingTransformerFactory
	{
		public MeltingTransformer create(MelterEntry entry);
	}
}
