package schoperation.schopcraft.lib;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import schoperation.schopcraft.block.BlockColoredLeaves;
import schoperation.schopcraft.block.BlockLucid;

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