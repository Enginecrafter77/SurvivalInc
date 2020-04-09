package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.stats.StatProvider;
import net.minecraft.entity.player.EntityPlayer;

//TODO document?
public interface Ghost {
	public void applyStatus(EntityPlayer player, boolean status);
	public void setStatus(boolean status);
	public boolean getStatus();
	
	public StatProvider getEnergyProvider();
	public void update(EntityPlayer player);
	
	public float setEnergy(float energy);
	public float getEnergy();
}