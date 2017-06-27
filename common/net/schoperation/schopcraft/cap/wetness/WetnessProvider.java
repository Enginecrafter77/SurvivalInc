package net.schoperation.schopcraft.cap.wetness;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

// this provides the mechanic/capability to the players
public class WetnessProvider implements ICapabilitySerializable<NBTBase> {
	
	@CapabilityInject(IWetness.class)
	public static final Capability<IWetness> WETNESS_CAP = null;
	
	private IWetness instance =  WETNESS_CAP.getDefaultInstance();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		
		return capability == WETNESS_CAP;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		
		return capability == WETNESS_CAP ? WETNESS_CAP.<T> cast(this.instance) : null;
	}
	
	@Override
	public NBTBase serializeNBT() {
		
		return WETNESS_CAP.getStorage().writeNBT(WETNESS_CAP, this.instance, null);
	}
	
	@Override
	public void deserializeNBT(NBTBase nbt) {
		
		WETNESS_CAP.getStorage().readNBT(WETNESS_CAP, this.instance, null, nbt);
	}

}
