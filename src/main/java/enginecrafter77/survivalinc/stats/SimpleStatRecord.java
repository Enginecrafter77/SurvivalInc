package enginecrafter77.survivalinc.stats;

import com.google.common.collect.Range;

import net.minecraft.nbt.NBTTagCompound;

/**
 * A simple instance of stat record, which
 * is used to store the information about
 * stat's value, represented as float.
 * @author Enginecrafter77
 */
public class SimpleStatRecord implements StatRecord {
	
	/** The range of values accepted by this record */
	private Range<Float> valuerange;
	
	/** The value stored in the record entry */
	protected float value;
	
	/** The value kept for calculating the change */
	private float lastvalue;
	
	/** The change after last {@link #checkoutValueChange()} call */
	private float change;
	
	public SimpleStatRecord()
	{
		this.valuerange = Range.all();
		this.lastvalue = 0F;
		this.change = 0F;
		this.value = 0F;
	}
	
	/**
	 * Checks if the value is in the desired range,
	 * and corrects it if necessary.
	 */
	protected void checkValue()
	{
		Range<Float> range = this.getValueRange();
		if(!range.contains(this.value))
		{
			if(range.hasLowerBound() && this.value < range.lowerEndpoint())
			{
				this.value = range.lowerEndpoint();
				return; // We can safely skip the next check
			}
			
			if(range.hasUpperBound() && this.value > range.upperEndpoint())
			{
				this.value = range.upperEndpoint();
			}
		}
	}
	
	/**
	 * @return The actual value range of this stat record
	 */
	public Range<Float> getValueRange()
	{
		return this.valuerange;
	}
	
	/**
	 * Sets the internal allowed value range using
	 * the specified range.
	 * @param range The value range
	 */
	public void setValueRange(Range<Float> range)
	{
		this.valuerange = range;
	}
	
	/**
	 * Sets the internally stored numeric value.
	 * @param value The value to set the internal value to.
	 */
	public void setValue(float value)
	{
		this.value = value;
		this.checkValue();
	}
	
	/**
	 * @return The current internally stored value.
	 */
	public float getValue()
	{
		return this.value;
	}
	
	/**
	 * Returns a value between {@code 0.0} and {@code 1.0}, which
	 * corresponds to the intermediate value in the specified range.
	 * This method will throw {@link IllegalStateException} if the
	 * range lacks either lower or upper endpoint.
	 * @throws IllegalStateException if the stat record doesn't have fully closed value range.
	 * @return A value between {@code 0.0} and {@code 1.0} corresponding to the current value in the specified range
	 */
	public float getNormalizedValue() throws IllegalStateException
	{
		Range<Float> range = this.getValueRange();
		return (this.value - range.lowerEndpoint()) / (range.upperEndpoint() - range.lowerEndpoint());
	}
	
	/**
	 * Adds the specified value to the internal value.
	 * @param value The value to add to the internal value.
	 */
	public void addToValue(float value)
	{
		this.value += value;
		this.checkValue();
	}
	
	/**
	 * Returns the difference between the value at the time
	 * when {@link #checkoutValueChange()} was second last
	 * called and when <code>checkoutValue</code> was last
	 * called. For illustration, if we say "X" is the number
	 * of times <code>checkoutValueChange()</code> was called,
	 * and F(x) is a function which returns the value at the time
	 * it was called, then the value returned by this method is
	 * always equal to <tt>F(x-1) - F(x-2)</tt>
	 * @see #checkoutValueChange()
	 * @return The difference between the last checked out value and the current value
	 */
	public float getLastChange()
	{
		return this.change;
	}
	
	/**
	 * Commits the pending value change. This means that
	 * {@link #getLastChange()} will return the difference
	 * between the current value and the value at the time
	 * of previous call to this method. This method should
	 * generally be called when the processing on the record
	 * during a specific update is done.
	 * @see #getLastChange()
	 */
	public void checkoutValueChange()
	{
		this.change = this.value - this.lastvalue;
		this.lastvalue = this.value;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setFloat("value", this.value);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		this.value = nbt.getFloat("value");
	}
	
	@Override
	public String toString()
	{
		return String.format("%f <%f>", this.getValue(), this.getLastChange());
	}
	
}
