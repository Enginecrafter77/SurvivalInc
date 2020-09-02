package enginecrafter77.survivalinc.stats;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * StatRecord stores information about a stat. In {@link StatTracker},
 * it is paired with {@link StatProvider} to provide information about
 * that stat. This class must keep at lest the record of the stat's
 * value, and the last change that has occurred to this stats (the
 * difference between pre-update and post-update values). The StatRecord
 * itself is {@link INBTSerializable}, which provides facilities for
 * fine-tuning how the stat record is serialized and deserialized from
 * the NBT.
 * @author Enginecrafter77
 */
public interface StatRecord extends INBTSerializable<NBTTagCompound> {
}
