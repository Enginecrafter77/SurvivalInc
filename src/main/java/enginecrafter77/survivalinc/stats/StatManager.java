package enginecrafter77.survivalinc.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;

/**
 * StatManager is the default implementation of StatTracker.
 * It features all the basic facilities and principles recommended
 * by StatTracker definition.
 * @author Enginecrafter77
 */
public class StatManager extends HashMap<StatProvider, StatRecord> implements StatTracker {
	private static final long serialVersionUID = -878624371786181967L;
	
	public static List<StatProvider> providers = new LinkedList<StatProvider>();
	
	public StatManager()
	{
		for(StatProvider provider : StatManager.providers)
			this.registerProvider(provider);
	}
	
	@Override
	public void registerProvider(StatProvider provider)
	{
		if(this.getProvider(provider.getStatID()) != null)
			throw new IllegalStateException("Provider " + provider.getClass().getCanonicalName() + " already registered!");
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
	public StatProvider getProvider(String identifier)
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
	public void modifyStat(StatProvider stat, float amount)
	{
		StatRecord record = this.getRecord(stat);
		amount = stat.getOverflowHandler().apply(stat, record.getValue() + amount);
		record.setValue(amount);
	}

	@Override
	public void setStat(StatProvider stat, float amount)
	{
		StatRecord record = this.getRecord(stat);
		amount = stat.getOverflowHandler().apply(stat, amount);
		record.setValue(amount);
	}
	
	@Override
	public float getStat(StatProvider stat)
	{
		return this.getRecord(stat).getValue();
	}
	
	@Override
	public void update(EntityPlayer player)
	{
		for(StatProvider provider : this.keySet())
		{
			if(provider.isAcitve(player))
			{
				this.setStat(provider, provider.updateValue(player, this.getStat(provider)));
			}
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
		List<String> list = new ArrayList<String>(this.size());
		for(Entry<StatProvider, StatRecord> entry : this.entrySet())
			list.add(String.format("%s=%f", entry.getKey().getStatID(), entry.getValue().getValue()));
		return list.toString();
	}
}
