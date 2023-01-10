package enginecrafter77.survivalinc.stats;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * StatTracker is capability interface used for tracking the so-called {@link StatProvider stats}. Stats are simple data
 * values kept using {@link StatRecord}s. The stats are designed to be updated each tick based on the situation the
 * player is currently in, so that other mechanisms can act upon them. StatTracker provides an interface for interaction
 * with these stats, as well as a system for seamless synchronization between client and server.
 *
 * @author Enginecrafter77
 */
public interface StatTracker {
	/**
	 * Registers the specified provider in the tracker. If the provider is already registered, throws an exception.
	 * @param provider The target provider
	 * @throws IllegalStateException when the provider is already registered.
	 */
	public void registerProvider(StatProvider<?> provider);
	
	/**
	 * Attempts to unregister the target provider from this tracker instance. If there is no such provider registered, this method throws an exception.
	 * @param provider The target provider
	 * @throws IllegalStateException when the provider is not found inside this tracker.
	 */
	public void removeProvider(StatProvider<?> provider);
	
	/**
	 * Searches for a {@link StatProvider provider} with the specified ID. If no such provider is found, null is returned by this method.
	 * @param identifier The {@link StatProvider#getStatID()} return value of the searched stat
	 * @return The target stat provider, or null if not found.
	 */
	@Nullable
	public StatProvider<?> getProvider(ResourceLocation identifier);
	
	/**
	 * @return The set of all registered providers in this tracker.
	 */
	public Collection<StatProvider<?>> getRegisteredProviders();
	
	/**
	 * Assigns or replaces a {@link StatRecord} to the specified {@link StatProvider}. If the given provider was
	 * not registered with this tracker before, a {@link UnknownStatException} will be thrown.
	 * @param stat The stat to assign the record to
	 * @param value The record to assign
	 * @throws UnknownStatException If the provider was not registered before
	 */
	public <RECORD extends StatRecord> void setRecord(StatProvider<RECORD> stat, RECORD value);
	
	/**
	 * Returns the record about the specified stat. If the {@link StatProvider} is not registered in this StatTracker
	 * instance, null is returned. If the given provider was not registered with this tracker before, a {@link UnknownStatException} will be thrown.
	 * @param stat The target stat provider
	 * @return The record regarding the stat provided or null if the stat is not tracked by this tracker.
	 * @throws UnknownStatException If the provider was not registered before
	 */
	@Nullable
	public <RECORD extends StatRecord> RECORD getRecord(StatProvider<RECORD> stat);
	
	/**
	 * Called each tick to update the stat tracker and the records stored about each stat. This method generally involves
	 * iterating over the set returned by {@link #getRegisteredProviders()}, getting the currently stored record value,
	 * running the {@link StatProvider#update(EntityPlayer, StatRecord)}.
	 * @param player The player to apply the update to
	 */
	public void update(EntityPlayer player);
	
	/**
	 * Checks whether the provided {@link StatProvider} will be updated on next tick. When supplied with the player
	 * parameter and the method returns true, the stat is guaranteed to have received update this tick. One may also supply
	 * null as the player parameter, in which case the method only checks whether the stat has been suspended from updating or not.
	 * In case the supplied stat provider is not registered in this tracker, the method throws {@link UnknownStatException}.
	 * @see #setSuspended(StatProvider, boolean)
	 * @param stat The stat to run the check for
	 * @param player The player to check for the update, or null to check only the suspended state
	 * @return True if the player is provided, and they passed all the checks, false otherwise. If player is null, returns
	 *         true if the stat is NOT suspended, false otherwise.
	 * @throws UnknownStatException If the given provider is not registered inside this tracker
	 */
	public boolean isActive(StatProvider<?> stat, @Nullable EntityPlayer player);
	
	/**
	 * Suspends or resumes the specified stat. Suspending a stat causes the stat to skip running its provider
	 * {@link StatProvider#update(EntityPlayer, StatRecord) update} method until it is resumed. Generally, suspending a stat
	 * temporarily disables any effect it has on the player whatsoever. The suspended status of a stat can be checked by
	 * passing <i>null</i> as player parameter to {@link #isActive(StatProvider, EntityPlayer)}. If the given provider was
	 * not registered with this tracker before, a {@link UnknownStatException} will be thrown.
	 *
	 * @see StatProvider#update(EntityPlayer, StatRecord)
	 * @see #isActive(StatProvider, EntityPlayer)
	 * @param stat The stat to set the suspended status for
	 * @param suspended The suspended status
	 * @throws UnknownStatException If the given provider is not registered inside this tracker
	 */
	public void setSuspended(StatProvider<?> stat, boolean suspended);
}
