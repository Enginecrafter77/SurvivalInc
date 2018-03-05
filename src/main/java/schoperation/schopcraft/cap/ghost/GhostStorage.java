package schoperation.schopcraft.cap.ghost;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

// Save ghost data for each player.
public class GhostStorage implements IStorage<IGhost> {
	
	@Override
	public NBTBase writeNBT(Capability<IGhost> capability, IGhost instance, EnumFacing side) {
		
		NBTTagCompound compound = new NBTTagCompound();
		
		compound.setBoolean("isGhost", instance.isGhost());
		compound.setFloat("ghostEnergyLevel", instance.getEnergy());
		
		return compound;
	}
	
	@Override
	public void readNBT(Capability<IGhost> capability, IGhost instance, EnumFacing side, NBTBase nbt) {
		
		if (nbt instanceof NBTTagCompound) {
			
			NBTTagCompound compound = (NBTTagCompound) nbt;
		
			if (compound.hasKey("isGhost") && compound.hasKey("ghostEnergyLevel")) {
				
				instance.setEnergy(compound.getFloat("ghostEnergyLevel"));
				
				if (compound.getBoolean("isGhost")) {
					
					instance.setGhost();
				}
				
				else {
					
					instance.setAlive();
				}
			}
		}
	}
}