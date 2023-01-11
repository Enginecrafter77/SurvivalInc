package enginecrafter77.survivalinc.stats;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * SimpleStatRegister is the default implementation of StatTracker. It features all the basic facilities and principles recommended by StatTracker definition.
 * @author Enginecrafter77
 */
public class SimpleStatRegister implements StatTracker {
	/** A map of the stat provider IDs with the {@link SimpleStatRegisterEntry} */
	public final Map<ResourceLocation, SimpleStatRegisterEntry> statmap;
	
	public SimpleStatRegister()
	{
		this.statmap = new HashMap<ResourceLocation, SimpleStatRegisterEntry>();
	}
	
	@Override
	public void registerProvider(StatProvider<?> provider)
	{
		ResourceLocation identifier = provider.getStatID();
		if(this.statmap.containsKey(identifier))
			throw new IllegalStateException("Provider " + identifier + " already registered!");
		this.statmap.put(identifier, this.createNewEntry(provider));
	}
	
	@Override
	public void removeProvider(StatProvider<?> provider)
	{
		ResourceLocation identifier = provider.getStatID();
		if(!this.statmap.containsKey(identifier))
			throw new IllegalStateException("Provider " + identifier + " was never registered!");
		this.statmap.remove(identifier);
	}

	@Nullable
	@Override
	public StatProvider<?> getProvider(ResourceLocation identifier)
	{
		SimpleStatRegisterEntry entry = this.statmap.get(identifier);
		return entry == null ? null : entry.provider;
	}
	
	@Override
	public <RECORD extends StatRecord> void setRecord(StatProvider<RECORD> stat, RECORD value)
	{
		this.getEntry(stat).setRecord(value);
	}
	
	@Override
	public <RECORD extends StatRecord> RECORD getRecord(StatProvider<RECORD> stat)
	{
		SimpleStatRegisterEntry entry = this.getEntry(stat);
		return stat.getRecordClass().cast(entry.getRecord());
	}
	
	@Override
	public void update(EntityPlayer player)
	{
		this.statmap.values().forEach((SimpleStatRegisterEntry entry) -> entry.tick(player));
	}
	
	@Override
	public Collection<StatProvider<?>> getRegisteredProviders()
	{
		Set<StatProvider<?>> providers = new HashSet<StatProvider<?>>(this.statmap.size());
		for(SimpleStatRegisterEntry entry : this.statmap.values())
			providers.add(entry.provider);
		return providers;
	}
	
	@Override
	public String toString()
	{
		return this.statmap.toString();
	}
	
	@Override
	public boolean isActive(StatProvider<?> stat, @Nullable EntityPlayer player)
	{
		SimpleStatRegisterEntry entry = this.getEntry(stat);
		return player == null ? entry.isActive() : entry.isActiveFor(player);
	}
	
	@Override
	public void setSuspended(StatProvider<?> stat, boolean suspended)
	{
		this.getEntry(stat).setActive(!suspended);
	}
	
	/**
	 * Returns the internally associated {@link SimpleStatRegisterEntry}.
	 * @param stat The stat provider
	 * @throws UnknownStatException If the given provider is not registered inside this tracker
	 * @return Internally associated {@link SimpleStatRegisterEntry}, or null if no such entry exists
	 */
	@Nonnull
	public SimpleStatRegisterEntry getEntry(StatProvider<?> stat)
	{
		SimpleStatRegisterEntry entry = this.statmap.get(stat.getStatID());
		if(entry == null)
			throw new UnknownStatException(stat);
		return entry;
	}
	
	/**
	 * Creates a new {@link SimpleStatRegisterEntry} to be used with this implementation of {@link SimpleStatRegister}.
	 * @param stat The stat provider for which the entry is being created
	 * @return A new instance of {@link SimpleStatRegisterEntry} associated to the supplied provider.
	 */
	protected SimpleStatRegisterEntry createNewEntry(StatProvider<?> stat)
	{
		return new SimpleStatRegisterEntry(stat);
	}
	
	protected static class SimpleStatRegisterEntry {
		public final StatProvider<?> provider;
		public final boolean runInCreative;
		protected StatRecord record;
		private boolean shouldTick;
		
		public SimpleStatRegisterEntry(StatProvider<?> provider)
		{
			this.record = provider.createNewRecord();
			this.runInCreative = false;
			this.provider = provider;
			this.shouldTick = true;
		}
		
		public void tick(EntityPlayer player)
		{
			if(this.isActiveFor(player))
			{
				this.updateGeneric(player, this.provider);
			}
		}
		
		public boolean isActiveFor(EntityPlayer player)
		{
			return this.isActive() && (this.runInCreative || !player.isCreative()) && !player.isSpectator();
		}
		
		public void setRecord(StatRecord record)
		{
			this.record = record;
		}
		
		public StatRecord getRecord()
		{
			return this.record;
		}
		
		public void setActive(boolean active)
		{
			this.shouldTick = active;
		}
		
		public boolean isActive()
		{
			return this.shouldTick;
		}
		
		private <RECORD extends StatRecord> void updateGeneric(EntityPlayer target, StatProvider<RECORD> provider)
		{
			RECORD specificrecord = provider.getRecordClass().cast(this.record);
			provider.update(target, specificrecord);
		}
		
		@Override
		public String toString()
		{
			return String.format("%s(%s:%s; A: %b, C: %b)", this.provider.getClass().getSimpleName(), this.provider.getRecordClass().getSimpleName(), this.record.toString(), this.shouldTick, this.runInCreative);
		}
	}
}
