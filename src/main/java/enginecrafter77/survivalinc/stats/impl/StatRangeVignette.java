package enginecrafter77.survivalinc.stats.impl;

import com.google.common.collect.Range;
import enginecrafter77.survivalinc.client.ElementalVignette;
import enginecrafter77.survivalinc.client.RenderFrameContext;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.ReadablePoint;

import java.util.Optional;

/**
 * StatRangeVignette is a {@link ElementalVignette} which
 * is designed to work with {@link StatTracker}s. Basically,
 * StatRangeVignette operates with {@link StatProvider}s that
 * use {@link SimpleStatRecord}s. The StatRangeVignette is only
 * drawn when the stat in question is active AND when the record's
 * value is in the activation range. If the value falls into the
 * activation range, it's mapped into a proportional position inside
 * that range, much like {@link SimpleStatRecord#getNormalizedValue()}
 * does. This value indicates the opacity (or intensity) of the vignette.
 * @author Enginecrafter77
 */
public class StatRangeVignette extends ElementalVignette {

	/** The stat provider used to track the desired stat */
	public final StatProvider<? extends SimpleStatRecord> provider;
	
	/** The activation range. Both ends need to be definitive. */
	public final Range<Float> activationrange;
	
	/** The vignette color */
	public final ReadableColor color;
	
	/**
	 * When true, the value mapping is reversed so that opposite
	 * values are returned. For example, 0.25 becomes 0.75. This
	 * is useful in activation ranges defined by {@link Range#lessThan(Comparable)},
	 * since it's implied that the user wants the effect "less the value more the intensity".
	 * In other words, value of false uses "more value => stronger intensity", while true
	 * means "less value => stronger intensity".
	 */
	public final boolean reverse;
	
	/** Set to true to use quasi-logarithmic scaling instead of linear one */
	public final boolean logarithmic;
	
	public StatRangeVignette(StatProvider<? extends SimpleStatRecord> provider, Range<Float> activationrange, ReadableColor color, float maxalpha, boolean logarithmic, boolean reverse)
	{
		super(maxalpha);
		this.logarithmic = logarithmic;
		this.provider = provider;
		this.reverse = reverse;
		this.color = color;
		
		SimpleStatRecord dummyrecord = this.provider.createNewRecord();
		this.activationrange = dummyrecord.getValueRange().intersection(activationrange);
		
		this.setTint(color);
	}
	
	/**
	 * Returns the value proportional position inside the activation range.
	 * @param value The value in question
	 * @return Value from 0.0 to 1.0 indicating the relative position between range start and end
	 */
	public float getValuePropPos(float value)
	{
		value = (value - this.activationrange.lowerEndpoint()) / (this.activationrange.upperEndpoint() - this.activationrange.lowerEndpoint());
		if(this.reverse) value = 1F - value;
		return value;
	}

	@Override
	public void draw(RenderFrameContext context, ReadablePoint position)
	{
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		StatTracker tracker = player.getCapability(StatCapability.getInstance(), null);
		if(tracker == null || !tracker.isActive(this.provider, player))
			return;

		float magnitude = Optional.ofNullable(tracker.getRecord(this.provider)).map(SimpleStatRecord::getValue).orElse(0F);
		if(this.activationrange.contains(magnitude))
		{
			float prop = this.getValuePropPos(magnitude);
			if(this.logarithmic)
				this.setOpacity((1F - (float)Math.pow(6F, -2F * prop)) / 0.9723F); // Scale logarithmically
			super.draw(context, position);
		}
	}
}
