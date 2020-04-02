package schoperation.schopcraft.block;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schoperation.schopcraft.SchopCraft;
import schoperation.schopcraft.lib.ModBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockColoredLeaves extends BlockLeaves {

	protected EnumType treetype;
	
	public BlockColoredLeaves(EnumType treetype)
	{
		this.treetype = treetype;
		this.setRegistryName(new ResourceLocation(SchopCraft.MOD_ID, "leaves_" + treetype.getUnlocalizedName()));
		this.setUnlocalizedName(SchopCraft.RESOURCE_PREFIX + "leaves_" + treetype.getUnlocalizedName());
		this.setCreativeTab(SchopCraft.mainTab);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] { DECAYABLE, CHECK_DECAY });
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		int meta = state.getValue(CHECK_DECAY).booleanValue() ? 0 : 1;
		if(state.getValue(DECAYABLE).booleanValue())
		{
			meta += 2;
		}
		return meta;
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{ 
		return getDefaultState().withProperty(DECAYABLE, meta >= 2).withProperty(CHECK_DECAY, meta % 2 == 0);
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
	{
		List<ItemStack> list = new ArrayList<ItemStack>();
		list.add(new ItemStack(ModBlocks.RED_LEAVES.get()));
		return list;
	}

	@Override
	public EnumType getWoodType(int meta)
	{
		return this.treetype;
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		super.randomDisplayTick(stateIn, worldIn, pos, rand);
		this.setGraphicsLevel(Minecraft.getMinecraft().gameSettings.fancyGraphics);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		// Check if the entity was a player and we are operating on server side
		if(placer instanceof EntityPlayer && !worldIn.isRemote)
		{
			// Set blockstate properties so the leaves actually last
			worldIn.setBlockState(pos, state.withProperty(DECAYABLE, false).withProperty(CHECK_DECAY, false));
		}
	}
}