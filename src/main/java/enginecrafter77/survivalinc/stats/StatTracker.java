package enginecrafter77.survivalinc.stats;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * StatTracker is capability interface used for tracking the
 * so-called {@link StatProvider stats}. Stats are usually a
 * simple numbers represented by floating-point numbers, which
 * are often used to represent the current state associated with
 * player. The stats are designed to be updated each tick using
 * the {@link #update(EntityPlayer)} method. As such, they are
 * kept up-to-date, so additional mechanisms can take appropriate
 * action according to the current value of a stat.
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
	public StatProvider getProvider(ResourceLocation identifier);
	
	/**
	 * @return The set of all registered providers in this tracker.
	 */
	public Set<StatProvider> getRegisteredProviders();
	
	/**
	 * Assigns or replaces a {@link StatRecord}
	 * to the specified {@link StatProvider}.
	 * @param stat The stat to assign the record to
	 * @param value The record to assign
	 */
	public void setRecord(StatProvider stat, StatRecord value);
	
	/**
	 * Returns the record about the specified stat. If
	 * the {@link StatProvider} is not registered in
	 * this StatTracker instance, null is returned.
	 * @param stat The target stat provider
	 * @return The record regarding the stat provided or null if the stat is not tracked by this tracker.
	 */
	public StatRecord getRecord(StatProvider stat);
	
	/**
	 * Adds amount to the value in the stat's record.
	 * If the specified {@link StatProvider} has no record
	 * associated with it, this method throws an {@link IllegalStateException}.
	 * @param stat The stat to modify
	 * @param amount The amount to add to the stat's record
	 * @throws IllegalStateException when the stat provider has no associated record
	 */
	public void modifyStat(StatProvider stat, float amount) throws IllegalStateException;
	
	/**
	 * Sets the value inside the target stat's record.
	 * If the specified {@link StatProvider} has no record
	 * associated with it, this method throws an {@link IllegalStateException}.
	 * @param stat The stat to assign new value to
	 * @param amount The new value of the stat's record.
	 * @throws IllegalStateException when the stat provider has no associated record
	 */
	public void setStat(StatProvider stat, float amount) throws IllegalStateException;
	
	/**
	 * Returns the current value of the target stat's record.
	 * This method generally delegates to {@link StatRecord#getValue()}.
	 * If the specified {@link StatProvider} has no record
	 * associated with it, this method throws an {@link IllegalStateException}.
	 * @param stat The StatProvider to get the value about
	 * @return The value of the record kept about <i>stat</i>
	 * @throws IllegalStateException when the stat provider has no associated record
	 */
	public float getStat(StatProvider stat) throws IllegalStateException;
	
	/**
	 * Called each tick to update the stat tracker
	 * and the records stored about each stat. This
	 * method generally involves iterating over the
	 * set returned by {@link #getRegisteredProviders()},
	 * getting the currently stored record value, running the
	 * {@link StatProvider#updateValue(EntityPlayer, float)}
	 * with the second parameter equal to the current value,
	 * applying the target {@link OverflowHandler overflow policy},
	 * and storing the new value in the record.
	 * @param player The player to apply the update to
	 */
	public void update(EntityPlayer player);
	
	/**
	 * Returns the last change in value to the specified stat.
	 * @param stat The stat to get last change for
	 * @return The change in the specified stat last tick
	 */
	public float getLastChange(StatProvider stat);
}