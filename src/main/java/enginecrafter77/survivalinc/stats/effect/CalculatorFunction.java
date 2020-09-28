package enginecrafter77.survivalinc.stats.effect;

import net.minecraft.entity.player.EntityPlayer;

/**
 * CalculatorFunction is an interface which specifies an
 * operation that is to be done by {@link FunctionalCalculator}.
 * @author Enginecrafter77
 */
@FunctionalInterface
public interface CalculatorFunction {
	/**
	 * Applies the described transformation(s) to the provided value.
	 * @param player The subject
	 * @param value The current value
	 * @return The new value, most probably dependent on the input value.
	 */
	public float apply(EntityPlayer player, float value);
}
