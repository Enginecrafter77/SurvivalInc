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
	
	// We'll only go in one direction per tick, to reduce load. These variables help determine that.
	/*          5  6  7
	 *           \ | /
	 * 			  \|/
	 *       4 --------- 0
	 *            /|\
	 *           / | \
	 *          3  2  1
	 */
	private static Random rand = new Random();
	private static int directionId = 0;
	private static int changeX = 1;
	private static int changeZ = 0;
	
	// Change directions
	private static void changeDirections() {
		
		// Grab a direction ID
		directionId = rand.nextInt(8);
		
		// Now set directions based on id
		switch(directionId) {
		
			case 0:
				changeX = 1;
				changeZ = 0;
				break;
			case 1:
				changeX = 1;
				changeZ = 1;
				break;
			case 2:
				changeX = 0;
				changeZ = 1;
				break;
			case 3:
				changeX = -1;
				changeZ = 1;
				break;
			case 4:
				changeX = -1;
				changeZ = 0;
				break;
			case 5:
				changeX = -1;
				changeZ = -1;
				break;
			case 6:
				changeX = 0;
				changeZ = -1;
				break;
			case 7:
				changeX = 1;
				changeZ = -1;
				break;
			default:
				changeX = 1;
				changeZ = 0;
				break;
		}
	}
	
	private static Chunk nextChunk(World world, int currentX, int currentZ) {
		
		Chunk chunk = world.getChunkFromChunkCoords(currentX + changeX, currentZ + changeZ);
		
		if (chunk.isLoaded()) {
			
			return chunk;
		}
		
		else {
			
			return null;
		}
	}
	
	// The actual method that melts the snow and ice
	public static void melt(World world, EntityPlayer player, Season season, int daysIntoSeason) {
		
		// Change directions
		changeDirections();
		
		// Grab a chunk
		Chunk chunk = world.getChunkFromChunkCoords(player.chunkCoordX, player.chunkCoordZ);
		
		// Go up to 8 chunks
		int chunkLimit = 8;
		int chunkCount = 0;
		
		// Lets go
		while (chunkCount < chunkLimit && chunk != null) {
			
			// There will be a random chance to remove a snow layer or ice block in a given chunk.
			// If lady luck happens to be on this chunk's side, we'll choose a random block to remove snow/ice from.
			// Also, if the biome's temp is still low enough to have snow, don't bother to do this.
			float threshold = 0.1f;
			if (season == Season.SPRING) { threshold = 0.1f * (daysIntoSeason + 1); }
			else if (season == Season.SUMMER) { threshold = 1.0f; } // get the damn snow outta here
			if (world.isRaining()) { threshold += 0.1f; } 
			
			// Are we gonna try to remove something?
			if (rand.nextFloat() < threshold) {
				
				// Alright, time to pick a location.
				// Starting coordinates
				int cx = chunk.getPos().getXStart();
				int cz = chunk.getPos().getZStart();
				
				// Counter
				int counter = 0;
				
				// We'll try 4 random locations before moving on to the next chunk.
				while (counter < 4) {
					
					// Random offsets
					int xOffset = rand.nextInt(16);
					int zOffset = rand.nextInt(16);
					
					// If negative, make offset negative.
					if (cx < 0) {
						
						xOffset = xOffset * -1;
					}
					
					if (cz < 0) {
						
						zOffset = zOffset * -1;
					}
					
					// The Blockpos
					BlockPos pos = new BlockPos(cx + xOffset, 0, cz + zOffset);
					pos = chunk.getPrecipitationHeight(pos);
					
					// Is this the right temperature? If not, screw all this.
					if (chunk.getBiome(pos, world.getBiomeProvider()).getDefaultTemperature() <= 0.15f) {
						
						counter = 4;
					}
					
					else {
						
						// Is this a snow layer?
						if (world.getBlockState(pos).getBlock() == Blocks.SNOW_LAYER) {
							
							// Remove it.
							world.setBlockToAir(pos);
							counter = 4;
						}
						
						// How about an ice block? This'll be one block down.
						else if (world.getBlockState(pos.down()).getBlock() == Blocks.ICE) {
							
							// Remove it.
							world.setBlockState(pos.down(), Blocks.WATER.getDefaultState());
							counter = 4;
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
			chunk = nextChunk(world, chunk.x, chunk.z);
			
			// Increment chunkcount
			chunkCount++;
		}
	}
}