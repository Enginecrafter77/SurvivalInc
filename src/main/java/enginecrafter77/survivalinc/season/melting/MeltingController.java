package enginecrafter77.survivalinc.season.melting;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableSet;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.block.BlockMelting;
import enginecrafter77.survivalinc.season.SeasonChangedEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.state.IBlockState;

/**
 * Controls the strategy employed to melt down the blocks
 * created by winter or cold weather, such as snow, ice
 * or another winter-related blocks.
 * @author Enginecrafter77
 */
public enum MeltingController {
	
	/**
	 * The fancy controller relies on random update ticks applied to {@link BlockMelting}.
	 * It's chunk filter basically replaces every block of type {@link BlockMelting#predecessor}
	 * with the melting block's own type. Then, it does nothing and relies on the BlockMelting
	 * to tick itself.
	 */
	FANCY((BlockMelting block) -> new MeltingTransformer(block)),
	
	/**
	 * The lazy controller is very similar to the fancy controller with a single exception.
	 * The lazy controller does not rely on random block ticks. In fact, it's {@link #allowRandomTicks()}
	 * method disallows this behavior. Instead, the melting blocks only update their state when
	 * a {@link SeasonUpdateEvent} is fired (which is roughly every day). This allows for less-dynamic
	 * but also way less laggy way of melting the meltable blocks.
	 */
	LAZY((BlockMelting block) -> new LazyMeltingTransformer(block)),
	
	/**
	 * The simple controller represents the most lightweight form of melting snow. It basically replaces every
	 * block specified by {@link BlockMelting#predecessor} with {@link BlockMelting#successor}. For example, snow is immediately
	 * replaced by air, and so on. Not very dramatic, but it can increase performance for very slow servers.
	 */
	SIMPLE((BlockMelting block) -> new MeltingTransformer(block) {
		@Override
		public IBlockState getReplacement(Chunk chunk, BlockPos position, IBlockState previous)
		{
			return this.meltingblock.successor.getDefaultState();
		}
	}),
	
	/** This melting controller simply disables any sort of melting whatsoever. */
	NONE(null);
	
	/**
	 * A set of {@link MelterEntry}, which allows defining different
	 * melting transformations that should occur.
	 */
	public static final Set<MelterEntry> meltmap = new HashSet<MelterEntry>();
	
	/**
	 * A compiled list of ChunkFilters generated by applying
	 * the {@link #factory} function on all of the {@link #meltmap}
	 * entries.
	 */
	private static List<ChunkFilter> transformers = new LinkedList<ChunkFilter>();
	
	/**
	 * A function that should a new chunk filter for the specified melter entry.
	 */
	private final Function<BlockMelting, MeltingTransformer> factory;
	
	private MeltingController(Function<BlockMelting, MeltingTransformer> factory)
	{
		this.factory = factory;
	}
	
	/**
	 * @return True if this controller is a valid controller (i.e. guaranteed to NOT cause NPE when run)
	 */
	public boolean isValid()
	{
		return this.factory != null;
	}
	
	/**
	 * @return True if {@link BlockMelting melting blocks} should receive random ticks, false otherwise
	 */
	public boolean allowRandomTicks()
	{
		return this == FANCY;
	}
	
	/**
	 * Compiles the {@link #transformers} list from the values
	 * of {@link #meltmap}. This list is further used by all
	 * chunk filtering operations.
	 * @param controller The controller used to compile the transformers list
	 */
	public static void compile(MeltingController controller)
	{
		if(!controller.isValid()) throw new UnsupportedOperationException("Controller " + controller.name() + " is not capable of compilation!");
		
		for(MelterEntry entry : MeltingController.meltmap)
		{
			MeltingTransformer transformer = controller.factory.apply(entry.block);
			for(Map.Entry<Integer, Boolean> levelentry : entry.levelmap.entrySet())
			{
				int level = levelentry.getKey();
				if(levelentry.getValue())
					level = transformer.surfaceOffsetToLayer(level);
				transformer.addLayer(level);
			}
			MeltingController.transformers.add(transformer);
		}
	}
	
	/**
	 * Registers a custom transformer to the global list.
	 * Use with caution.
	 * @param filter
	 */
	public static void registerCustomTransformer(ChunkFilter filter)
	{
		MeltingController.transformers.add(filter);
	}
	
	/**
	 * Applies the list of transformers to all blocks
	 * contained within the chunk. In general, this
	 * involves all blocks along X and Z axis, and usually
	 * only a few Y levels. The amount of processed
	 * blocks is therefore <tt>Y*256</tt> blocks.
	 * @param chunk The chunk to be processed.
	 */
	public static void processChunks(Collection<Chunk> chunks)
	{
		for(ChunkFilter transformer : MeltingController.transformers)
		{
			long time = System.nanoTime();
			transformer.processChunks(chunks);
			time = System.nanoTime() - time;
			//SurvivalInc.logger.debug("Processing {} chunks using transformer {} took {} ns", chunks.size(), transformer.toString(), time);
		}
		
	}
	
	@SubscribeEvent
	public static void onSeasonUpdate(SeasonChangedEvent event)
	{
		World world = event.getWorld();
		if(world.isRemote) return; // Avoid running on client
		
		WorldServer serverworld = (WorldServer)world;
		ChunkProviderServer provider = serverworld.getChunkProvider();
		Collection<Chunk> chunks = provider.getLoadedChunks();
		
		SurvivalInc.logger.info("Preparing to process {} chunks...", chunks.size());
		long overall_time = System.currentTimeMillis();
		MeltingController.processChunks(chunks);
		overall_time = System.currentTimeMillis() - overall_time;
		SurvivalInc.logger.info("Chunk processing done using all transformers in {} ms", overall_time);
	}
	
	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event)
	{
		World world = event.getWorld();
		// Avoid running on client and outside overworld
		if(world.isRemote || world.provider.getDimensionType() != DimensionType.OVERWORLD) return;
		MeltingController.processChunks(ImmutableSet.of(event.getChunk()));
	}
	
	/**
	 * Describes an entry used in the compilation
	 * of the transformer list. It serves as a base
	 * for specifying the necessary information used
	 * to initialize a new chunk filter for every
	 * entry specified by this class.
	 * @author Enginecrafter77
	 */
	public static class MelterEntry
	{
		public final Map<Integer, Boolean> levelmap;
		public final BlockMelting block;
		
		public MelterEntry(BlockMelting block)
		{
			this.levelmap = new HashMap<Integer, Boolean>();
			this.block = block;
		}
		
		public MelterEntry level(int level, boolean surface)
		{
			this.levelmap.put(level, surface);
			return this;
		}
	}
}
