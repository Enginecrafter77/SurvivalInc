package enginecrafter77.survivalinc.block;

import enginecrafter77.survivalinc.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * BlockMelting represents an intermediate block between two blocks,
 * of which one is said to melt and become the other one.
 * @author Enginecrafter77
 */
public class BlockMelting extends Block {
	
	/** The block to which this block turns into if fully melted */
	public final Block meltTarget;
	
	/** The block to which this block turns into if fully frozen */
	public final Block freezeTarget;
	
	/** The melt phase property */
	private PropertyInteger phase_property;
	
	/** The temperature which when passed, the block freezes/melts */
	public float freezing_point = 0.15F;
	
	public BlockMelting(Block frozen, Block melted)
	{
		super(frozen.getDefaultState().getMaterial());
		this.freezeTarget = frozen;
		this.meltTarget = melted;
		
		this.setDefaultState(this.blockState.getBaseState().withProperty(phase_property, 0));
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
		return ModConfig.SEASONS.meltController.requiresRandomTicks();
	}

	@Override
	public void updateTick(World world, BlockPos position, IBlockState state, Random rng)
	{
		world.profiler.startSection("melting");
		
		MeltAction action = this.getAction(world, position);
		if(action != MeltAction.PASS) // Check to avoid unnecessary block updates
		{
			int phase = state.getValue(phase_property) + action.getPhaseIncrement();
			if(this.phase_property.getAllowedValues().contains(phase))
			{
				state = state.withProperty(phase_property, phase);
				world.setBlockState(position, state, 2);
			}
			else
			{
				IBlockState next = this.transform(world, position, action);
				world.setBlockState(position, next, 2);
			}
		}
		
		world.profiler.endSection();
	}
	
	/**
	 * @return Number of intermediate steps between frozen and melted state
	 */
	public int getPhaseCount()
	{
		return 3;
	}
	
	/**
	 * Called when the melting block reaches borderline
	 * status, i.e. the phase has reached value out of
	 * its range, so the block should transform into
	 * another block based on the melt action.
	 * @param world The world
	 * @param position The position of the transforming block
	 * @param action The melt action performed this tick
	 * @return The block to which this block transforms upon either {@link MeltAction#MELT melting} or {@link MeltAction#FREEZE freezing}. Returns null if none is applicable.
	 */
	public IBlockState transform(World world, BlockPos position, MeltAction action)
	{
		switch(action)
		{
		case FREEZE:
			return this.freezeTarget.getDefaultState();
		case MELT:
			return this.meltTarget.getDefaultState();
		default:
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Called on world ticks to change the melting phase of the melting block.
	 * @param world The world to operate in
	 * @param position The position of the block
	 * @return One of the possible actions that will be performed
	 */
	public MeltAction getAction(World world, BlockPos position)
	{
		return world.getBiome(position).getTemperature(position) > this.freezing_point ? MeltAction.MELT : MeltAction.FREEZE;
	}
	
	/**
	 * MeltAction represents the possible actions
	 * that may be performed on a melting block.
	 * @author Enginecrafter77
	 */
	public static enum MeltAction
	{
		/** Causes the block to undergo freezing, i.e. it's melt phase decreases */
		FREEZE,
		
		/** Does not modify the block's melt phase */
		PASS,
		
		/** Causes the block to undergo melting, i.e. it's melt phase increases */
		MELT;
		
		/**
		 * @return The increment to the melt phase this action causes.
		 */
		public int getPhaseIncrement()
		{
			return this.ordinal() - 1;
		}
	}
}
