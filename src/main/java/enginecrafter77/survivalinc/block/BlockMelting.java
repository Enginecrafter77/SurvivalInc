package enginecrafter77.survivalinc.block;

import java.util.Random;

import enginecrafter77.survivalinc.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * BlockMelting represents a transitional block between
 * two blocks, of which one is said to melt and become
 * the other one.
 * @author Enginecrafter77
 */
public class BlockMelting extends Block {
	
	/** The block which was transformed to BlockMelting */
	public final Block predecessor;
	
	/** The block to which this block eventually melts into */
	public final Block successor;
	
	/** The melt phase property */
	private PropertyInteger phase_property;
	
	/** The temperature which when passed, the block freezes/melts */
	public float freezing_point = 0.15F;
	
	public BlockMelting(Block predecessor, Block successor)
	{
		super(predecessor.getDefaultState().getMaterial());
		this.predecessor = predecessor;
		this.successor = successor;
		
		this.setDefaultState(this.blockState.getBaseState().withProperty(phase_property, Integer.valueOf(0)));
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
		this.phase_property = PropertyInteger.create("meltphase", 0, this.getPhaseCount());
		return new BlockStateContainer(this, phase_property);
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(phase_property);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(phase_property, meta);
	}
	
	@Override
	public boolean getTickRandomly()
	{
		return ModConfig.SEASONS.meltController.allowRandomTicks();
	}

	@Override
	public void updateTick(World world, BlockPos position, IBlockState state, Random rng)
	{
		world.profiler.startSection("melting");
		
		boolean melts = this.shouldMelt(world, position);
		int phase = state.getValue(phase_property) + (melts ? 1 : -1);
		if(phase_property.getAllowedValues().contains(phase))
		{
			state = state.withProperty(phase_property, phase);
			world.setBlockState(position, state, 2);
		}
		else
		{
			Block target = melts ? this.successor : this.predecessor;
			world.setBlockState(position, target.getDefaultState(), 2);
		}
		
		world.profiler.endSection();
	}
	
	/**
	 * @return The maximum melt phase
	 */
	public int getPhaseCount()
	{
		return 3;
	}
	
	/**
	 * Used to get reference to the melt phase property
	 * @return The melt phase property of the block
	 */
	public PropertyInteger getMeltProperty()
	{
		return this.phase_property;
	}
	
	/**
	 * Indicates whether the block at the specified position should melt.
	 * If this method returns true, it's melt phase is incremented by 1.
	 * @param world The world to operate in
	 * @param position The position of the block
	 * @return True if the block should increment it's melt phase
	 */
	public boolean shouldMelt(World world, BlockPos position)
	{
		return world.getBiome(position).getTemperature(position) > this.freezing_point;
	}
	
	/**
	 * Indicates whether the block at the specified position should freeze.
	 * If this method returns true, it's melt phase is decreased by 1.
	 * @param world The world to operate in
	 * @param position The position of the block
	 * @return True if the block should decrease it's melt phase.
	 */
	public boolean shouldFreeze(World world, BlockPos position)
	{
		return !this.shouldMelt(world, position);
	}
}
