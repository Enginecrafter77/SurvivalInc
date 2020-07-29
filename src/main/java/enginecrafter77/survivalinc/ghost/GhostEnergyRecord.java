package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import net.minecraft.nbt.NBTTagCompound;

public class GhostEnergyRecord extends SimpleStatRecord {
	
	protected boolean active;
	
	public GhostEnergyRecord()
	{
		super();
		this.active = false;
	}
	
	public boolean isActive()
	{
		return this.active;
	}
	
	public void setActive(boolean active)
	{
		this.active = active;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = super.serializeNBT();
		tag.setBoolean("active", this.active);
		return tag;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		super.deserializeNBT(nbt);
		this.active = nbt.getBoolean("active");
	}
	
}
