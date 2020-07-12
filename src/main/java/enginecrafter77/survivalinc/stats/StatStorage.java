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
			compound.setTag(provider.getStatID().toString(), instance.getRecord(provider).serializeNBT());
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
				String id = provider.getStatID().toString();
				if(compound.hasKey(id))
				{
					NBTTagCompound entry = compound.getCompoundTag(id);
					StatRecord record = provider.createNewRecord();
					record.deserializeNBT(entry);
					instance.setRecord(provider, record);
				}
				else System.err.format("Error: Requested stat %s not defined in saved NBT!\n", id);
			}
		}
	}

}
