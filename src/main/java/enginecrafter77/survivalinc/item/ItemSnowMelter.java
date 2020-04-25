package enginecrafter77.survivalinc.item;

import java.util.Collection;

import enginecrafter77.survivalinc.ModBlocks;
import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

public class ItemSnowMelter extends Item {
	
	public ItemSnowMelter()
	{
		this.setRegistryName(new ResourceLocation(SurvivalInc.MOD_ID, "snowmelter"));
		this.setCreativeTab(SurvivalInc.mainTab);
		this.setTranslationKey("snowmelter");
		this.setMaxStackSize(1);//ChunkProviderServer
	}
	
	public void meltSnowInChunk(Chunk chunk)
	{
		BlockPos begin = new BlockPos(0, 0, 0);
		BlockPos end = new BlockPos(15, 0, 15);
		
		Iterable<BlockPos.MutableBlockPos> itr = BlockPos.getAllInBoxMutable(begin, end);
		
		//int count = 0;
		//long time = System.currentTimeMillis();
		for(BlockPos base : itr)
		{
			Biome biome = chunk.getBiome(base, chunk.getWorld().getBiomeProvider());
			if(!biome.isSnowyBiome()) // We don't want to destroy that pretty permanently snowy biomes
			{
				BlockPos position = chunk.getPrecipitationHeight(base);			
				IBlockState state = chunk.getBlockState(position);
				if(state.getBlock() == Blocks.SNOW_LAYER)
				{
					chunk.setBlockState(position, ModBlocks.MELTING_SNOW.get().getDefaultState());
					//SurvivalInc.logger.info("Converted block at {} to melting snow", position);
				}
			}
			//count++;
		}
		//time = System.currentTimeMillis() - time;
		//SurvivalInc.logger.info("Iterated {} blocks. Took {} ms.", count, time);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		if(!world.isRemote)
		{
			WorldServer server = (WorldServer)world;
			ChunkProviderServer chunkprovider = server.getChunkProvider();
			
			Collection<Chunk> loadedchunks = chunkprovider.getLoadedChunks();
			
			long time = System.currentTimeMillis();
			for(Chunk c : loadedchunks)
			{
				SurvivalInc.logger.info("Processing chunk at {}/{}", c.x, c.z);
				this.meltSnowInChunk(c);
			}
			time = System.currentTimeMillis() - time;
			SurvivalInc.logger.info("Iterated {} chunks. Took {} ms.", loadedchunks.size(), time);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
	
}
