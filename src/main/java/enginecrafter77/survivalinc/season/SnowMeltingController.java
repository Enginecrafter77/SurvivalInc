package enginecrafter77.survivalinc.season;

import java.util.Collection;
import java.util.Random;

import enginecrafter77.survivalinc.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public enum SnowMeltingController {
	
	FANCY(new MeltingController() {
		@Override
		public IBlockState applyToBlock(Chunk chunk, BlockPos position, IBlockState source)
		{
			return ModBlocks.MELTING_SNOW.get().getDefaultState();
		}
	}, true),
	SIMPLE(new MeltingController() {
		@Override
		public IBlockState applyToBlock(Chunk chunk, BlockPos position, IBlockState source)
		{
			Random rng = chunk.getWorld().rand;
			return rng.nextBoolean() ? Blocks.AIR.getDefaultState() : source;
		}
	}, false),
	ALL_IN(new MeltingController() {
		@Override
		public IBlockState applyToBlock(Chunk chunk, BlockPos position, IBlockState source)
		{
			return Blocks.AIR.getDefaultState();
		}
	}, true),
	NONE(null, false);
	
	private final MeltingController controller;
	private final boolean advanceonly;
	
	private SnowMeltingController(MeltingController controller, boolean advanceonly)
	{
		this.advanceonly = advanceonly;
		this.controller = controller;
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
			if(state.getBlock() == Blocks.SNOW_LAYER)
			{
				IBlockState target = this.controller.applyToBlock(chunk, position, state);
				if(target != state) chunk.setBlockState(position, target);
			}
		}
	}
	
	@SubscribeEvent
	public void onSeasonUpdate(SeasonUpdateEvent event)
	{
		if(this == NONE) return; // In the NONE mode, we do nothing...
		if(this.advanceonly && !event.hasSeasonAdvanced()) return; // If the season hasn't advanced yet this preset requires it, exit
		
		World world = event.getWorld();
		if(world.isRemote) return; // We don't serve clients here
		
		SeasonData data = SeasonData.load(world);
		
		// Test if the procedure is allowed
		if(data.season != Season.WINTER)
		{
			WorldServer serverworld = (WorldServer)world;
			ChunkProviderServer provider = serverworld.getChunkProvider();
			Collection<Chunk> chunks = provider.getLoadedChunks();
			
			world.profiler.startSection("worldfilter");
			for(Chunk current : chunks)
				this.processChunk(current);
			world.profiler.endSection();
		}
	}
	
	//@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event)
	{
		if(this == NONE) return; // In the NONE mode, we do nothing...
		
		if(event.getWorld().isRemote) return; // We don't serve clients here
		
		SeasonData data = SeasonData.load(event.getWorld());
		if(data.season != Season.WINTER)
		{
			//this.processChunk(event.getChunk());
		}
	}
	
	private static interface MeltingController
	{
		public IBlockState applyToBlock(Chunk chunk, BlockPos position, IBlockState source);
	}
	
}
