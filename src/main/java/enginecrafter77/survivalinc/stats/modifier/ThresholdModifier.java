package enginecrafter77.survivalinc.stats.modifier;

public class ThresholdModifier<TYPE> implements Modifier<TYPE> {
	
	public static final int LOWER = -1;
	public static final int HIGHER = 1;
	
	public final Modifier<TYPE> action;
	public float threshold;
	public final int type;
	
	public ThresholdModifier(Modifier<TYPE> action, float threshold, int type)
	{
		this.threshold = threshold;
		this.action = action;
		this.type = type;
	}
	
	@Override
	public boolean shouldTrigger(TYPE target, float level)
	{
		return (level * type) > (threshold * type) && action.shouldTrigger(target, level);
	}

	@Override
	public float apply(TYPE target, float current)
	{
		return this.action.apply(target, current);
	}

}
