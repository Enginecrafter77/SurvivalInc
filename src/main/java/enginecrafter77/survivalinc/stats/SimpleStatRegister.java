package enginecrafter77.survivalinc.stats;

import java.util.HashMap;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * SimpleStatRegister is the default implementation of StatTracker.
 * It features all the basic facilities and principles recommended
 * by StatTracker definition.
 * @author Enginecrafter77
 */
public class SimpleStatRegister extends HashMap<StatProvider, StatRecord> implements StatTracker {
	private static final long serialVersionUID = -878624371786181967L;
	
	@Override
	public void registerProvider(StatProvider provider)
	{
		if(this.containsKey(provider)) throw new IllegalStateException("Provider " + provider.getClass().getCanonicalName() + " already registered!");
		this.setRecord(provider, provider.createNewRecord());
	}
	
	@Override
	public void removeProvider(StatProvider provider)
	{
		if(this.getProvider(provider.getStatID()) == null)
			throw new IllegalStateException("Provider " + provider.getClass().getCanonicalName() + " was never registered!");
		this.remove(provider);
	}
	
	@Override
	public StatProvider getProvider(ResourceLocation identifier)
	{
		for(StatProvider provider : this.keySet())
		{
			if(provider.getStatID().equals(identifier))
				return provider;
		}
		return null;
	}
	
	@Override
	public void setRecord(StatProvider stat, StatRecord value)
	{
		this.put(stat, value);
	}
	
	@Override
	public StatRecord getRecord(StatProvider stat)
	{
		return this.get(stat);
	}
	
	@Override
	public void update(EntityPlayer player)
	{
		for(StatProvider provider : this.keySet())
		{
			StatRecord record = this.getRecord(provider);
			provider.update(player, record);
		}
	}

	@Override
	public Set<StatProvider> getRegisteredProviders()
	{
		return this.keySet();
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getSimpleName());
		builder.append('[');
		for(Entry<StatProvider, StatRecord> entry : this.entrySet())
		{
			builder.append(entry.getKey().getStatID().toString());
			builder.append(": ");
			builder.append(entry.getValue().toString());
			builder.append(", ");
		}
		builder.setLength(builder.length() - 2);
		builder.append(']');
		return builder.toString();
	}
}
