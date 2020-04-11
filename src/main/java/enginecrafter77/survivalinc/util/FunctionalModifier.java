package enginecrafter77.survivalinc.util;

import java.util.function.Function;

public class FunctionalModifier<TARGET> implements Modifier<TARGET>
{
	private final Function<TARGET, Float> function;
	
	public FunctionalModifier(Function<TARGET, Float> function)
	{
		this.function = function;
	}
	
	@Override
	public boolean shouldTrigger(TARGET target, float level)
	{
		return true;
	}

	@Override
	public float apply(TARGET target, float current)
	{
		return function.apply(target);
	}
}