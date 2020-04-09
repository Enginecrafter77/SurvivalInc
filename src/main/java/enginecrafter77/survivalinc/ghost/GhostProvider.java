package enginecrafter77.survivalinc.ghost;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

// Provides ghost mechanic to the player.
public class GhostProvider implements ICapabilitySerializable<NBTBase> {

	@CapabilityInject(IGhost.class)
	public static final Capability<IGhost> GHOST_CAP = null;

	private IGhost instance = GHOST_CAP.getDefaultInstance();

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{

		return capability == GHOST_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{

		return capability == GHOST_CAP ? GHOST_CAP.<T> cast(this.instance) : null;
	}

	@Override
	public NBTBase serializeNBT()
	{

		return GHOST_CAP.getStorage().writeNBT(GHOST_CAP, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt)
	{

		GHOST_CAP.getStorage().readNBT(GHOST_CAP, this.instance, null, nbt);
	}
}