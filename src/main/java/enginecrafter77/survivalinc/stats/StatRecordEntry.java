package enginecrafter77.survivalinc.stats;

import net.minecraft.nbt.NBTTagCompound;

public class StatRecordEntry implements StatRecord {
	
	public float lastChange; // TODO make private
	public float value;
	
	public StatRecordEntry()
	{
		this.lastChange = 0;
		this.value = 0;
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
		tag.setFloat("change", this.lastChange);
		tag.setFloat("value", this.value);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		this.lastChange = nbt.getFloat("change");
		this.value = nbt.getFloat("value");
	}
	
}
