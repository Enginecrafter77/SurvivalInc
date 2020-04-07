package enginecrafter77.survivalinc;

import java.util.function.Supplier;

import enginecrafter77.survivalinc.block.BlockColoredLeaves;
import enginecrafter77.survivalinc.block.BlockLucid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;

public enum ModBlocks implements Supplier<Block> {
	
	LUCID_BLOCK(new BlockLucid()),
	RED_LEAVES(new BlockColoredLeaves(BlockPlanks.EnumType.OAK)),
	YELLOW_LEAVES(new BlockColoredLeaves(BlockPlanks.EnumType.BIRCH)),
	ORANGE_LEAVES(new BlockColoredLeaves(BlockPlanks.EnumType.DARK_OAK));
	
	public final Block targetblock;
	
	private ModBlocks(Block instance)
	{
		this.targetblock = instance;
	}

	@Override
	public Block get()
	{
		return this.targetblock;
	}
}