package enginecrafter77.survivalinc.block;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockMeltingSnow extends BlockMelting {
	
	public BlockMeltingSnow()
	{
		super(Blocks.SNOW_LAYER, Blocks.AIR);
		this.setRegistryName(new ResourceLocation(SurvivalInc.MOD_ID, "melting_snow"));
		this.setTranslationKey("melting_snow");
		this.setSoundType(SoundType.SNOW);
		this.setLightOpacity(0);
		this.setHardness(0.1F);
	}
	
	@Override
	@SuppressWarnings("deprecation") // We need this
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
	@SuppressWarnings("deprecation") // also this
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return null;
	}
	
	@Override
	@SuppressWarnings("deprecation") // and this
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	@SuppressWarnings("deprecation") // and this
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
}
