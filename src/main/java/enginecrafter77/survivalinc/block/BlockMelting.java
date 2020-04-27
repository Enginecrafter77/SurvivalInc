package enginecrafter77.survivalinc.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BlockMelting extends Block {
	public static final PropertyInteger MELTPHASE = PropertyInteger.create("meltphase", 0, 3);
	
	public final Block from, to;
	
	public BlockMelting(Block from, Block to)
	{
		super(from.getDefaultState().getMaterial());
		this.setTickRandomly(true);
		
		this.from = from;
		this.to = to;
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
		world.profiler.startSection("melting");
		
		Biome biome = world.getBiome(position);
		boolean melts = biome.getTemperature(position) > 0;
		int phase = state.getValue(MELTPHASE) + (melts ? 1 : -1);
		if(MELTPHASE.getAllowedValues().contains(phase))
		{
			state = state.withProperty(MELTPHASE, phase);
			world.setBlockState(position, state, 2);
		}
		else
		{
			Block target = melts ? this.to : this.from;
			world.setBlockState(position, target.getDefaultState(), 2);
		}
		
		world.profiler.endSection();
	}
}
