package enginecrafter77.survivalinc.season.melting;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import enginecrafter77.survivalinc.ModBlocks;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.block.BlockMelting;
import enginecrafter77.survivalinc.season.Season;
import enginecrafter77.survivalinc.season.SeasonData;
import enginecrafter77.survivalinc.season.SeasonUpdateEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Controls the strategy employed to melt down the blocks
 * created by winter or cold weather, such as snow, ice
 * or another winter-related blocks.
 * @author Enginecrafter77
 */
public enum MeltingController {
	
	/**
	 * The fancy controller relies on random update ticks applied to {@link BlockMelting}.
	 * It's chunk filter basically replaces every block of type {@link BlockMelting#from}
	 * with the melting block's own type. Then, it does nothing and relies on the BlockMelting
	 * to tick itself.
	 */
	FANCY((MelterEntry entry) -> new SimpleMeltingTransformer(entry.block, entry.level).setSurfaceRelative(entry.surfaceRelative)),
	
	/**
	 * The lazy controller is very similar to the fancy controller with a single exception.
	 * The lazy controller does not rely on random block ticks. In fact, it's {@link #allowRandomTicks()}
	 * method disallows this behavior. Instead, the melting blocks only update their state when
	 * a {@link SeasonUpdateEvent} is fired (which is roughly every day). This allows for less-dynamic
	 * but also way less laggy way of melting the meltable blocks.
	 */
	LAZY((MelterEntry entry) -> new LazyMeltingTransformer(entry.block, entry.level).setSurfaceRelative(entry.surfaceRelative)),
	
	/**
	 * The simple controller represents the most lightweight form of melting snow. It basically replaces every
	 * block specified by {@link BlockMelting#from} with {@link BlockMelting#to}. For example, snow is immediately
	 * replaced by air, and so on. Not very dramatic, but it can increase performance for very slow servers.
	 */
	SIMPLE((MelterEntry entry) -> new SimpleChunkFilter(entry.block.from, entry.block.to, entry.level).setSurfaceRelative(entry.surfaceRelative)),
	
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
	private static List<ChunkFilter> transformers;
	
	/**
	 * A function that should a new chunk filter for the specified melter entry.
	 */
	private final Function<MelterEntry, ChunkFilter> factory;
	
	private MeltingController(Function<MelterEntry, ChunkFilter> factory)
	{
		this.factory = factory;
	}
	
	/**
	 * @return True if this controller is a valid controller (i.e. guaranteed to NOT cause NPE when run)
	 */
	public boolean isValid()
	{
		return this != NONE;
	}
	
	/**
	 * @return True if {@link BlockMelting melting blocks} should receive random ticks, false otherwise
	 */
	public boolean allowRandomTicks()
	{
		return this == FANCY;
	}
	
	/** Registers transformers used by Survival Inc. */
	public static void registerTransformers()
	{
		MeltingController.meltmap.add(new MelterEntry((BlockMelting)ModBlocks.MELTING_SNOW.get(), 0, true)); // 0 + true = precipitation height
		MeltingController.meltmap.add(new MelterEntry((BlockMelting)ModBlocks.MELTING_ICE.get(), -1, true)); // -1 + true = ground
	}
	
	/**
	 * Compiles the {@link #transformers} list from the values
	 * of {@link #meltmap}. This list is further used by all
	 * chunk filtering operations.
	 * @param controller The controller used to compile the transformers list
	 */
	public static void compile(MeltingController controller)
	{
		if(MeltingController.transformers == null) MeltingController.transformers = new LinkedList<ChunkFilter>();
		else MeltingController.transformers.clear();
		
		for(MelterEntry entry : MeltingController.meltmap)
		{
			MeltingController.transformers.add(controller.factory.apply(entry));
		}
	}
	
	/**
	 * Applies the list of transformers to all blocks
	 * contained within the chunk. In general, this
	 * involves all blocks along X and Z axis, and usually
	 * only a few Y levels. The amount of processed
	 * blocks is therefore <tt>Y*256</tt> blocks.
	 * @param chunk The chunk to be processed.
	 */
	public static void processChunk(Chunk chunk)
	{
		BlockPos begin = new BlockPos(0, 0, 0);
		BlockPos end = new BlockPos(15, 0, 15);
		
		Iterable<BlockPos.MutableBlockPos> itr = BlockPos.getAllInBoxMutable(begin, end);
		
		for(BlockPos.MutableBlockPos base : itr)
		{
			for(ChunkFilter transformer : MeltingController.transformers)
			{				
				BlockPos position = transformer.offsetPosition(chunk, base);
				transformer.applyToChunk(chunk, position);
			}
		}
	}
	
	@SubscribeEvent
	public static void onSeasonUpdate(SeasonUpdateEvent event)
	{
		World world = event.getWorld();
		if(world.isRemote) return; // We don't serve clients here
		
		WorldServer serverworld = (WorldServer)world;
		ChunkProviderServer provider = serverworld.getChunkProvider();
		Collection<Chunk> chunks = provider.getLoadedChunks();
		
		SurvivalInc.logger.info("Preparing to process {} chunks...", chunks.size());
		long time = System.currentTimeMillis();
		for(Chunk current : chunks)
			MeltingController.processChunk(current);
		time = System.currentTimeMillis() - time;
		SurvivalInc.logger.info("Processed {} chunks in {} ms", chunks.size(), time);
	}
	
	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event)
	{		
		if(event.getWorld().isRemote) return; // We don't serve clients here
		
		SeasonData data = SeasonData.load(event.getWorld());
		if(data.season != Season.WINTER)
		{
			MeltingController.processChunk(event.getChunk());
		}
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
		public final boolean surfaceRelative;
		public final BlockMelting block;
		public final int level;
		
		public MelterEntry(BlockMelting block, int level, boolean surfaceRelative)
		{
			this.surfaceRelative = surfaceRelative;
			this.block = block;
			this.level = level;
		}
	}
	
}
