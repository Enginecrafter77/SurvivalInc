package schoperation.schopcraft.block;

import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.schoperation.schopcraft.SchopCraft;
import net.schoperation.schopcraft.lib.ModBlocks;

import java.util.ArrayList;
import java.util.List;

public class BlockOrangeLeaves extends BlockColoredLeaves {

	/*
	 * Orange dark oak leaves for Autumn. Bit messed up. But otherwise fine.
	 */
	
	public BlockOrangeLeaves() {
		
		super();
		
		// Registry and Unlocalized names.
		setRegistryName(new ResourceLocation(SchopCraft.MOD_ID, "orange_leaves"));
		setUnlocalizedName(SchopCraft.RESOURCE_PREFIX + "orange_leaves");
	}
	
	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {

		List<ItemStack> list = new ArrayList();
		list.add(new ItemStack(ModBlocks.ORANGE_LEAVES));
		return list;
	}

	@Override
	public EnumType getWoodType(int meta) {

		return EnumType.DARK_OAK;
	}
}