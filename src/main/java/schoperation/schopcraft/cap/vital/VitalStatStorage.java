package schoperation.schopcraft.cap.vital;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

// Saves thirst data for each player.
public class VitalStatStorage implements IStorage<VitalStat> {

	@Override
	public NBTBase writeNBT(Capability<VitalStat> capability, VitalStat instance, EnumFacing side)
	{
		NBTTagCompound compound = new NBTTagCompound();
		for(VitalStatType type : VitalStatType.values())
			compound.setFloat(type.getStatID(), instance.getStat(type));
		return compound;
	}

	@Override
	public void readNBT(Capability<VitalStat> capability, VitalStat instance, EnumFacing side, NBTBase nbt)
	{
		if(nbt instanceof NBTTagCompound)
		{
			NBTTagCompound compound = (NBTTagCompound)nbt;
			for(VitalStatType type : VitalStatType.values())
			{
				String id = type.getStatID();
				if(compound.hasKey(id))
					instance.setStat(type, compound.getFloat(id));
				else
					System.err.format("Error: Requested stat %s not defined in saved NBT!\n", id);
			}
		}
	}
}