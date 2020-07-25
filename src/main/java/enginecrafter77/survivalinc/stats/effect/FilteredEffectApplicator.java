package enginecrafter77.survivalinc.stats.effect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import net.minecraft.entity.player.EntityPlayer;

/**
 * FilteredEffectApplicator is the recommended implementation
 * of {@link EffectApplicator}. FilteredEffectApplicator features
 * a mechanism known as "The Filters". Each {@link StatEffect}
 * can have it's own set of filters. These filters are objects
 * implementing {@link EffectFilter} interface, which closely
 * resembles a bi-predicate. Each time an effect is about to
 * be applied, all of it's filters are checked. If at least
 * one of it's filters returns false from it's
 * {@link EffectFilter#isApplicableFor(EntityPlayer, float)},
 * method, the effect is not going to be applied. FilteredEffectApplicator
 * internally uses {@link LinkedHashMap} to satisfy predictable order
 * recommendation of EffectApplicator. Moreover, this particular
 * implementation uses {@link ArrayList} to store the filters as
 * a key in the aforementioned {@link #registry map}. Extending
 * implementations are free to provide their own containers, as
 * long at the container implements {@link Collection} interface.
 * @author Enginecrafter77
 */
public class FilteredEffectApplicator extends EffectApplicator {
	
	/**
	 * The map storing the internal mapping of {@link StatEffect} to their collections of filters.
	 * @see #createNewFilterSet()
	 */
	public final LinkedHashMap<StatEffect, Collection<EffectFilter>> registry;
	
	public FilteredEffectApplicator()
	{
		this.registry = new LinkedHashMap<StatEffect, Collection<EffectFilter>>();
	}
	
	/**
	 * Adds the specified effect optionally followed by
	 * a list of it's {@link EffectFilter filters}.
	 * @param effect The effect to be registered
	 * @param tests It's effect filters
	 */
	public void addEffect(StatEffect effect, EffectFilter... filters)
	{
		Collection<EffectFilter> filterset = this.createNewFilterSet();
		for(EffectFilter filter : filters) filterset.add(filter);
		this.registry.put(effect, filterset);
	}
	
	/**
	 * Adds a filter to the specified stat effect.
	 * @param effect The effect to register the filter to
	 * @param test The filter to register
	 */
	public void addFilter(StatEffect effect, EffectFilter filter)
	{
		this.registry.get(effect).add(filter);
	}
	
	/**
	 * Creates a new collection used to store the stat effect's filters.
	 * @return A new collection to store the stat effect's filters.
	 */
	protected Collection<EffectFilter> createNewFilterSet()
	{
		return new ArrayList<EffectFilter>(1);
	}
	
	@Override
	protected float applyEffect(StatEffect effect, EntityPlayer player, float value)
	{
		for(EffectFilter test : this.registry.get(effect))
		{
			if(!test.isApplicableFor(player, value))
			{
				return value;
			}
		}
		
		return effect.apply(player, value);
	}

	@Override
	protected Collection<StatEffect> getEffectSet()
	{
		return this.registry.keySet();
	}
	
	/**
	 * A simple interface which is used to check
	 * whether a stat effect is applicable or not.
	 * @author Enginecrafter77
	 */
	@FunctionalInterface
	public static interface EffectFilter
	{
		/**
		 * @param player The player the stat effect will be run for
		 * @param value The current value of the stat
		 * @return True if the stat effect should run, false otherwise
		 */
		public boolean isApplicableFor(EntityPlayer player, float value);
	}

}
