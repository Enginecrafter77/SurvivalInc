package enginecrafter77.survivalinc.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Modifier Applicator is basically an evaluation core of the Modifier
 * module. The modifier applicator is able to store another modifiers
 * in a set. When invoked, the modifier applicator runs through the
 * registered modifiers, applying them one by another, provided that
 * the modifier's {@link Modifier#shouldTrigger(Object, float)} method
 * returned true. When such modifier is applied, the method {@link #continueAfterMatch(Modifier)}
 * is run to query whether to continue applying another modifiers from the
 * set, or to simply exit. ModifierApplicator is also an instance of Modifier
 * itself, and therefore it allows for structural nesting of ModifierApplicators
 * inside other applicators.
 * @author Enginecrafter77
 * @param <TYPE> The type this applicator works with
 */
public class ModifierApplicator<TYPE> extends HashMap<Modifier<TYPE>, OperationType> implements Modifier<TYPE> {
	private static final long serialVersionUID = -4354151273868761972L;
	
	/**
	 * Determines if this applicator should give up after
	 * it has found a modifier whose {@link Modifier#shouldTrigger(float)}
	 * method returned true.
	 */
	public boolean singleCatch;
	
	public ModifierApplicator()
	{
		this.singleCatch = false;
	}
	
	/**
	 * Determines if this applicator should continue after
	 * it has found a modifier whose {@link Modifier#shouldTrigger(float)}
	 * method returned true.
	 * @param mod The modifier which has just been applied
	 * @return True to continue applying other modifiers, false to stop
	 */
	protected boolean continueAfterMatch(Modifier<TYPE> mod)
	{
		return !this.singleCatch;
	}
	
	public void add(Modifier<TYPE> mod)
	{
		this.put(mod, OperationType.NOOP);
	}
	
	@Override
	public float apply(TYPE target, float level)
	{
		for(Map.Entry<Modifier<TYPE>, OperationType> entry : this.entrySet())
		{
			Modifier<TYPE> mod = entry.getKey();
			if(mod.shouldTrigger(target, level))
			{
				float newvalue = mod.apply(target, level);
				level = entry.getValue().apply(level, newvalue);
				
				if(!this.continueAfterMatch(mod)) break;
			}
		}
		return level;
	}
	
	@Override
	public boolean shouldTrigger(TYPE target, float level)
	{
		return this.size() > 0;
	}
}
