package enginecrafter77.survivalinc.stats.modifier;

import java.util.function.BiFunction;

public enum OperationType implements BiFunction<Float, Float, Float> {
	
	NOOP((Float current, Float mod) -> current),
	OVERWRITE((Float current, Float mod) -> mod),
	OFFSET((Float current, Float mod) -> current + mod),
	SCALE((Float current, Float mod) -> current * mod);
	
	private final BiFunction<Float, Float, Float> function;
	
	private OperationType(BiFunction<Float, Float, Float> function)
	{
		this.function = function;
	}

	@Override
	public Float apply(Float current, Float mod)
	{
		return function.apply(current, mod);
	}
}