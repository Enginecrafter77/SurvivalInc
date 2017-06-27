package net.schoperation.schopcraft.cap.wetness;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

// saves the wetness data for each player
public class WetnessStorage implements IStorage<IWetness> {
	
	@Override
	public NBTBase writeNBT(Capability<IWetness> capability, IWetness instance, EnumFacing side) {
		
		return new NBTTagFloat(instance.getWetness());
	}
	
	@Override
	public void readNBT(Capability<IWetness> capability, IWetness instance, EnumFacing side, NBTBase nbt) {
		
		instance.set(((NBTPrimitive) nbt).getFloat());
	}

}
