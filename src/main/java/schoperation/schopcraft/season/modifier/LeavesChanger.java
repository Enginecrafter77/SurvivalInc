package schoperation.schopcraft.season.modifier;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeJungle;
import net.minecraft.world.chunk.Chunk;
import schoperation.schopcraft.lib.ModBlocks;
import schoperation.schopcraft.season.Season;

public class LeavesChanger {
	
	/*
	 * Changes leaves in Autumn and Spring.
	 * 
	 * Oaks will have red leaves and birches will have yellow leaves.
	 * 
	 * The colors revert back to normal in the spring.
	 */
	
	// Goto next chunk
	private Chunk nextChunk(World world, int currentX, int currentZ) {
		
		Chunk chunk = world.getChunkFromChunkCoords(currentX, currentZ);
		
		if (chunk.isLoaded()) {
			
			return chunk;
		}
		
		else {
			
			return null;
		}
	}
	
	// When Autumn or Spring first starts, we're going to go through all chunks around the player, changing the leaves.
	// We'll use the same method as SnowMelter.melt(), except with all rows, not just a random one.
	// This only happens once in each season.
	public void changeInitial(Season season, World world, EntityPlayer player) {
		
		// Is X negative or positive?
		int changeX = 1;
		int posX = player.getPosition().getX();
		
		if (posX < 0) {
			
			changeX = -1;
		}
		
		// Grab first chunk
		Chunk chunk = world.getChunkFromChunkCoords(player.chunkCoordX + (-8 * changeX), player.chunkCoordZ - 8);
		
		// Go up to 16 chunks in that row, 16 rows
		int chunkLimit = 16;
		int chunkNum = 0;
		int rowLimit = 17;
		int rowNum = 0;
		
		// Let's go
		while (rowNum < rowLimit) {
			
			// One row while loop
			while (chunkNum < chunkLimit && chunk != null) {
			
				// Go through the selected chunk
				changeSingleChunk(chunk, world, season);
				
				// Goto next chunk
				chunk = nextChunk(world, chunk.x + changeX, chunk.z);
				
				// Increment chunkcount
				chunkNum++;
			}
			
			// Goto next chunk in next row
			chunk = nextChunk(world, chunk.x + (-16 * changeX), chunk.z + 1);
			
			// Increment rowNum
			rowNum++;
			
			// Reset chunkNum
			chunkNum = 0;
		}
	}
	
	// This method is the actual one that's called continuously in the Autumn.
	public void change(int startX, int startZ, World world, Season season) {
			
		/*
		 * We'll go like this, to avoid wasting precious CPU. Because this'll happen everytime someone enters a chunk.
		 * ^---------------------->
		 * |                      |
		 * |                      |
		 * |          o           |
		 * |                      |
		 * |                      |
		 * |                      |
		 * <----------------------V
		 */
		
		// Starting chunk.
		Chunk chunk = world.getChunkFromChunkCoords(startX - 8, startZ - 8);

		// Northwest corner. Add one to x.
		for (int x = 0; x < 15; x++) {
			
			changeSingleChunk(chunk, world, season);
			chunk = world.getChunkFromChunkCoords(chunk.x + 1, chunk.z);
		}
		
		// Northeast corner. Add one to z.
		for (int z = 0; z < 15; z++) {
			
			chunk = world.getChunkFromChunkCoords(chunk.x, chunk.z + 1);
			changeSingleChunk(chunk, world, season);
		}
		
		// Southeast corner. Subtract one from x.
		for (int x = 0; x < 15; x++) {
			
			chunk = world.getChunkFromChunkCoords(chunk.x - 1, chunk.z);
			changeSingleChunk(chunk, world, season);
		}
		
		// Southwest corner. Subtract one from z.
		for (int z = 0; z < 15; z++) {
			
			chunk = world.getChunkFromChunkCoords(chunk.x, chunk.z - 1);
			changeSingleChunk(chunk, world, season);
		}
	}
	
	// Loops through one chunk, calling the private method below quite a lot.
	private void changeSingleChunk(Chunk chunk, World world, Season season) {
		
		// Starting coords
		int cx = chunk.getPos().getXStart();
		int cz = chunk.getPos().getZStart();
		BlockPos pos = new BlockPos(cx, 64, cz);
		
		// Biome
		Biome biome = chunk.getBiome(pos, world.getBiomeProvider());
		
		// Does this biome allow for changing leaves?
		if (biome instanceof BiomeJungle) {
			
			;
		}
		
		else {
			
			// Iterate through all of the top-blocks and remove any snow and ice.
			for (int x = 0; x < 16; x++) {
				
				for (int z = 0; z < 16; z++) {
					
					// New BlockPos; get the top-most y-value. Then go down one, because precipitation height is one block above the ground.
					pos = chunk.getPrecipitationHeight(pos);
					pos = pos.down();
					
					// Loop through each y-level below until it's not leaves, or the column isn't worth changing.
					for (int y = 0; y < 20; y++) {
						
						// Determine if this block should be replaced.
						boolean success = tryToChangeSingleBlock(world, pos, season);
						
						// Next y-level down, if this succeeded.
						if (success) {
							
							pos = pos.down();
						}
						
						else {
							
							break;
						}
					}
	
					// Goto next z
					pos = pos.south();
				}
				
				// Goto next x
				pos = pos.east();
				pos = pos.north(16);
			}
		}
		
		// Mark chunk dirty just in case
		chunk.markDirty();
	}
		
	// Actually does the logic in replacing a single block.
	private boolean tryToChangeSingleBlock(World world, BlockPos pos, Season season) {
		
		// Blockstate
		IBlockState state = world.getBlockState(pos);
		
		// Is this block a normal leaf block?
		if (state.getBlock() == Blocks.LEAVES) {
			
			// Alright, it's a normal type of leaves (so not acacia or dark oak)
			// Is it Autumn, where they should be colorful?
			if (season == Season.AUTUMN) {
				
				// Okay. This block needs to change. What type of leaves?
				if (state.getBlock() instanceof BlockOldLeaf) {
					
					// Birch leaves? Make them yellow.
					if (state.getValue(BlockOldLeaf.VARIANT) == EnumType.BIRCH && state.getValue(BlockLeaves.DECAYABLE).booleanValue()) {
						
						world.setBlockState(pos, ModBlocks.YELLOW_LEAVES.getDefaultState().withProperty(BlockLeaves.DECAYABLE, true).withProperty(BlockLeaves.CHECK_DECAY, false));
						return true;
					}
					
					// No? Oak leaves? Make them red.
					else if (state.getValue(BlockOldLeaf.VARIANT) == EnumType.OAK && state.getValue(BlockLeaves.DECAYABLE).booleanValue()) {
						
						world.setBlockState(pos, ModBlocks.RED_LEAVES.getDefaultState().withProperty(BlockLeaves.DECAYABLE, true).withProperty(BlockLeaves.CHECK_DECAY, false));
						return true;
					}
					
					// No? Screw it. They're fine.
					else {
						
						return false;
					}
				}
				
				else {
					
					// Ehh no. Not enough metadata. Perhaps with 1.13, when metadata is gonna disappear. Oh dear.
					return false;
				}	
			}
			
			else {
				
				// Leave em.
				return false;
			}
		}
		
		// What about the OTHER type of leaf block (acacia and dark oak)?
		else if (state.getBlock() == Blocks.LEAVES2) {
			
			// Alright, it's the secondary type of leaves.
			// Is it Autumn, where they should be colorful?
			if (season == Season.AUTUMN) {
				
				// Okay. This block needs to change. What type of leaves? Just in case
				if (state.getBlock() instanceof BlockNewLeaf) {
					
					// Dark oak leaves? Make them orange.
					if (state.getValue(BlockNewLeaf.VARIANT) == EnumType.DARK_OAK && state.getValue(BlockLeaves.DECAYABLE).booleanValue()) {
						
						world.setBlockState(pos, ModBlocks.ORANGE_LEAVES.getDefaultState().withProperty(BlockLeaves.DECAYABLE, true).withProperty(BlockLeaves.CHECK_DECAY, false));
						return true;
					}
					
					// No? Screw it. They're fine. Acacia's fine.
					else {
						
						return false;
					}
				}
				
				else {
					
					// Ehh no. Not enough metadata. Perhaps with 1.13, when metadata is gonna disappear. Oh dear.
					return false;
				}	
			}
			
			else {
				
				// Leave em.
				return false;
			}
		}
		
		// No? How about one of the modded leaves? Red?
		else if (state.getBlock() ==  ModBlocks.RED_LEAVES && state.getValue(BlockLeaves.DECAYABLE).booleanValue()) {
			
			// Okay, red leaves. Is it Spring?
			if (season == Season.SPRING) {
				
				// Alrighty. Change them back.
				world.setBlockState(pos, Blocks.LEAVES.getDefaultState().withProperty(BlockLeaves.DECAYABLE, true).withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockOldLeaf.VARIANT, EnumType.OAK));
				return true;
			}
			
			else {
				
				// Don't bother.
				return false;
			}
		}
		
		// Yellow?
		else if (state.getBlock() ==  ModBlocks.YELLOW_LEAVES && state.getValue(BlockLeaves.DECAYABLE).booleanValue()) {
			
			// Okay, yellow leaves. Is it Spring?
			if (season == Season.SPRING) {
				
				// Alrighty. Change them back.
				world.setBlockState(pos, Blocks.LEAVES.getDefaultState().withProperty(BlockLeaves.DECAYABLE, true).withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockOldLeaf.VARIANT, EnumType.BIRCH));
				return true;
			}
			
			else {
				
				// Don't bother.
				return false;
			}
		}
		
		// Orange?
		else if (state.getBlock() ==  ModBlocks.ORANGE_LEAVES && state.getValue(BlockLeaves.DECAYABLE).booleanValue()) {
			
			// Okay, orange leaves. Is it Spring?
			if (season == Season.SPRING) {
				
				// Alrighty. Change them back.
				world.setBlockState(pos, Blocks.LEAVES2.getDefaultState().withProperty(BlockLeaves.DECAYABLE, true).withProperty(BlockLeaves.CHECK_DECAY, false).withProperty(BlockNewLeaf.VARIANT, EnumType.DARK_OAK));
				return true;
			}
			
			else {
				
				// Don't bother.
				return false;
			}
		}
		
		// Air? Continue anyway.
		else if (state.getBlock() == Blocks.AIR) {
			
			return true;
		}
		
		// Not leaves? Screw it.
		else {
			
			return false;
		}
	}
}