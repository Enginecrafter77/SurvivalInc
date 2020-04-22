package enginecrafter77.survivalinc.stats;

import java.util.function.BiFunction;

/**
 * The overflow handler specifies the behavior when the
 * value of a stat passes the values specified by the
 * stat's {@link StatProvider provider}. This class,
 * in itself, is a BiFunction with the same signature
 * as the {@link StatProvider#updateValue(net.minecraft.entity.player.EntityPlayer, float)}
 * method. This allows for similar procedure as updating the value
 * to happen, but instead of updating, it applies the chosen overflow
 * policy to the value.
 * @author Enginecrafter77
 */
public enum OverflowHandler implements BiFunction<StatProvider, Float, Float> {
	/**
	 * Does absolutely no checking against the value's boundaries,
	 * and allows it to overflow the boundaries.
	 */
	NONE((StatProvider provider, Float value) -> value),
	
	/**
	 * When the value passes past the boundaries, wraps
	 * the value to it's respective position past the
	 * opposite boundary. Think of it like the value is
	 * some object and both boundaries are some teleporters.
	 * When the object is moved past the teleporters, it
	 * appears in the other teleporter, continuing it's
	 * move procedure.
	 */
	WRAP(OverflowHandler::wrap),
	
	/**
	 * Caps the value against the boundaries. When
	 * the value exceeds the maximum or minimum, set
	 * the value to maximum or minimum boundaries
	 * respectively. This is the most commonly
	 * utilized overflow handler.
	 */
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
