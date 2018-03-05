package schoperation.schopcraft.cap.wetness;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

// Saves the wetness data for each player.
public class WetnessStorage implements IStorage<IWetness> {
	
	@Override
	public NBTBase writeNBT(Capability<IWetness> capability, IWetness instance, EnumFacing side) {
		
		NBTTagCompound compound = new NBTTagCompound();
		
		compound.setFloat("wetnessLevel", instance.getWetness());
		compound.setFloat("maxWetnessLevel", instance.getMaxWetness());
		compound.setFloat("minWetnessLevel", instance.getMinWetness());
		
		return compound;
	}
	
	@Override
	public void readNBT(Capability<IWetness> capability, IWetness instance, EnumFacing side, NBTBase nbt) {
		
		if (nbt instanceof NBTTagCompound) {
			
			NBTTagCompound compound = (NBTTagCompound) nbt;
		
			if (compound.hasKey("wetnessLevel") && compound.hasKey("maxWetnessLevel") && compound.hasKey("minWetnessLevel")) {
			
				instance.set(compound.getFloat("wetnessLevel"));
				instance.setMax(compound.getFloat("maxWetnessLevel"));
				instance.setMin(compound.getFloat("minWetnessLevel"));
			}
		}
	}
}