package enginecrafter77.survivalinc.season.melting;

import com.google.common.base.Stopwatch;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.season.SeasonChangedEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MeltingController {
	public final Set<MelterEntry> meltmap;
	private final Collection<ChunkFilter> customFilters;

	@Nullable
	private MeltingFilterCompiler compiler;

	@Nullable
	private Collection<ChunkFilter> compiledTransformers;

	public MeltingController()
	{
		this.customFilters = new ArrayList<ChunkFilter>();
		this.meltmap = new HashSet<MelterEntry>();
		this.compiledTransformers = null;
		this.compiler = null;
	}

	public void clearMeltingMap()
	{
		this.meltmap.clear();

		if(this.compiledTransformers != null)
			this.compiledTransformers.clear();
	}

	public void registerMelterEntry(MelterEntry entry)
	{
		this.meltmap.add(entry);

		if(this.compiler != null && this.compiledTransformers != null)
			this.compiledTransformers.add(this.compiler.compile(entry));
	}

	public void registerMelterEntries(Collection<MelterEntry> entries)
	{
		this.meltmap.addAll(entries);

		if(this.compiler != null && this.compiledTransformers != null)
			entries.stream().map(this.compiler::compile).forEach(this.compiledTransformers::add);
	}

	public void setCompiler(MeltingFilterCompiler compiler)
	{
		if(compiler != this.compiler)
		{
			this.compiler = compiler;
			this.compiledTransformers = this.meltmap.parallelStream().map(compiler::compile).collect(Collectors.toList());
		}
	}
	
	public void registerCustomTransformer(ChunkFilter filter)
	{
		this.customFilters.add(filter);
	}
	
	/**
	 * Applies the list of transformers to all blocks
	 * contained within the chunks. In general, this
	 * involves all blocks along X and Z axis, and usually
	 * only a few Y levels. The amount of processed
	 * blocks is therefore <tt>N*Y*256</tt> blocks, where
	 * N is the size of the provided collection.
	 * @param chunk The chunk to process
	 */
	public void processChunk(Chunk chunk)
	{
		for(ChunkFilter custom : this.customFilters)
			custom.processChunk(chunk);

		if(this.compiledTransformers != null)
		{
			for(ChunkFilter filter : this.compiledTransformers)
				filter.processChunk(chunk);
		}
	}
	
	@SubscribeEvent
	public void onSeasonUpdate(SeasonChangedEvent event)
	{
		World world = event.getWorld();
		if(world.isRemote)
			return; // Avoid running on client
		
		WorldServer serverworld = (WorldServer)world;
		ChunkProviderServer provider = serverworld.getChunkProvider();
		Collection<Chunk> chunks = provider.getLoadedChunks();
		
		SurvivalInc.logger.info("Preparing to process {} chunks...", chunks.size());
		Stopwatch sw = Stopwatch.createStarted();
		serverworld.profiler.startSection("chunktransformer");
		chunks.forEach(this::processChunk);
		serverworld.profiler.endSection();
		sw.stop();
		SurvivalInc.logger.info("Chunk processing done using all transformers in {} ms", sw.elapsed(TimeUnit.MILLISECONDS));
	}
	
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event)
	{
		World world = event.getWorld();
		// Avoid running on client and outside overworld
		if(world.isRemote || world.provider.getDimensionType() != DimensionType.OVERWORLD)
			return;
		this.processChunk(event.getChunk());
	}

}
