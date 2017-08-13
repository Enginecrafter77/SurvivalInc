package net.schoperation.schopcraft.cap.ghost;

public interface IGhost {
	
	// Stuff we can do with the ghost capability
	public void setGhost();
	public void setAlive();
	
	// Getting ghost status
	public boolean isGhost();
	
	// Stuff we can do with ghost energy (Note that min and max energy won't be changeable)
	public void increaseEnergy(float amount);
	public void decreaseEnergy(float amount);
	public void setEnergy(float amount);
	
	// Getting energy
	public float getEnergy();

}
