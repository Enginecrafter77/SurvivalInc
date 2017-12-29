package net.schoperation.schopcraft.season;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class SnowMelter {
	
	/*
	 * All about melting that ice and snow after winter has done its messy work.
	 */
	
	// We'll only go in one direction per tick, to reduce load.
	/*        
	 *  17 ------------------------>  |
	 *  16 ------------------------>  |
	 *  15 ------------------------>  | positive Z
	 *  14 ------------------------>  |
	 *  13 ------------------------>  V
	 *  12 ------------------------>
	 *  11 ------------------------>
	 *  10 ------------------------>  
	 *  0 --------player---------->
	 *  1 ------------------------>
	 *  2 ------------------------>
	 *  3 ------------------------>
	 *  4 ------------------------>  
	 *  5 ------------------------>  
	 *  6 ------------------------>
	 *  7 ------------------------>
	 *  8 ------------------------>
	 * 
	 * Each row is 16 chunks.
	 */
	
	// Yay pseudorandom
	private Random rand = new Random();
	
	// What chunks to go through?
	private int setOffset() {
		
		// Offset
		int offsetZ = 0;
		
		// Grab a direction ID
		int rowId = rand.nextInt(18);
		
		// Now set offset based on id
		if (rowId < 9) {
			
			offsetZ = rowId % 9;
		}
		
		else if (rowId > 9) {
			
			offsetZ = (rowId % 9) * -1;
		}
		
		else {
			
			offsetZ = 0;
		}
		
		return offsetZ;
	}
	
	private Chunk nextChunk(World world, int currentX, int offsetZ) {
		
		Chunk chunk = world.getChunkFromChunkCoords(currentX, offsetZ);
		
		if (chunk.isLoaded()) {
			
			return chunk;
		}
		
		else {
			
			return null;
		}
	}
	
	// The actual method that melts the snow and ice. SPRING ONLY
	public void melt(World world, EntityPlayer player, Season season, int daysIntoSeason) {
		
		// Grab a random z offset
		int offsetZ = setOffset();
		
		// Is X negative or positive?
		int changeX = 1;
		int posX = player.getPosition().getX();
		
		if (posX < 0) {
			
			changeX = -1;
		}
		
		// Grab first chunk
		Chunk chunk = world.getChunkFromChunkCoords(player.chunkCoordX + (-8 * changeX), player.chunkCoordZ + offsetZ);
		
		// Go up to 16 chunks in that row
		int chunkLimit = 16;
		int chunkNum = 0;
		
		// Lets go
		while (chunkNum < chunkLimit && chunk != null) {
			
			// There will be a random chance to remove a snow layer or ice block in a given chunk.
			// If lady luck happens to be on this chunk's side, we'll choose a random block to remove snow/ice from.
			// Also, if the biome's temp is still low enough to have snow, don't bother to do this.
			float threshold = 0.1f;
			if (season == Season.SPRING) { 
				
				threshold = 0.1f * (daysIntoSeason + 1); 
			}

			if (world.isRaining()) { 
				
				threshold = threshold * 2f; 
			} 
			
			// Are we gonna try to remove something?
			if (rand.nextFloat() < threshold) {
				
				// Alright, time to pick a location.
				// Starting coordinates
				int cx = chunk.getPos().getXStart();
				int cz = chunk.getPos().getZStart();
				
				// Counter
				int counter = 0;
				int counterTarget = 2 * (daysIntoSeason + 1);
				
				// We'll try <counterTarget> random locations before moving on to the next chunk.
				while (counter < counterTarget) {
					
					// Random offsets
					int xOffset = rand.nextInt(16);
					int zOffset = rand.nextInt(16);
					
					// The Blockpos
					BlockPos pos = new BlockPos(cx + xOffset, 0, cz + zOffset);
					pos = chunk.getPrecipitationHeight(pos);
					
					// Is this the right temperature? If not, screw all this.
					if (chunk.getBiome(pos, world.getBiomeProvider()).getDefaultTemperature() <= 0.15f) {
						
						counter = counterTarget;
					}
					
					else {
						
						// Is this a snow layer?
						if (world.getBlockState(pos).getBlock() == Blocks.SNOW_LAYER) {
							
							// Remove it.
							world.setBlockToAir(pos);
							counter = counterTarget;
						}
						
						// How about an ice block? This'll be one block down.
						else if (world.getBlockState(pos.down()).getBlock() == Blocks.ICE) {
							
							// Remove it.
							world.setBlockState(pos.down(), Blocks.WATER.getDefaultState());
							counter = counterTarget;
						}
						
						else {
							
							counter++;
						}
					}
				}
			}
			
			// Mark chunk dirty just in case
			chunk.markDirty();
			
			// Goto next chunk
			chunk = nextChunk(world, chunk.x + changeX, chunk.z);
			
			// Increment chunkcount
			chunkNum++;
		}
	}
	
	// Used in summer. By now, any non-snow biome shouldn't have snow.
}