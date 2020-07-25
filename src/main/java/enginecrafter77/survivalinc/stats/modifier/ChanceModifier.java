package enginecrafter77.survivalinc.stats.modifier;

import java.util.Random;

@Deprecated
public class ChanceModifier<TYPE> implements Modifier<TYPE> {
	
	public final Modifier<TYPE> action;
	public final Random rng;
	public float chance;
	
	public ChanceModifier(Modifier<TYPE> action, Random rng, float chance)
	{
		this.action = action;
		this.chance = chance;
		this.rng = rng;
	}
	
	public ChanceModifier(Modifier<TYPE> action, float chance)
	{
		this(action, new Random(), chance);
	}

	@Override
	public boolean shouldTrigger(TYPE target, float level)
	{
		return rng.nextFloat() < chance && this.action.shouldTrigger(target, level);
	}

	@Override
	public float apply(TYPE target, float current)
	{
		return this.action.apply(target, current);
	}
	
}
