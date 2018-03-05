package schoperation.schopcraft.cap.sanity;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

// Save sanity data for each player.
public class SanityStorage implements IStorage<ISanity> {

	@Override
	public NBTBase writeNBT(Capability<ISanity> capability, ISanity instance, EnumFacing side) {
		
		NBTTagCompound compound = new NBTTagCompound();
		
		compound.setFloat("sanityLevel", instance.getSanity());
		compound.setFloat("maxSanityLevel", instance.getMaxSanity());
		compound.setFloat("minSanityLevel", instance.getMinSanity());
		
		return compound;
	}
	
	@Override
	public void readNBT(Capability<ISanity> capability, ISanity instance, EnumFacing side, NBTBase nbt) {
		
		if (nbt instanceof NBTTagCompound) {
			
			NBTTagCompound compound = (NBTTagCompound) nbt;
		
			if (compound.hasKey("sanityLevel") && compound.hasKey("maxSanityLevel") && compound.hasKey("minSanityLevel")) {
			
				instance.set(compound.getFloat("sanityLevel"));
				instance.setMax(compound.getFloat("maxSanityLevel"));
				instance.setMin(compound.getFloat("minSanityLevel"));
			}
		}
	}
}