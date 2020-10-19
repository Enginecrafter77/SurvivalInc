package enginecrafter77.survivalinc.stats;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * StatRecord stores information about a stat. In {@link StatTracker},
 * it is paired with {@link StatProvider} to provide information about
 * that stat. StatRecord can generally keep any value whatsoever. Values
 * that are meant to be non-volatile must be specified in {@link #deserializeNBT(NBTTagCompound)}
 * and {@link #serializeNBT()}; the methods inherited from {@link INBTSerializable}
 * @author Enginecrafter77
 */
public interface StatRecord extends INBTSerializable<NBTTagCompound> {
}
