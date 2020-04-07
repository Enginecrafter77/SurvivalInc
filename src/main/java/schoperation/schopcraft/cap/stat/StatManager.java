package schoperation.schopcraft.cap.stat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public class StatManager extends HashMap<StatProvider, Float> implements StatTracker {
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
		this.setStat(provider, provider.getDefault());
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
			if(provider.getStatID() == identifier)
				return provider;
		}
		return null;
	}
	
	@Override
	public void modifyStat(StatProvider stat, float amount)
	{
		this.setStat(stat, this.getStat(stat) + amount);
	}

	@Override
	public void setStat(StatProvider stat, float amount)
	{
		this.put(stat, stat.getOverflowHandler().apply(stat, amount));
	}
	
	@Override
	public float getStat(StatProvider stat)
	{
		return this.get(stat);
	}
	
	@Override
	public void update(EntityPlayer player)
	{
		for(StatProvider provider : this.keySet())
			this.setStat(provider, provider.updateValue(player, this.getStat(provider)));
	}

	@Override
	public Iterator<Entry<StatProvider, Float>> iterator()
	{
		return this.entrySet().iterator();
	}
}
