package enginecrafter77.survivalinc.stats;

import java.io.Serializable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * StatProvider describes how a specific stat should behave.
 * This includes specifying maximum, minimum and default values.
 * This interface is designed to be used as singleton. That means
 * it's not good to store stat values in the implementing class,
 * as this stat provider can possibly be shared among multiple
 * {@link StatTracker trackers}.
 * @author Enginecrafter77
 */
public interface StatProvider extends Serializable {
	/**
	 * Called by the stat {@link StatTracker#update(EntityPlayer) tracker}
	 * to update the value of this stat. This method basically calculates
	 * the change to the stat value based on the player the update was
	 * called for. The general principle is to take the <i>current</i>
	 * parameter, calculate the new value for the target <i>player</i>,
	 * and return the new value from this method.
	 * @param target The player this update was called for
	 * @param current The current value of the stat
	 * @return The new value of the stat
	 */
	public void update(EntityPlayer target, StatRecord record);
	
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
	public StatRecord createNewRecord();
}
