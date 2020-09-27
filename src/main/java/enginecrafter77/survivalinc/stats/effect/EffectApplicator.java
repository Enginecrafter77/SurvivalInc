package enginecrafter77.survivalinc.stats.effect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import enginecrafter77.survivalinc.stats.StatRecord;
import net.minecraft.entity.player.EntityPlayer;

public class EffectApplicator<RECORD extends StatRecord> implements StatEffect<RECORD> {
	
	public final Collection<EffectFilterContainer> registry;
	
	public EffectApplicator()
	{
		this.registry = new LinkedList<EffectFilterContainer>();
	}
	
	public EffectFilterContainer add(StatEffect<? super RECORD> effect)
	{
		EffectFilterContainer container = new EffectFilterContainer(effect);
		this.registry.add(container);
		return container;
	}
	
	@Override
	public void apply(RECORD record, EntityPlayer player)
	{
		for(EffectFilterContainer container : this.registry)
			container.checkAndApply(record, player);
	}
	
	public class EffectFilterContainer	
	{
		public final StatEffect<? super RECORD> effect;
		public final Collection<EffectFilter<? super RECORD>> filters;
		
		public EffectFilterContainer(StatEffect<? super RECORD> effect)
		{
			this.effect = effect;
			this.filters = new ArrayList<EffectFilter<? super RECORD>>(1);
		}
		
		public EffectFilterContainer addFilter(EffectFilter<? super RECORD> filter)
		{
			this.filters.add(filter);
			return this;
		}
		
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