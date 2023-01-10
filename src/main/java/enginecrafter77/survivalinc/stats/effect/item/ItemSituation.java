package enginecrafter77.survivalinc.stats.effect.item;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public abstract class ItemSituation<TYPE extends Event> {
	
	public final Map<ResourceLocation, Float> effects;
	
	public final Properties props;
	public final Item item;
	
	public ItemSituation(Item item, Properties props)
	{
		this.effects = new HashMap<ResourceLocation, Float>();
		this.props = props;
		this.item = item;
	}
	
	public abstract boolean isTriggeredBy(TYPE event);
	
	public abstract EntityPlayer getPlayer(TYPE event);
	
	public abstract Class<TYPE> getEventClass();
	
	public ItemSituation<TYPE> addEffect(ResourceLocation id, Float value)
	{
		this.effects.put(id, value);
		return this;
	}
	
	public StatProvider<?> resolve(StatTracker tracker, ResourceLocation id)
	{
		StatProvider<?> provider = tracker.getProvider(id);
		if(provider == null) throw new IllegalArgumentException();
		return provider;
	}
	
	@Deprecated
	public void apply(TYPE event)
	{
		if(this.isTriggeredBy(event))
			StatCapability.obtainTracker(this.getPlayer(event)).ifPresent(this::apply);
	}
	
	@Override
	public String toString()
	{
		return String.format("%s(%s; I: %s, P: %s, E: %s)", this.getClass().getSimpleName(), this.getEventClass().getSimpleName(), this.item.getRegistryName(), this.props, this.effects);
	}
	
	protected void apply(StatTracker tracker)
	{
		for(Map.Entry<ResourceLocation, Float> entry : this.effects.entrySet())
		{
			ResourceLocation id = entry.getKey();
			try
			{
				StatProvider<?> provider = this.resolve(tracker, id);
				
				if(SimpleStatRecord.class.isAssignableFrom(provider.getRecordClass()))
				{
					Optional.ofNullable(tracker.getRecord(provider)).map(SimpleStatRecord.class::cast).ifPresent(SimpleStatRecord.addF(entry.getValue()));
				}
				else
				{
					SurvivalInc.logger.error("StatProvider with ID \"{}\" does not use SimpleStatRecord-type records!", id);
					this.effects.remove(id); // Effect is invalid
				}
			}
			catch(IllegalArgumentException exc)
			{
				SurvivalInc.logger.warn("StatProvider with ID \"{}\" is not registered inside player's StatTracker!", id);
			}
		}
	}
}
