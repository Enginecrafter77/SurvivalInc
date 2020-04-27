package enginecrafter77.survivalinc.block;

import java.util.Random;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockMeltingSnow extends BlockMelting {
	
	public BlockMeltingSnow()
	{
		super(Blocks.SNOW_LAYER, Blocks.AIR);
		this.setRegistryName(new ResourceLocation(SurvivalInc.MOD_ID, "melting_snow"));
		this.setDefaultState(this.blockState.getBaseState().withProperty(MELTPHASE, Integer.valueOf(0)));
		this.setTranslationKey("melting_snow");
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
	}
	
	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return super.isPassable(worldIn, pos);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return null;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		int phase = state.getValue(MELTPHASE);
		Item ret = null;
		switch(phase)
		{
		case 1:
			if(rand.nextBoolean()) break; // If true, you won't get anything.
		case 0:
			ret = Items.SNOWBALL;
		}
		return ret;
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}
	
}
