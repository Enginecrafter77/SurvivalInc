package enginecrafter77.survivalinc.stats;

import net.minecraft.nbt.NBTTagCompound;

/**
 * A simple instance of stat record, which
 * is used to store the information about
 * stat's value, represented as float.
 * @author Enginecrafter77
 */
public class SimpleStatRecord implements StatRecord {
	
	/** The value stored in the record entry */
	protected float value;
	
	/**
	 * Constructs StatRecordEntry with default value of <i>value</i>
	 */
	public SimpleStatRecord(float value)
	{
		this.value = value;
	}
	
	/**
	 * Constructs StatRecordEntry with default value of 0
	 */
	public SimpleStatRecord()
	{
		this(0F);
	}

	@Override
	public void setValue(float value)
	{
		this.value = value;
	}

	@Override
	public float getValue()
	{
		return this.value;
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
		return Float.toString(this.value);
	}
	
}
