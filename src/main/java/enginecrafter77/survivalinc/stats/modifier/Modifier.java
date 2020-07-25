package enginecrafter77.survivalinc.stats.modifier;

/**
 * Modifier represents an abstract way to
 * describe how to modify an object by
 * means regarding a numerical value.
 * Generally, this is designed to work
 * with {@link StatProvider} and {@link StatTracker}s.
 * @author Enginecrafter77
 * @param <TARGET> The target this modifier applies to
 */
@Deprecated
public interface Modifier<TARGET> {
	/**
	 * Determines if this modifier is applicable to the given
	 * target and also if the application is feasible for the
	 * given target. Basically, this method determines the
	 * behavior of {@link ModifierApplicator} and whether
	 * the method {@link #apply(Object, float)} on this object
	 * will ever be run.
	 * @param target The target to determine against
	 * @param level The current level of some stat
	 * @return True to allow running the {@link #apply(Object, float)} method, false otherwise
	 */
	public boolean shouldTrigger(TARGET target, float level);
	
	/**
	 * Applies the current modifier onto the target, and updates
	 * the {@link StatTracker tracker's} value based on the operation
	 * given by {@link #getOperation()}.
	 * @param target The target to apply the modifier onto
	 * @param current The current value of the stat
	 * @return The modifier to the stat value (see {@link #getOperation()})
	 */
	public float apply(TARGET target, float current);
}
