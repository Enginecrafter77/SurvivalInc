package schoperation.schopcraft.cap.vital;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class VitalStatProvider implements ICapabilitySerializable<NBTBase> {

	@CapabilityInject(VitalStat.class)
	public static final Capability<VitalStat> VITAL_CAP = null;
	private VitalStat instance = VITAL_CAP.getDefaultInstance();

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == VITAL_CAP;
	}

	@Override
	public <TARGET> TARGET getCapability(Capability<TARGET> capability, EnumFacing facing)
	{
		return this.hasCapability(capability, facing) ? VITAL_CAP.cast(this.instance) : null;
	}

	@Override
	public NBTBase serializeNBT()
	{
		return VITAL_CAP.getStorage().writeNBT(VITAL_CAP, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt)
	{
		VITAL_CAP.getStorage().readNBT(VITAL_CAP, this.instance, null, nbt);
	}
}