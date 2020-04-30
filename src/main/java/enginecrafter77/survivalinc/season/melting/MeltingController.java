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

public enum MeltingController {
	
	FANCY((MelterEntry entry) -> new SimpleMeltingTransformer(entry.block, entry.level).setSurfaceRelative(entry.surfaceRelative)),
	LAZY((MelterEntry entry) -> new LazyMeltingTransformer(entry.block, entry.level).setSurfaceRelative(entry.surfaceRelative)),
	SIMPLE((MelterEntry entry) -> new SimpleChunkFilter(entry.block.from, entry.block.to, entry.level).setSurfaceRelative(entry.surfaceRelative)),
	NONE(null);
	
	public static final Set<MelterEntry> meltmap = new HashSet<MelterEntry>();
	private static List<ChunkFilter> transformers;
	
	private final Function<MelterEntry, ChunkFilter> factory;
	
	private MeltingController(Function<MelterEntry, ChunkFilter> factory)
	{
		this.factory = factory;
	}
	
	public boolean isValid()
	{
		return this != NONE;
	}
	
	public boolean allowRandomTicks()
	{
		return this == FANCY;
	}
	
	public static void registerTransformers()
	{
		MeltingController.meltmap.add(new MelterEntry((BlockMelting)ModBlocks.MELTING_SNOW.get(), 0, true)); // 0 + true = precipitation height
	}
	
	public static void compile(MeltingController controller)
	{
		MeltingController.transformers = new LinkedList<ChunkFilter>();
		for(MelterEntry entry : MeltingController.meltmap)
		{
			MeltingController.transformers.add(controller.factory.apply(entry));
		}
	}
	
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
