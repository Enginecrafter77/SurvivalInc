package net.schoperation.schopcraft.cap.sanity;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

// Provides sanity mechanic to the player.
public class SanityProvider implements ICapabilitySerializable<NBTBase> {
	
	@CapabilityInject(ISanity.class)
	public static final Capability<ISanity> SANITY_CAP = null;
	
	private ISanity instance = SANITY_CAP.getDefaultInstance();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		
		return capability == SANITY_CAP;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		
		return capability == SANITY_CAP ? SANITY_CAP.<T> cast(this.instance) : null;
	}
	
	@Override
	public NBTBase serializeNBT() {
		
		return SANITY_CAP.getStorage().writeNBT(SANITY_CAP, this.instance, null);
	}
	
	@Override
	public void deserializeNBT(NBTBase nbt) {
		
		SANITY_CAP.getStorage().readNBT(SANITY_CAP, this.instance, null, nbt);
	}
}