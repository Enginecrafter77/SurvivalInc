package enginecrafter77.survivalinc.stats.impl;

import com.google.common.collect.Range;

import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import net.minecraft.nbt.NBTTagCompound;

public class SanityRecord extends SimpleStatRecord {
	public static final Range<Float> values = Range.closed(0F, 100F);
	
	protected int ticksAwake;
	
	public SanityRecord()
	{
		super(SanityRecord.values);
		this.setValue((float)ModConfig.SANITY.startValue);
		this.ticksAwake = 0;
	}
	
	public void resetSleep()
	{
		this.ticksAwake = 0;
	}
	
	public int getTicksAwake()
	{
		return this.ticksAwake;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = super.serializeNBT();
		tag.setInteger("ticksAwake", this.ticksAwake);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		super.deserializeNBT(nbt);
		this.ticksAwake = nbt.getInteger("ticksAwake");
	}
	
	@Override
	public String toString()
	{
		return super.toString() + String.format(" [SD: %d]", this.ticksAwake);
	}
}