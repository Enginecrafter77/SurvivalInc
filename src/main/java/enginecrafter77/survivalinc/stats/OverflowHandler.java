package enginecrafter77.survivalinc.stats;

import java.util.function.BiFunction;

public enum OverflowHandler implements BiFunction<StatProvider, Float, Float> {
	NONE((StatProvider provider, Float value) -> value),
	WRAP(OverflowHandler::wrap),
	CAP(OverflowHandler::cap);
	
	private final BiFunction<StatProvider, Float, Float> handler;
	
	private OverflowHandler(BiFunction<StatProvider, Float, Float> handler)
	{
		this.handler = handler;
	}
	
	@Override
	public Float apply(StatProvider stat, Float value)
	{
		return this.handler.apply(stat, value);
	}
	
	private static float wrap(StatProvider provider, Float value)
	{
		return ((value - provider.getMinimum()) % provider.getMaximum()) + provider.getMinimum();
	}
	
	private static float cap(StatProvider provider, Float value)
	{
		if(value > provider.getMaximum()) value = provider.getMaximum();
		if(value < provider.getMinimum()) value = provider.getMinimum();
		return value;
	}
}
