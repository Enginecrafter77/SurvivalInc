package enginecrafter77.survivalinc.stats.modifier;

import java.util.function.BiFunction;
import java.util.function.Function;

public class FunctionalModifier<TARGET> implements Modifier<TARGET>
{	
	protected final BiFunction<TARGET, Float, Float> function;
	
	public FunctionalModifier(BiFunction<TARGET, Float, Float> function)
	{
		this.function = function;
	}
	
	public FunctionalModifier(Function<TARGET, Float> function)
	{
		this((TARGET target, Float value) -> function.apply(target));
	}
	
	@Override
	public boolean shouldTrigger(TARGET target, float level)
	{
		return true;
	}

	@Override
	public float apply(TARGET target, float current)
	{
		return this.function.apply(target, current);
	}
}