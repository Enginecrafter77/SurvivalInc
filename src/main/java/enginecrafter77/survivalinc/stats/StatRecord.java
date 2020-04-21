package enginecrafter77.survivalinc.stats;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface StatRecord extends INBTSerializable<NBTTagCompound> {
	public void setValue(float value);
	public float getLastChange();
	public float getValue();
}
