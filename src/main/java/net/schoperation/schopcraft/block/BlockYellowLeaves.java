package net.schoperation.schopcraft.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.schoperation.schopcraft.SchopCraft;
import net.schoperation.schopcraft.lib.ModBlocks;

public class BlockYellowLeaves extends BlockColoredLeaves {
	
	/*
	 * Yellow birch leaves for Autumn. Bit messed up. But otherwise fine.
	 */
	
	public BlockYellowLeaves() {
		
		super();
		
		// Registry and Unlocalized names.
		setRegistryName(new ResourceLocation(SchopCraft.MOD_ID, "yellow_leaves"));
		setUnlocalizedName(SchopCraft.RESOURCE_PREFIX + "yellow_leaves");
	}
	
	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {

		List<ItemStack> list = new ArrayList();
		list.add(new ItemStack(ModBlocks.YELLOW_LEAVES));
		return list;
	}

	@Override
	public EnumType getWoodType(int meta) {

		return EnumType.BIRCH;
	}
}