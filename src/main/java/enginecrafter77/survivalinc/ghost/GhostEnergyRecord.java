package enginecrafter77.survivalinc.ghost;

import com.google.common.collect.Range;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import net.minecraft.nbt.NBTTagCompound;

public class GhostEnergyRecord extends SimpleStatRecord {
	
	/**
	 * BITS:
	 * 0: active
	 * 1: changing
	 */
	private byte status;
	
	public GhostEnergyRecord()
	{
		super(Range.closed(0F, 100F));
		this.status = 0x3;
	}
	
	public boolean shouldReceiveTicks()
	{
		return this.status != 0;
	}
	
	public boolean isActive()
	{
		return (this.status & 0x1) > 0;
	}
	
	public void setActive(boolean active)
	{
		this.status &= 0xFE;
		if(active) this.status |= 0x1;
		this.status |= 0x2;
		SurvivalInc.logger.info("Setting ghost status: {}", this.status);
	}
	
	public boolean hasPendingChange()
	{
		return (this.status & 0x2) > 0;
	}
	
	public void acceptChange()
	{
		this.status &= 0xFD;
		SurvivalInc.logger.info("Ghost status change accepted: {}", this.status);
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = super.serializeNBT();
		tag.setBoolean("active", this.isActive());
		return tag;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		super.deserializeNBT(nbt);
		this.setActive(nbt.getBoolean("active"));
	}
	
}
