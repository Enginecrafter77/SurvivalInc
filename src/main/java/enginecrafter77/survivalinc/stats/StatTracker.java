package enginecrafter77.survivalinc.stats;

import java.util.Collection;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * StatTracker is capability interface used for tracking the
 * so-called {@link StatProvider stats}. Stats are simple data
 * values kept using {@link StatRecord}s. The stats are designed
 * to be updated each tick based on the situation the player is
 * currently in, so that other mechanisms can act upon them.
 * StatTracker provides an interface for interaction with these
 * stats, as well as a system for seamless synchronization between
 * client and server.
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
	public void registerProvider(StatProvider<?> provider) throws IllegalStateException;
	
	/**
	 * Attempts to unregister the target provider from this
	 * tracker instance. If there is no such provider registered,
	 * this method throws an exception.
	 * @param provider The target provider
	 * @throws IllegalStateException when the provider is not found inside this tracker
	 */
	public void removeProvider(StatProvider<?> provider) throws IllegalStateException;
	
	/**
	 * Searches for a {@link StatProvider provider} with the
	 * specified ID. If no such provider is found, null is
	 * returned by this method.
	 * @param identifier The {@link StatProvider#getStatID()} return value of the searched stat
	 * @return The target stat provider, or null if not found.
	 */
	public StatProvider<?> getProvider(ResourceLocation identifier);
	
	/**
	 * @return The set of all registered providers in this tracker.
	 */
	public Collection<StatProvider<?>> getRegisteredProviders();
	
	/**
	 * Assigns or replaces a {@link StatRecord}
	 * to the specified {@link StatProvider}.
	 * @param stat The stat to assign the record to
	 * @param value The record to assign
	 */
	public <RECORD extends StatRecord> void setRecord(StatProvider<RECORD> stat, RECORD value);
	
	/**
	 * Returns the record about the specified stat. If
	 * the {@link StatProvider} is not registered in
	 * this StatTracker instance, null is returned.
	 * @param stat The target stat provider
	 * @return The record regarding the stat provided or null if the stat is not tracked by this tracker.
	 */
	public <RECORD extends StatRecord> RECORD getRecord(StatProvider<RECORD> stat);
	
	/**
	 * Called each tick to update the stat tracker
	 * and the records stored about each stat. This
	 * method generally involves iterating over the
	 * set returned by {@link #getRegisteredProviders()},
	 * getting the currently stored record value, running the
	 * {@link StatProvider#update(EntityPlayer, StatRecord)}.
	 * @param player The player to apply the update to
	 */
	public void update(EntityPlayer player);
	
	/**
	 * Checks whether the provided {@link StatProvider}
	 * will be updated on next tick. When supplied with
	 * the player parameter and the method returns true,
	 * the stat is guaranteed to have received update this
	 * tick. One may also supply null as the player parameter,
	 * in which case the method only checks whether the stat
	 * has been suspended from updating or not.
	 * @see #setSuspended(StatProvider, boolean)
	 * @param stat The stat to run the check for
	 * @param player The player to check for the update, or null to check only the suspended state
	 * @return
	 * 	True if the player is provided and they passed all the checks, false otherwise.
	 * 	If player is null, returns true if the stat is NOT suspended, false otherwise.
	 */
	public boolean isActive(StatProvider<?> stat, @Nullable EntityPlayer player);
	
	/**
	 * Suspends or resumes the specified stat. Suspending a stat causes the stat
	 * to skip running it's provider {@link StatProvider#update(EntityPlayer, StatRecord) update}
	 * method until it is resumed. Generally, suspending a stat temporarily disables
	 * any effect it has on the player whatsoever. The suspended status of a stat can
	 * be checked by passing <i>null</i> as player parameter to {@link #isActive(StatProvider, EntityPlayer)}.
	 * @see StatProvider#update(EntityPlayer, StatRecord)
	 * @see #isActive(StatProvider, EntityPlayer)
	 * @param stat The stat to set the suspended status for
	 * @param suspended The suspended status
	 */
	public void setSuspended(StatProvider<?> stat, boolean suspended);
}