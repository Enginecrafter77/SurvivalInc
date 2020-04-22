package enginecrafter77.survivalinc.stats;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

/**
 * A class used to serialize and deserialize {@link StatTracker trackers}
 * and their associated records.
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
		for(StatProvider provider : instance.getRegisteredProviders())
			compound.setTag(provider.getStatID(), instance.getRecord(provider).serializeNBT());
		return compound;
	}

	@Override
	public void readNBT(Capability<StatTracker> capability, StatTracker instance, EnumFacing side, NBTBase nbt)
	{
		if(nbt instanceof NBTTagCompound)
		{
			NBTTagCompound compound = (NBTTagCompound)nbt;
			for(StatProvider provider : instance.getRegisteredProviders())
			{
				String id = provider.getStatID();
				if(compound.hasKey(id))
				{
					NBTTagCompound record = compound.getCompoundTag(id);
					StatRecordEntry entry = new StatRecordEntry();
					entry.deserializeNBT(record);
					instance.setRecord(provider, entry);
				}
				else System.err.format("Error: Requested stat %s not defined in saved NBT!\n", id);
			}
		}
	}

}
