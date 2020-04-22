package enginecrafter77.survivalinc.stats;

import net.minecraft.nbt.NBTTagCompound;

/**
 * A simple StatRecord implementation, with the
 * ability to track changes occurring to the
 * record entry.
 * @author Enginecrafter77
 */
public class StatRecordEntry implements StatRecord {
	
	/**
	 * The last change that occurred during update to the value.
	 * Positive values indicate that the value grew, while negative
	 * values indicate that the value shrunk.
	 */
	private float lastChange;
	
	/** The value stored in the record entry */
	protected float value;
	
	/**
	 * Constructs StatRecordEntry with default value of <i>value</i>
	 */
	public StatRecordEntry(float value)
	{
		this.lastChange = 0;
		this.value = value;
	}
	
	/**
	 * Constructs StatRecordEntry with default value of 0
	 */
	public StatRecordEntry()
	{
		this(0F);
	}

	@Override
	public void setValue(float value)
	{
		this.lastChange = value - this.value;
		this.value = value;
	}

	@Override
	public float getLastChange()
	{
		return this.lastChange;
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
	
}
