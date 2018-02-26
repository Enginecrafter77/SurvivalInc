package net.schoperation.schopcraft.lib;

import net.minecraft.block.Block;
import net.schoperation.schopcraft.block.BlockLucid;
import net.schoperation.schopcraft.block.BlockOrangeLeaves;
import net.schoperation.schopcraft.block.BlockRedLeaves;
import net.schoperation.schopcraft.block.BlockYellowLeaves;

public class ModBlocks {
	
	/*
	 *  A list of all blocks in the game, used to quickly register and render everything. kek
	 */
	
	// List for easy referencing.
	
	public static final Block LUCID_BLOCK = new BlockLucid();
	public static final Block RED_LEAVES = new BlockRedLeaves();
	public static final Block YELLOW_LEAVES = new BlockYellowLeaves();
	public static final Block ORANGE_LEAVES = new BlockOrangeLeaves();
	
	public static final Block[] BLOCKS = {
				
				LUCID_BLOCK,
				RED_LEAVES,
				YELLOW_LEAVES,
				ORANGE_LEAVES
			
	};
}