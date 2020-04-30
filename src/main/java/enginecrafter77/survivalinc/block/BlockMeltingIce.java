package enginecrafter77.survivalinc.block;

import java.util.Random;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMeltingIce extends BlockMelting {

	public BlockMeltingIce()
	{
		super(Blocks.ICE, Blocks.WATER);
		this.setRegistryName(new ResourceLocation(SurvivalInc.MOD_ID, "melting_ice"));
		this.setTranslationKey("melting_ice");
	}
	
	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos position, IBlockState state, TileEntity tile, ItemStack stack)
	{
		this.from.harvestBlock(world, player, position, state, tile, stack);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }
	
	@Override
	public int quantityDropped(Random random)
	{
		return 0;
	}

}
