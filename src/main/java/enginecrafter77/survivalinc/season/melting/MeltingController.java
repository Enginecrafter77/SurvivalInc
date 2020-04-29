package enginecrafter77.survivalinc.season.melting;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import enginecrafter77.survivalinc.ModBlocks;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.block.BlockMelting;
import enginecrafter77.survivalinc.season.Season;
import enginecrafter77.survivalinc.season.SeasonData;
import enginecrafter77.survivalinc.season.SeasonUpdateEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public enum MeltingController {
	
	FANCY(true),
	LAZY(false),
	SIMPLE(false),
	BURST(true),
	NONE(false);
	
	private final List<BlockTransformer> transformers;
	private final boolean advanceonly;
	
	private MeltingController(boolean advanceonly)
	{
		this.transformers = new LinkedList<BlockTransformer>();
		this.advanceonly = advanceonly;
	}
	
	public static void registerTransformers()
	{
		MeltingController.registerTransformation(Blocks.SNOW_LAYER, (BlockMelting)ModBlocks.MELTING_SNOW.get(), (BlockMelting)ModBlocks.LAZY_MELTING_SNOW.get());
	}
	
	public static void registerTransformation(Block from, BlockMelting active, BlockMelting lazy)
	{
		MeltingController.FANCY.registerTransformer(new SimpleMeltingTransformer(from, active));
		MeltingController.LAZY.registerTransformer(new SimpleMeltingTransformer(from, lazy));
		MeltingController.SIMPLE.registerTransformer(new SimpleMeltingTransformer(from, active.to, 0.9F));
		MeltingController.BURST.registerTransformer(new SimpleMeltingTransformer(from, active.to));
		
		MeltingController.LAZY.registerTransformer(new BlockTransformer() {
			@Override
			public IBlockState applyToBlock(Chunk chunk, BlockPos position, IBlockState source)
			{
				Block cblock = source.getBlock();
				World world = chunk.getWorld();
				Random rng = world.rand;
				if(cblock == lazy && rng.nextFloat() < 0.9F) // 90% chance to trigger melting
				{
					cblock.updateTick(world, chunk.getPos().getBlock(position.getX(), position.getY(), position.getZ()), source, rng);
					chunk.markDirty();
				}
				return source;
			}
		});
	}
	
	public void registerTransformer(BlockTransformer transformer)
	{
		this.transformers.add(transformer);
	}
	
	public void processChunk(Chunk chunk)
	{
		BlockPos begin = new BlockPos(0, 0, 0);
		BlockPos end = new BlockPos(15, 0, 15);
		
		Iterable<BlockPos.MutableBlockPos> itr = BlockPos.getAllInBoxMutable(begin, end);
		
		for(BlockPos.MutableBlockPos base : itr)
		{
			BlockPos position = chunk.getPrecipitationHeight(base);
			IBlockState state = chunk.getBlockState(position);
			IBlockState target = state;
			for(BlockTransformer transformer : this.transformers)
				target = transformer.applyToBlock(chunk, position, target);
			if(state != target) chunk.setBlockState(position, target);
		}
	}
	
	@SubscribeEvent
	public void onSeasonUpdate(SeasonUpdateEvent event)
	{
		if(this == NONE) return; // In the NONE mode, we do nothing...
		if(this.advanceonly && !event.hasSeasonAdvanced()) return; // If the season hasn't advanced yet this preset requires it, exit
		
		World world = event.getWorld();
		if(world.isRemote) return; // We don't serve clients here
		
		WorldServer serverworld = (WorldServer)world;
		ChunkProviderServer provider = serverworld.getChunkProvider();
		Collection<Chunk> chunks = provider.getLoadedChunks();
		
		SurvivalInc.logger.info("Preparing to process {} chunks...", chunks.size());
		long time = System.currentTimeMillis();
		for(Chunk current : chunks)
			this.processChunk(current);
		time = System.currentTimeMillis() - time;
		SurvivalInc.logger.info("Processed {} chunks in {} ms", chunks.size(), time);
	}
	
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event)
	{
		if(this == NONE) return; // In the NONE mode, we do nothing...
		
		if(event.getWorld().isRemote) return; // We don't serve clients here
		
		SeasonData data = SeasonData.load(event.getWorld());
		if(data.season != Season.WINTER)
		{
			this.processChunk(event.getChunk());
		}
	}
	
}
