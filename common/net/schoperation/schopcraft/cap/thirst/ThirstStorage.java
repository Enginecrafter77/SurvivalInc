package net.schoperation.schopcraft.cap.thirst;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

// saves the thirst data for each player
public class ThirstStorage implements IStorage<IThirst> {
	
	@Override
	public NBTBase writeNBT(Capability<IThirst> capability, IThirst instance, EnumFacing side) {
		
		NBTTagCompound compound = new NBTTagCompound();
		
		compound.setFloat("thirstLevel", instance.getThirst());
		compound.setFloat("maxThirstLevel", instance.getMaxThirst());
		compound.setFloat("minThirstLevel", instance.getMinThirst());
		
		return compound;
	}
	
	@Override
	public void readNBT(Capability<IThirst> capability, IThirst instance, EnumFacing side, NBTBase nbt) {
		
		if (nbt instanceof NBTTagCompound) {
			
			NBTTagCompound compound = (NBTTagCompound) nbt;
		
			if (compound.hasKey("thirstLevel") && compound.hasKey("maxThirstLevel") && compound.hasKey("minThirstLevel")) {
			
				instance.set(compound.getFloat("thirstLevel"));
				instance.setMax(compound.getFloat("maxThirstLevel"));
				instance.setMin(compound.getFloat("minThirstLevel"));
			}
		}
	}
}
