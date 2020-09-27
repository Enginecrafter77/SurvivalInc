package enginecrafter77.survivalinc.stats.effect;

import java.util.function.BiFunction;

import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import net.minecraft.entity.player.EntityPlayer;

/**
 * ValueStatEffect is a class which is used to modify a value
 * of {@link SimpleStatRecord} in a monotonic manner. ValueStatEffect
 * is used to apply a mathematical {@link Operation operation}
 * to the value with an {@link #argument}. For example, by using
 * {@link Operation#OFFSET} with argument equal to 2 basically
 * translates to "add 2 to the resulting value".
 * @see ValueStatEffect.Operation
 * @author Enginecrafter77
 */
public class ValueStatEffect implements CalculatorFunction, StatEffect<SimpleStatRecord> {
	
	/** The operation to apply using the current value and the {@link #argument} */
	public final Operation operation;
	
	/** The second operand to the mathematical equation */
	public final float argument;
	
	public ValueStatEffect(Operation operation, float argument)
	{
		this.operation = operation;
		this.argument = argument;
	}
	
	@Override
	public void apply(SimpleStatRecord record, EntityPlayer player)
	{
		float value = record.getValue();
		value = this.operation.function.apply(value, this.argument);
		record.setValue(value);
	}
	
	@Override
	public float apply(EntityPlayer player, float current)
	{
		return this.operation.function.apply(current, this.argument);
	}
	
	/**
	 * Operation enum defines possible mathematical operations
	 * that can be applied with the help of {@link ValueStatEffect}
	 * @author Enginecrafter77
	 */
	public static enum Operation {
		/** Absolute offsetting level-1 operation (add/subtract) */
		OFFSET((Float current, Float mod) -> current + mod),
		
		/** Relative scaling level-2 operation (multiply/divide) */
		SCALE((Float current, Float mod) -> current * mod),
		
		/** Hyper-relative power level-3 operation (power/root) */
		POWER((Float current, Float mod) -> (float)Math.pow(current, mod));
		
		public final BiFunction<Float, Float, Float> function;
		
		private Operation(BiFunction<Float, Float, Float> function)
		{
			this.function = function;
		}
	}
}