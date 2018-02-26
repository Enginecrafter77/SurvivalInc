package net.schoperation.schopcraft.cap.ghost;

public class Ghost implements IGhost {
	
	// Create ghost variables.
	private boolean isGhost = false;
	private float ghostEnergy = 0.00f;
	private float maxGhostEnergy = 100.00f;
	private float minGhostEnergy = 0.00f;
	
	// Methods for messing with ghost status or getting it.
	public void setGhost() {
		
		this.isGhost = true;
	}
	
	public void setAlive() {
		
		this.isGhost = false;
	}
	
	public boolean isGhost() {
		
		return this.isGhost;
	}
	
	// Methods for messing with ghost energy or getting it.
	public void increaseEnergy(float amount) {
		
		this.ghostEnergy += amount;
		
		if (this.ghostEnergy > this.maxGhostEnergy) {
			
			this.ghostEnergy = this.maxGhostEnergy;
		}
		
		else if (this.ghostEnergy < this.minGhostEnergy) {
			
			this.ghostEnergy = this.minGhostEnergy;
		}
	}
	
	public void decreaseEnergy(float amount) {
		
		this.ghostEnergy -= amount;
		
		if (this.ghostEnergy < this.minGhostEnergy) {
			
			this.ghostEnergy = this.minGhostEnergy;
		}
		
		else if (this.ghostEnergy > this.maxGhostEnergy) {
			
			this.ghostEnergy = this.maxGhostEnergy;
		}
	}
	
	public void setEnergy(float amount) {
		
		this.ghostEnergy = amount;
		
		if (this.ghostEnergy > this.maxGhostEnergy) {
			
			this.ghostEnergy = this.maxGhostEnergy;
		}
		
		else if (this.ghostEnergy < this.minGhostEnergy) {
			
			this.ghostEnergy = this.minGhostEnergy;
		}
	}
	
	public float getEnergy() {
		
		return this.ghostEnergy;
	}
}