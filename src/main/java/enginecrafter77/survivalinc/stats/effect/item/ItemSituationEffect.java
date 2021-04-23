package enginecrafter77.survivalinc.stats.effect.item;

import java.util.HashMap;
import java.util.Map;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ItemSituationEffect {
	
	public final Map<ResourceLocation, Float> effects;
	
	private final EffectiveSituationCastAdapter<? extends Event> situation;
	
	public <TYPE extends Event> ItemSituationEffect(EffectiveSituation<TYPE> situation)
	{
		this.effects = new HashMap<ResourceLocation, Float>();
		this.situation = new EffectiveSituationCastAdapter<TYPE>(situation);
	}
	
	public ItemSituationEffect addEffect(ResourceLocation id, Float value)
	{
		this.effects.put(id, value);
		return this;
	}
	
	public boolean apply(Event event)
	{
		if(this.situation.isTriggered(event))
		{
			this.apply(this.situation.getPlayer(event).getCapability(StatCapability.target, null));
			return true;
		}
		return false;
	}
	
	protected void apply(StatTracker tracker)
	{
		for(Map.Entry<ResourceLocation, Float> entry : this.effects.entrySet())
		{
			StatProvider<?> provider = tracker.getProvider(entry.getKey());
			if(provider == null)
			{
				SurvivalInc.logger.error("No such stat provider with ID \"{}\"!", entry.getKey());
				continue;
			}
			
			if(SimpleStatRecord.class.isAssignableFrom(provider.getRecordClass()))
			{
				SimpleStatRecord record = (SimpleStatRecord)tracker.getRecord(provider);
				record.addToValue(entry.getValue());
			}
			else
			{
				SurvivalInc.logger.error("StatProvider with ID \"{}\" does not use SimpleStatRecord-type records!", entry.getKey());
			}
		}
	}
	
	private class EffectiveSituationCastAdapter<TYPE extends Event> implements EffectiveSituation<TYPE>
	{
		public final EffectiveSituation<TYPE> situation;
		
		public EffectiveSituationCastAdapter(EffectiveSituation<TYPE> situation)
		{
			this.situation = situation;
		}
		
		@Override
		public boolean isTriggered(Event event)
		{
			if(this.situation.getEventClass().isInstance(event)) // We only want specific events
			{
				return this.situation.isTriggered(this.getEventClass().cast(event));
			}
			return false;
		}

		@Override
		public EntityPlayer getPlayer(Event event)
		{
			if(!this.situation.getEventClass().isInstance(event)) throw new UnsupportedOperationException("This situation is not suitable for given event");
			
			return this.situation.getPlayer(this.getEventClass().cast(event));
		}

		@Override
		public Class<TYPE> getEventClass()
		{
			return this.situation.getEventClass();
		}
	}
	
}
