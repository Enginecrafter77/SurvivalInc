package enginecrafter77.survivalinc.stats;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

import javax.annotation.Nonnull;

/**
 * A class used to serialize and deserialize {@link StatTracker trackers} and their associated records.
 * @author Enginecrafter77
 */
public class StatStorage implements IStorage<StatTracker> {
	
	/** The singleton instance of the serializer/deserializer */
	public static final IStorage<StatTracker> instance = new StatStorage();
	
	/** The singleton constructor */
	private StatStorage() {}
	
	@Override
	public NBTBase writeNBT(Capability<StatTracker> capability, StatTracker instance, EnumFacing side)
	{
		NBTTagCompound compound = new NBTTagCompound();
		for(StatProvider<?> provider : instance.getRegisteredProviders())
		{
			String key = provider.getStatID().toString();
			NBTBase data = this.getNonnullRecord(instance, provider).serializeNBT();
			compound.setTag(key, data);
		}
		return compound;
	}
	
	@Override
	public void readNBT(Capability<StatTracker> capability, StatTracker instance, EnumFacing side, NBTBase nbt)
	{
		if(nbt instanceof NBTTagCompound)
		{
			NBTTagCompound compound = (NBTTagCompound)nbt;
			for(StatProvider<?> provider : instance.getRegisteredProviders())
			{
				String id = provider.getStatID().toString();
				if(compound.hasKey(id))
				{
					NBTTagCompound entry = compound.getCompoundTag(id);
					this.getNonnullRecord(instance, provider).deserializeNBT(entry);
				}
				else
				{
					SurvivalInc.logger.warn("Requested stat {} not defined in provided NBT!", id);
				}
			}
		}
		else
		{
			SurvivalInc.logger.error("Malformed stat capability NBT data.");
		}
	}

	@Nonnull
	protected <REC extends StatRecord> REC getNonnullRecord(StatTracker tracker, StatProvider<REC> provider)
	{
		REC record = tracker.getRecord(provider);
		if(record == null)
			throw new IllegalStateException("Reportedly registered provider has no record!");
		return record;
	}

}
