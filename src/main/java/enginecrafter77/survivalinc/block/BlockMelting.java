package enginecrafter77.survivalinc.block;

import java.util.Random;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMelting extends Block {
	public static final PropertyInteger MELTPHASE = PropertyInteger.create("meltphase", 0, 3);
	
	public BlockMelting(Material material)
	{
		super(material);
		this.setTickRandomly(true);
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {MELTPHASE});
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(MELTPHASE);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(MELTPHASE, meta);
	}

	@Override
	public void updateTick(World world, BlockPos position, IBlockState state, Random rng)
	{
		int phase = state.getValue(MELTPHASE) + 1;
		SurvivalInc.logger.info("Triggering snow melt. Current level: {}", phase);
		if(MELTPHASE.getAllowedValues().contains(phase))
		{
			state = state.withProperty(MELTPHASE, phase);
			world.setBlockState(position, state, 3);
		}
		else world.setBlockToAir(position);
	}
}
