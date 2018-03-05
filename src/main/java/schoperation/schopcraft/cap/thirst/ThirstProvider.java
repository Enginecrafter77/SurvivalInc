package schoperation.schopcraft.cap.thirst;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

// Provides thirst mechanic to the player.
public class ThirstProvider implements ICapabilitySerializable<NBTBase> {
	
	@CapabilityInject(IThirst.class)
	public static final Capability<IThirst> THIRST_CAP = null;
	
	private IThirst instance = THIRST_CAP.getDefaultInstance();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		
		return capability == THIRST_CAP;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		
		return capability == THIRST_CAP ? THIRST_CAP.<T> cast(this.instance) : null;
	}
	
	@Override
	public NBTBase serializeNBT() {
		
		return THIRST_CAP.getStorage().writeNBT(THIRST_CAP, this.instance, null);
	}
	
	@Override
	public void deserializeNBT(NBTBase nbt) {
		
		THIRST_CAP.getStorage().readNBT(THIRST_CAP, this.instance, null, nbt);
	}
}