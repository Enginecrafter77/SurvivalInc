package enginecrafter77.survivalinc.stats.modifier.ng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

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
			if(!test.test(player, value))
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
	
	public static class EffectFilter implements BiPredicate<EntityPlayer, Float>
	{
		public BiPredicate<EntityPlayer, Float> delegate;
		
		public EffectFilter(BiPredicate<EntityPlayer, Float> delegate)
		{
			this.delegate = delegate;
		}
		
		public EffectFilter(Predicate<Float> check)
		{
			this.delegate = (EntityPlayer player, Float value) -> check.test(value);
		}
		
		@Override
		public boolean test(EntityPlayer t, Float u)
		{
			return this.delegate.test(t, u);
		}
		
		public EffectFilter invert()
		{
			return new EffectFilter((EntityPlayer player, Float value) -> !this.delegate.test(player, value));
		}
	}

}
