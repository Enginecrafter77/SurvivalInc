package enginecrafter77.survivalinc.stats.effect;

import java.util.function.BiFunction;

import net.minecraft.entity.player.EntityPlayer;

/**
 * ConstantStatEffect is a class which is used to
 * modify a value in a monotonic manner. ConstantStatEffect
 * is used to apply a mathematical {@link Operation operation}
 * to the value with an {@link #argument}. For example, by using
 * {@link Operation#OFFSET} with argument equal to 2 basically
 * translates to "add 2 to the resulting value".
 * @see ConstantStatEffect.Operation
 * @author Enginecrafter77
 */
public class ConstantStatEffect implements StatEffect {
	
	/** The operation to apply using the current value and the {@link #argument} */
	public final Operation operation;
	
	/** The second operand to the mathematical equation */
	public final float argument;
	
	public ConstantStatEffect(Operation operation, float argument)
	{
		this.operation = operation;
		this.argument = argument;
	}
	
	@Override
	public float apply(EntityPlayer player, float current)
	{
		return this.operation.apply(current, this.argument);
	}
	
	/**
	 * Operation enum defines possible mathematical operations
	 * that can be applied with the help of {@link ConstantStatEffect}
	 * @author Enginecrafter77
	 */
	public static enum Operation implements BiFunction<Float, Float, Float> {
		/** Absolute offsetting level-1 operation (add/subtract) */
		OFFSET((Float current, Float mod) -> current + mod),
		
		/** Relative scaling level-2 operation (multiply/divide) */
		SCALE((Float current, Float mod) -> current * mod),
		
		/** Hyper-relative power level-3 operation (power/root) */
		POWER((Float current, Float mod) -> (float)Math.pow(current, mod));
		
		private final BiFunction<Float, Float, Float> function;
		
		private Operation(BiFunction<Float, Float, Float> function)
		{
			this.function = function;
		}

		@Override
		public Float apply(Float current, Float mod)
		{
			return function.apply(current, mod);
		}
	}
}
