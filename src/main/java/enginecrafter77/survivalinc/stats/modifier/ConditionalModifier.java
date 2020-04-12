package enginecrafter77.survivalinc.stats.modifier;

import java.util.function.Predicate;

public class ConditionalModifier<TARGET> implements Modifier<TARGET>
{
	private final Predicate<TARGET> test;
	private final float modifier;
	
	public ConditionalModifier(Predicate<TARGET> test, float modifier)
	{
		this.modifier = modifier;
		this.test = test;
	}
	
	@Override
	public boolean shouldTrigger(TARGET target, float level)
	{
		return this.test.test(target);
	}

	@Override
	public float apply(TARGET target, float current)
	{
		return modifier;
	}
}