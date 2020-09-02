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
	public final Range<Float> valuerange;
	
	/** The value stored in the record entry */
	protected float value;
	
	/** The change induced last tick */
	protected float change;
	
	/**
	 * Constructs StatRecordEntry with default value of <i>value</i>
	 */
	public SimpleStatRecord(Range<Float> range)
	{
		this.valuerange = range;
		this.change = 0F;
		this.value = 0F;
	}
	
	/**
	 * Constructs StatRecordEntry with default value of 0
	 */
	public SimpleStatRecord()
	{
		this(Range.all());
	}
	
	/**
	 * Checks if the value is in the desired range,
	 * and corrects it if necessary.
	 */
	protected void checkValue()
	{
		if(!valuerange.contains(this.value))
		{
			Float midpoint = valuerange.lowerEndpoint() + (valuerange.upperEndpoint() - valuerange.lowerEndpoint()) / 2F;
			switch(midpoint.compareTo(this.value))
			{
			case -1: // The midpoint is below the value, thus the value is greater than the range
				this.value = valuerange.upperEndpoint();
				break;
			case 1: // The midpoint is above the value, thus the value is less than the range
				this.value = valuerange.lowerEndpoint();
				break;
			default:
				return;
			}
			this.change = 0;
		}
	}
	
	public void setValue(float value)
	{
		this.change = value - this.value;
		this.value = value;
		this.checkValue();
	}
	
	public float getValue()
	{
		return this.value;
	}
	
	public void addToValue(float value)
	{
		this.change = value;
		this.value += value;
		this.checkValue();
	}
	
	public float getLastChange()
	{
		return this.change;
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
		return String.format("%f (+ %f/t)", this.value, this.change);
	}
	
}
