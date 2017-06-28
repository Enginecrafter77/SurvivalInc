package net.schoperation.schopcraft.util;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class ProximityLegacy {
	
	/*
	 * Congratulations on finding this class! This is just a showroom of my previous code in ProximityDetect.
	 * And boy was it messy! Thank God I started to think for once and made the detection process a bit better.
	 * But I'm keeping the other crap here just in case.
	 */
	
	/*
	 * // block position variables
		Block atfeet = world.getBlockState(new BlockPos(posX,posY,posZ)).getBlock();
		Block north = world.getBlockState(new BlockPos(posX,posY,posZ-1)).getBlock();
		Block south = world.getBlockState(new BlockPos(posX,posY,posZ+1)).getBlock();
		Block east = world.getBlockState(new BlockPos(posX+1,posY,posZ)).getBlock();
		Block west = world.getBlockState(new BlockPos(posX-1,posY,posZ)).getBlock();
		Block northeast = world.getBlockState(new BlockPos(posX+1,posY,posZ-1)).getBlock();
		Block northwest = world.getBlockState(new BlockPos(posX-1,posY,posZ-1)).getBlock();
		Block southeast = world.getBlockState(new BlockPos(posX+1,posY,posZ+1)).getBlock();
		Block southwest = world.getBlockState(new BlockPos(posX-1,posY,posZ+1)).getBlock();
		
		// see if those positions have the specified block at all
		if (atfeet == block) { return true; }
		else if (north == block) { return true; }
		else if (south == block) { return true; }
		else if (east == block) { return true; }
		else if (west == block) { return true; }
		else if (northeast == block) { return true; }
		else if (northwest == block) { return true; }
		else if (southeast == block) { return true; }
		else if (southwest == block) { return true; }
		else { return false; }
	 */
	
	/*
	if (Math.abs(z) == x && z < 0 || loopkey >= 1) {
		
		if (loopkey == 0) { loopkey = 1; }
		if (x == z && x > 0 || loopkey >= 2) {
			
			if (loopkey == 1) { loopkey = 2; }
			if (Math.abs(x) == z && x < 0 || loopkey >= 3) {
				
				if (loopkey == 2) { loopkey = 3; }
				if (x == z && x < 0) {
					
					loopkey = 0;
				}
				else {
					z--;
				}
			}
			else {
				x--;
			}
		}
		else {
			z++;
		}
	}
	else {
		x++;
	}
	*/
	
	/*
	 * // block position variables
		// within ONE block of the player.
		Block atfeet = world.getBlockState(new BlockPos(posX,posY,posZ)).getBlock();
		Block north = world.getBlockState(new BlockPos(posX,posY,posZ-1)).getBlock();
		Block south = world.getBlockState(new BlockPos(posX,posY,posZ+1)).getBlock();
		Block east = world.getBlockState(new BlockPos(posX+1,posY,posZ)).getBlock();
		Block west = world.getBlockState(new BlockPos(posX-1,posY,posZ)).getBlock();
		Block northeast = world.getBlockState(new BlockPos(posX+1,posY,posZ-1)).getBlock();
		Block northwest = world.getBlockState(new BlockPos(posX-1,posY,posZ-1)).getBlock();
		Block southeast = world.getBlockState(new BlockPos(posX+1,posY,posZ+1)).getBlock();
		Block southwest = world.getBlockState(new BlockPos(posX-1,posY,posZ+1)).getBlock();
		
		// within TWO blocks of the player.
		Block pos1 = world.getBlockState(new BlockPos(posX,posY,posZ-2)).getBlock();
		Block pos2 = world.getBlockState(new BlockPos(posX+1,posY,posZ-2)).getBlock();
		Block pos3 = world.getBlockState(new BlockPos(posX+2,posY,posZ-2)).getBlock();
		Block pos4 = world.getBlockState(new BlockPos(posX+2,posY,posZ-1)).getBlock();
		Block pos5 = world.getBlockState(new BlockPos(posX+2,posY,posZ)).getBlock();
		Block pos6 = world.getBlockState(new BlockPos(posX+2,posY,posZ+1)).getBlock();
		Block pos7 = world.getBlockState(new BlockPos(posX+2,posY,posZ+2)).getBlock();
		Block pos8 = world.getBlockState(new BlockPos(posX+1,posY,posZ+2)).getBlock();
		Block pos9 = world.getBlockState(new BlockPos(posX,posY,posZ+2)).getBlock();
		Block pos10 = world.getBlockState(new BlockPos(posX-1,posY,posZ+2)).getBlock();
		Block pos11 = world.getBlockState(new BlockPos(posX-2,posY,posZ+2)).getBlock();
		Block pos12 = world.getBlockState(new BlockPos(posX-2,posY,posZ+1)).getBlock();
		Block pos13 = world.getBlockState(new BlockPos(posX-2,posY,posZ)).getBlock();
		Block pos14 = world.getBlockState(new BlockPos(posX-2,posY,posZ-1)).getBlock();
		Block pos15 = world.getBlockState(new BlockPos(posX-2,posY,posZ-2)).getBlock();
		Block pos16 = world.getBlockState(new BlockPos(posX-1,posY,posZ-2)).getBlock();
	 */
	/*
	 	// see if those positions have the specified block at all
		if (atfeet == block) { return true; }
		else if (north == block) { return true; }
		else if (south == block) { return true; }
		else if (east == block) { return true; }
		else if (west == block) { return true; }
		else if (northeast == block) { return true; }
		else if (northwest == block) { return true; }
		else if (southeast == block) { return true; }
		else if (southwest == block) { return true; }
		else if (pos1 == block) { if (north != air && !throughBlock) { return false; } else { return true; } }
		else if (pos2 == block) { if (northeast != air && !throughBlock) { return false; } else { return true; } }
		else if (pos3 == block) { if (northeast != air && !throughBlock) { return false; } else { return true; } }
		else if (pos4 == block) { if (northeast != air && !throughBlock) { return false; } else { return true; } }
		else if (pos5 == block) { if (east != air && !throughBlock) { return false; } else { return true; } }
		else if (pos6 == block) { if (southeast != air && !throughBlock) { return false; } else { return true; } }
		else if (pos7 == block) { if (southeast != air && !throughBlock) { return false; } else { return true; } }
		else if (pos8 == block) { if (southeast != air && !throughBlock) { return false; } else { return true; } }
		else if (pos9 == block) { if (south != air && !throughBlock) { return false; } else { return true; } }
		else if (pos10 == block) { if (southwest != air && !throughBlock) { return false; } else { return true; } }
		else if (pos11 == block) { if (southwest != air && !throughBlock) { return false; } else { return true; } }
		else if (pos12 == block) { if (southwest != air && !throughBlock) { return false; } else { return true; } }
		else if (pos13 == block) { if (west != air && !throughBlock) { return false; } else { return true; } }
		else if (pos14 == block) { if (northwest != air && !throughBlock) { return false; } else { return true; } }
		else if (pos15 == block) { if (northwest != air && !throughBlock) { return false; } else { return true; } }
		else if (pos16 == block) { if (northwest != air && !throughBlock) { return false; } else { return true; } }
		else { return false; }
	 	*/
	
	

}
