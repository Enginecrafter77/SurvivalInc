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
			Float midpoint = range.lowerEndpoint() + (range.upperEndpoint() - range.lowerEndpoint()) / 2F;
			switch(midpoint.compareTo(this.value))
			{
			case -1: // The midpoint is below the value, thus the value is greater than the range
				this.value = range.upperEndpoint();
				break;
			case 1: // The midpoint is above the value, thus the value is less than the range
				this.value = range.lowerEndpoint();
				break;
			default:
				return;
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
	 * Returns the difference between the last
	 * checked-out value and the current value. This
	 * method depends on the time when {@link #checkoutValueChange()}
	 * was last called. The general idea is this method
	 * should return the change between the last tick's value
	 * and the current tick's computed value, but other
	 * implementations are free to specify their own terms.
	 * @see #checkoutValueChange()
	 * @return The difference between the last checked out value and the current value
	 */
	public float getLastChange()
	{
		return this.change;
	}
	
	/**
	 * Commits the pending value change. A call to this method
	 * in a thread-safe context will guarantee that the next
	 * call to {@link #getLastChange()} will result in 0.
	 * Generally, this method is intended to be run at the
	 * end of each tick, so that {@link #getLastChange()}
	 * may return the change relative to the last tick's value.
	 * But other implementations are free to specify their own terms.
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
		return String.format("%f (/\\: %f)", this.getValue(), this.getLastChange());
	}
	
}
