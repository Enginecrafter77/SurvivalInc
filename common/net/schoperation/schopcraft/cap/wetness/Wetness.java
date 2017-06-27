package net.schoperation.schopcraft.cap.wetness;

public class Wetness implements IWetness {
	
	// create wetness variables
	private float wetness = 0.00f;
	private float maxWetness = 100.00f;
	
	// methods for messing with wetness (or getting it)
	public void increase(float amount) {
		
		this.wetness += amount;
		if (this.wetness > maxWetness) {
			this.wetness = 100.0f;
		}
	}
	
	public void decrease(float amount) {
		
		this.wetness -= amount;
		if (this.wetness < 0.0f) {
			this.wetness = 0.0f;
		}
	}
	
	public void set(float amount) {
		
		this.wetness = amount;
		if (this.wetness > maxWetness) {
			this.wetness = 100.0f;
		}
	}
	
	public float getWetness() {
		
		return this.wetness;
	}
	
	public float getMaxWetness() {
		
		return this.maxWetness;
	}
}
