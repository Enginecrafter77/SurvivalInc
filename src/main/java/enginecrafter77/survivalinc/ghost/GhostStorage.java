package enginecrafter77.survivalinc.ghost;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

// Save ghost data for each player.
public class GhostStorage implements IStorage<Ghost> {

	@Override
	public NBTBase writeNBT(Capability<Ghost> capability, Ghost instance, EnumFacing side)
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setBoolean("status", instance.getStatus());
		compound.setFloat("energy", instance.getEnergy());
		return compound;
	}

	@Override
	public void readNBT(Capability<Ghost> capability, Ghost instance, EnumFacing side, NBTBase nbt)
	{
		if(nbt instanceof NBTTagCompound)
		{
			NBTTagCompound compound = (NBTTagCompound) nbt;
			if(compound.hasKey("status") && compound.hasKey("energy"))
			{
				instance.setStatus(compound.getBoolean("status"));
				instance.setEnergy(compound.getFloat("energy"));
			}
		}
	}
}