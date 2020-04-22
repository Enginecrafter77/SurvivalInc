package enginecrafter77.survivalinc.stats;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;

/**
 * StatTracker is a class that keeps {@link StatRecord records}
 * of a player's stats, and updates them when {@link #update(EntityPlayer)}
 * is called.
 * @author Enginecrafter77
 */
public interface StatTracker {
	/**
	 * Registers the specified provider in the tracker.
	 * If the provider is already registered, throws
	 * an exception.
	 * @param provider The target provider
	 * @throws IllegalStateException when the provider is already registered
	 */
	public void registerProvider(StatProvider provider) throws IllegalStateException;
	
	/**
	 * Attempts to unregister the target provider from this
	 * tracker instance. If there is no such provider registered,
	 * this method throws an exception.
	 * @param provider The target provider
	 * @throws IllegalStateException when the provider is not found inside this tracker
	 */
	public void removeProvider(StatProvider provider) throws IllegalStateException;
	
	/**
	 * Searches for a {@link StatProvider provider} with the
	 * specified ID. If no such provider is found, null is
	 * returned by this method.
	 * @param identifier The {@link StatProvider#getStatID()} return value of the searched stat
	 * @return The target stat provider, or null if not found.
	 */
	public StatProvider getProvider(String identifier);
	
	/**
	 * @return The set of all registered providers in this tracker.
	 */
	public Set<StatProvider> getRegisteredProviders();
	
	/**
	 * Sets (or overwrites) the record of the target stat.
	 * @param stat The stat to assign the record to
	 * @param value The record to assign
	 */
	public void setRecord(StatProvider stat, StatRecord value);
	
	/**
	 * Returns the record about the specified stat. If
	 * the stat has no assigned record yet, a new record
	 * is created.
	 * @param stat The target stat provider
	 * @return The record regarding the stat provided
	 */
	public StatRecord getRecord(StatProvider stat);
	
	/**
	 * Adds amount to the value in the stat's record.
	 * @param stat The stat to modify
	 * @param amount The amount to add to the stat's record
	 */
	public void modifyStat(StatProvider stat, float amount);
	
	/**
	 * Sets the value inside the target stat's record.
	 * @param stat The stat to assign new value to
	 * @param amount The new value of the stat's record.
	 */
	public void setStat(StatProvider stat, float amount);
	
	/**
	 * Returns the current value of the target stat's record.
	 * This method generally delegates to {@link StatRecord#getValue()}
	 * @param stat The StatProvider to get the value about
	 * @return The value of the record keeped about <i>stat</i>
	 */
	public float getStat(StatProvider stat);
	
	/**
	 * Called each server tick to update the stat
	 * tracker and the records stored about each
	 * stat. This method generally involves iterating
	 * over the set returned by {@link #getRegisteredProviders()},
	 * getting the currently stored record value, running the
	 * {@link StatProvider#updateValue(EntityPlayer, float)}
	 * with the second parameter equal to the current value,
	 * applying the target {@link OverflowHandler overflow policy},
	 * and storing the new value in the record.
	 * Different implementations may differ, but this procedure
	 * shall be kept.
	 * @param player The player to apply the update to
	 */
	public void update(EntityPlayer player);
}