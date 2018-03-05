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
	
	/*
	 * The base class for the autumn leaves.
	 */
	
public BlockColoredLeaves() {
		
		super();
		
		// Set correct tab
		setCreativeTab(SchopCraft.mainTab);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		
		return new BlockStateContainer(this, new IProperty[]{DECAYABLE, CHECK_DECAY});
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		
		if (state.getValue(DECAYABLE).booleanValue()) {
			
			if (state.getValue(CHECK_DECAY).booleanValue()) {
				
				return 0;
			}
			
			else {
				
				return 1;
			}
		}
		
		else {
			
			if (state.getValue(CHECK_DECAY).booleanValue()) {
				
				return 2;
			}
			
			else {
				
				return 3;
			}
		}
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		
		if (meta == 0) {
			
			return getDefaultState().withProperty(DECAYABLE, true).withProperty(CHECK_DECAY, true);
		}
		
		else if (meta == 1) {
			
			return getDefaultState().withProperty(DECAYABLE, true).withProperty(CHECK_DECAY, false);
		}
		
		else if (meta == 2) {
			
			return getDefaultState().withProperty(DECAYABLE, false).withProperty(CHECK_DECAY, true);
		}
		
		else {
			
			return getDefaultState().withProperty(DECAYABLE, false).withProperty(CHECK_DECAY, false);
		}
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {

		List<ItemStack> list = new ArrayList();
		list.add(new ItemStack(ModBlocks.RED_LEAVES));
		return list;
	}

	@Override
	public EnumType getWoodType(int meta) {

		return EnumType.OAK;
	}
	
	@SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
        
        // Render leaves correctly
        this.setGraphicsLevel(Minecraft.getMinecraft().gameSettings.fancyGraphics);
    }
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		
		// Was this a player?
		if (placer instanceof EntityPlayer && !worldIn.isRemote) {
			
			// Set blockstate properties so the leaves actually last
			worldIn.setBlockState(pos, state.withProperty(DECAYABLE, false).withProperty(CHECK_DECAY, false));
		}
    }
}