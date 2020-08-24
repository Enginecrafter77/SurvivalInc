package enginecrafter77.survivalinc.stats;

import java.util.HashMap;
import java.util.Set;

import javax.annotation.Nonnull;

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
	
	/** A map storing the last change to each stat */
	private final HashMap<StatProvider, Float> changelog;
	
	/**
	 * Creates a new empty SimpleStatProvider instance
	 * with no {@link StatProvider}s registered.
	 */
	public SimpleStatRegister()
	{
		this.changelog = new HashMap<StatProvider, Float>();
	}
	
	@Override
	public void registerProvider(StatProvider provider)
	{
		if(this.containsKey(provider)) throw new IllegalStateException("Provider " + provider.getClass().getCanonicalName() + " already registered!");
		this.setRecord(provider, provider.createNewRecord());
		this.changelog.put(provider, 0F);
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
	public void modifyStat(StatProvider stat, float amount) throws IllegalStateException
	{
		StatRecord record = this.getNonNullRecord(stat);
		record.setValue(record.getValue() + amount);
	}

	@Override
	public void setStat(StatProvider stat, float amount) throws IllegalStateException
	{
		this.getNonNullRecord(stat).setValue(amount);
	}
	
	@Override
	public float getStat(StatProvider stat) throws IllegalStateException
	{
		return this.getNonNullRecord(stat).getValue();
	}
	
	@Override
	public void update(EntityPlayer player)
	{
		for(StatProvider provider : this.keySet())
		{
			if(provider.isAcitve(player))
			{
				StatRecord record = this.getRecord(provider);
				float value = record.getValue();
				float newvalue = provider.updateValue(player, value);
				this.changelog.put(provider, newvalue - value);
				record.setValue(newvalue);
			}
		}
	}
	
	@Override
	public float getLastChange(StatProvider stat)
	{
		return this.changelog.get(stat);
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
	
	/**
	 * A simple helper used to check whether a stat provider is registered in delegate functions.
	 * @param stat The stat to get the record for
	 * @return The record associated (guaranteed to not be null)
	 * @throws IllegalStateException If the record is null
	 */
	@Nonnull
	private StatRecord getNonNullRecord(StatProvider stat) throws IllegalStateException
	{
		StatRecord record = this.getRecord(stat);
		if(record == null) throw new IllegalStateException("Stat " + stat.getStatID().toString() + " has no record in this stat tracker!");
		return record;
	}
}
