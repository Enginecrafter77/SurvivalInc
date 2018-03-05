package schoperation.schopcraft.cap.temperature;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

// Save temperature data for each player.
public class TemperatureStorage implements IStorage<ITemperature> {
	
	@Override
	public NBTBase writeNBT(Capability<ITemperature> capability, ITemperature instance, EnumFacing side) {
		
		NBTTagCompound compound = new NBTTagCompound();
		
		compound.setFloat("temperatureLevel", instance.getTemperature());
		compound.setFloat("maxTemperatureLevel", instance.getMaxTemperature());
		compound.setFloat("minTemperatureLevel", instance.getMinTemperature());
		compound.setFloat("targetTemperature", instance.getTargetTemperature());
		
		return compound;
	}
	
	@Override
	public void readNBT(Capability<ITemperature> capability, ITemperature instance, EnumFacing side, NBTBase nbt) {
		
		if (nbt instanceof NBTTagCompound) {
			
			NBTTagCompound compound = (NBTTagCompound) nbt;
		
			if (compound.hasKey("temperatureLevel") && compound.hasKey("maxTemperatureLevel") && compound.hasKey("minTemperatureLevel") && compound.hasKey("targetTemperature")) {
			
				instance.set(compound.getFloat("temperatureLevel"));
				instance.setMax(compound.getFloat("maxTemperatureLevel"));
				instance.setMin(compound.getFloat("minTemperatureLevel"));
				instance.setTarget(compound.getFloat("targetTemperature"));
			}
		}
	}
}