package net.schoperation.schopcraft.block;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.schoperation.schopcraft.SchopCraft;

public class BlockLucid extends BlockFalling {
	
	/*
	 * Is it real?
	 */
	
	public BlockLucid() {
		
		super(Material.GLASS);
		
		// Names
		setRegistryName(new ResourceLocation(SchopCraft.MOD_ID, "lucid_block"));
		setUnlocalizedName(SchopCraft.RESOURCE_PREFIX + "lucid_block");
		
		// Properties
		setCreativeTab(SchopCraft.mainTab);
		setHardness(0.0f);
		setResistance(1.0f);
		setSoundType(SoundType.SLIME);
	}
	
	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		
		return true;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		
        return false;
    }
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		
		return false;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		
        return NULL_AABB;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		
	    return BlockRenderLayer.TRANSLUCENT;
	}
}
