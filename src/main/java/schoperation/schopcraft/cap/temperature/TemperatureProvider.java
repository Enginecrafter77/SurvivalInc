package schoperation.schopcraft.cap.temperature;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

// Provides temperature mechanic to the player.
public class TemperatureProvider implements ICapabilitySerializable<NBTBase> {
	
	@CapabilityInject(ITemperature.class)
	public static final Capability<ITemperature> TEMPERATURE_CAP = null;
	
	private ITemperature instance = TEMPERATURE_CAP.getDefaultInstance();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		
		return capability == TEMPERATURE_CAP;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		
		return capability == TEMPERATURE_CAP ? TEMPERATURE_CAP.<T> cast(this.instance) : null;
	}
	
	@Override
	public NBTBase serializeNBT() {
		
		return TEMPERATURE_CAP.getStorage().writeNBT(TEMPERATURE_CAP, this.instance, null);
	}
	
	@Override
	public void deserializeNBT(NBTBase nbt) {
		
		TEMPERATURE_CAP.getStorage().readNBT(TEMPERATURE_CAP, this.instance, null, nbt);
	}
}