package net.schoperation.schopcraft.cap.wetness;

public class Wetness implements IWetness {
	
	// create wetness variables
	private float wetness = 0.00f;
	private float maxWetness = 100.00f;
	private float minWetness = 0.00f;
	
	// methods for messing with wetness (or getting it)
	public void increase(float amount) {
		
		this.wetness += amount;
		if (this.wetness > this.maxWetness) {
			this.wetness = this.maxWetness;
		}
		else if (this.wetness < this.minWetness) {
			this.wetness = this.minWetness;
		}
	}
	
	public void decrease(float amount) {
		
		this.wetness -= amount;
		if (this.wetness < this.minWetness) {
			this.wetness = this.minWetness;
		}
		else if (this.wetness > this.maxWetness) {
			this.wetness = this.maxWetness;
		}
	}
	
	public void set(float amount) {
		
		this.wetness = amount;
		if (this.wetness > this.maxWetness) {
			this.wetness = this.maxWetness;
		}
		else if (this.wetness < this.minWetness) {
			this.wetness = this.minWetness;
		}
	}
	
	public float getWetness() {
		
		return this.wetness;
	}
	
	public float getMaxWetness() {
		
		return this.maxWetness;
	}
	
	public float getMinWetness() {
		
		return this.minWetness;
	}
}
