package enginecrafter77.survivalinc.stats.effect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import enginecrafter77.survivalinc.stats.StatRecord;
import net.minecraft.entity.player.EntityPlayer;

/**
 * EffectApplicator is essentially a container for {@link StatEffect}s,
 * which can be used to periodically apply those effects. Moreover,
 * EffectApplicator provides filtering mechanism using the {@link EffectFilterContainer}
 * wrapper class. Instances of this class are dynamically created by
 * {@link #createNewContainer(StatEffect)} method. These containers are
 * then returned, so the programmer can use the returned instance to add
 * the filters to the effect. Since both the effect and the filters are
 * required to be a superclass of the EffectApplicator's record type,
 * it's perfectly possible for the effect's type to differ from the
 * filter's type, as long as both are assignable to the EffectApplicator's
 * record type. 
 * @author Enginecrafter77
 * @param <RECORD> The type of records this EffectApplicator accepts.
 */
public class EffectApplicator<RECORD extends StatRecord> implements StatEffect<RECORD> {
	
	/** The list holding the filter containers */
	public final Collection<EffectFilterContainer> registry;
	
	/** Creates an empty EffectApplicator instance. */
	public EffectApplicator()
	{
		this.registry = new LinkedList<EffectFilterContainer>();
	}
	
	/**
	 * Creates an new container for the specified effect. Please note that
	 * this must be a freshly created {@link EffectFilterContainer}, otherwise
	 * unexpected errors may arise.
	 * @param effect The effect to create the container for
	 * @return A fresh instance of {@link EffectFilterContainer}
	 */
	protected EffectFilterContainer createNewContainer(StatEffect<? super RECORD> effect)
	{
		return new EffectFilterContainer(effect);
	}
	
	/**
	 * Adds a new {@link StatEffect} to the applicator. That means that
	 * on each call to the applicator's {@link #apply(StatRecord, EntityPlayer)}
	 * method, this effect will be run depending on the applied filters.
	 * @param effect The effect to add
	 * @return {@link EffectFilterContainer}, which can be used to register filters to the effect.
	 */
	public EffectFilterContainer add(StatEffect<? super RECORD> effect)
	{
		EffectFilterContainer container = this.createNewContainer(effect);
		this.registry.add(container);
		return container;
	}
	
	@Override
	public void apply(RECORD record, EntityPlayer player)
	{
		for(EffectFilterContainer container : this.registry)
			container.checkAndApply(record, player);
	}
	
	/**
	 * EffectFilterContainer is a wrapper class for a {@link StatEffect}
	 * and it's associated filters. EffectFilterContainer basically allows
	 * filters with incompatible type to be coupled with effects with incompatible
	 * type, as long as they share a common subclass provided as the type
	 * argument for the enclosing {@link EffectApplicator} instance.
	 * @author Enginecrafter77
	 */
	public class EffectFilterContainer	
	{
		/** The effect that is being filtered */
		public final StatEffect<? super RECORD> effect;
		
		/** A collection of all the filters */
		public final Collection<EffectFilter<? super RECORD>> filters;
		
		public EffectFilterContainer(StatEffect<? super RECORD> effect)
		{
			this.effect = effect;
			this.filters = new ArrayList<EffectFilter<? super RECORD>>(1);
		}
		
		/**
		 * A builder-like method used to add filters to the container.
		 * @param filter The filter to add to the container
		 * @return The instance of this class
		 */
		public EffectFilterContainer addFilter(EffectFilter<? super RECORD> filter)
		{
			this.filters.add(filter);
			return this;
		}
		
		/**
		 * Checks the result of all of the filters. If all the filters
		 * returned true, the effect is applied. If any of the filters
		 * returned false, nothing happens.
		 * @param record The common subclass record
		 * @param player The player subjected to the procedure
		 */
		protected void checkAndApply(RECORD record, EntityPlayer player)
		{
			for(EffectFilter<? super RECORD> test : this.filters)
			{
				if(!test.isApplicableFor(record, player)) return;
			}
			
			effect.apply(record, player);
		}
	}
}