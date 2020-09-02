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
}