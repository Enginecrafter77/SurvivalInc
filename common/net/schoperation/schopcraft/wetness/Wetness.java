package net.schoperation.schopcraft.wetness;

public class Wetness implements IWetness {
	
	// create wetness variable
	private float wetness = 0.0f;
	
	// methods for messing with wetness (or getting it)
	public void increase(float amount) {
		
		this.wetness += amount;
	}
	
	public void decrease(float amount) {
		
		this.wetness -= amount;
	}
	
	public void set(float amount) {
		
		this.wetness = amount;
	}
	
	public float getWetness() {
		
		return this.wetness;
	}
}
