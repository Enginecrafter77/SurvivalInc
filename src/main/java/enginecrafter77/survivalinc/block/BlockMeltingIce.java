package enginecrafter77.survivalinc.block;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.block.SoundType;
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

import javax.annotation.Nullable;
import java.util.Random;

public class BlockMeltingIce extends BlockMelting {

	public BlockMeltingIce()
	{
		super(Blocks.ICE, Blocks.WATER);
		this.setRegistryName(new ResourceLocation(SurvivalInc.MOD_ID, "melting_ice"));
		this.setTranslationKey("melting_ice");
		this.setSoundType(SoundType.GLASS);
		this.setLightOpacity(3);
		this.setHardness(0.5F);
	}
	
	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos position, IBlockState state, @Nullable TileEntity tile, ItemStack stack)
	{
		this.freezeTarget.harvestBlock(world, player, position, state, tile, stack);
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
