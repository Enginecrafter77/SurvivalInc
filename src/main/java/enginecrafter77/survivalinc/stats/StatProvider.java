package enginecrafter77.survivalinc.stats;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.Serializable;

/**
 * StatProvider serves as a descriptor of a stat.
 * StatProvider's main job is providing the stat's
 * ID, creating new {@link StatRecord}, and updating
 * the record each tick.
 * @author Enginecrafter77
 */
public interface StatProvider<RECORD extends StatRecord> extends Serializable {
	/**
	 * Called by the stat {@link StatTracker#update(EntityPlayer) tracker}
	 * to update the value of this stat. This method takes care of updating
	 * the record kept about the stat described by this class.
	 * and return the new value from this method.
	 * @param target The player this update was called for
	 * @param record The record kept about this stat
	 */
	public void update(EntityPlayer target, RECORD record);
	
	/**
	 * Returns a {@link ResourceLocation} based
	 * stat identifier. This is used to avoid stat
	 * name conflicts among different mods.
	 * @return A ResourceLocation-based stat ID
	 */
	public ResourceLocation getStatID();
	
	/**
	 * Creates a new record for the stat provider.
	 * This method is used to create a new record
	 * about the stat this interface tries to describe.
	 * This method allows for custom implementations
	 * to specify their own StatRecords that will be
	 * used to store their stats. This method replaces
	 * the legacy method <tt>getDefault</tt>, as this
	 * method can be used for the same purpose of setting
	 * default values when new record is created.
	 * @return A new instance of stat record.
	 */
	public RECORD createNewRecord();
	
	/**
	 * Returns the formal type representation
	 * of the {@link StatRecord} used by this
	 * provider.
	 * @return The type of object returned by {@link #createNewRecord()}
	 */
	public Class<RECORD> getRecordClass();
}
