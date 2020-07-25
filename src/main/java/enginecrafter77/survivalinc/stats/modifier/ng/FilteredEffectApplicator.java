package enginecrafter77.survivalinc.stats.modifier.ng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import net.minecraft.entity.player.EntityPlayer;

public class FilteredEffectApplicator extends EffectApplicator {
	
	public final LinkedHashMap<StatEffect, Collection<EffectFilter>> registry;
	
	public FilteredEffectApplicator()
	{
		this.registry = new LinkedHashMap<StatEffect, Collection<EffectFilter>>();
	}
	
	public void addEffect(StatEffect effect, EffectFilter... tests)
	{
		Collection<EffectFilter> filters = this.createNewFilterSet();
		for(EffectFilter filter : tests) filters.add(filter);
		this.registry.put(effect, filters);
	}
	
	public void addFilter(StatEffect effect, EffectFilter test)
	{
		this.registry.get(effect).add(test);
	}
	
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
	
	public static interface EffectFilter
	{
		public boolean isApplicableFor(EntityPlayer player, float value);
	}

}
