package net.schoperation.schopcraft.cap.thirst;

public class Thirst implements IThirst {
	
	// Create thirst variables.
	private float thirst = 100.00f;
	private float maxThirst = 100.00f;
	private float minThirst = 0.00f;
	
	// Methods for messing with thirst (or getting it).
	public void increase(float amount) {
		
		this.thirst += amount;
		
		if (this.thirst > this.maxThirst) {
			
			this.thirst = this.maxThirst;
		}
		
		else if (this.thirst < this.minThirst) {
			
			this.thirst = this.minThirst;
		}
	}
	
	public void decrease(float amount) {
		
		this.thirst -= amount;
		
		if (this.thirst < this.minThirst) {
			
			this.thirst = this.minThirst;
		}
		
		else if (this.thirst > this.maxThirst) {
			
			this.thirst = this.maxThirst;
		}
	}
	
	public void set(float amount) {
		
		this.thirst = amount;
		
		if (this.thirst > this.maxThirst) {
			
			this.thirst = this.maxThirst;
		}
		
		else if (this.thirst < this.minThirst) {
		
			this.thirst = this.minThirst;
		}
	}
	
	public void setMax(float amount) {

		this.maxThirst = amount;
	}
	
	public void setMin(float amount) {
		
		this.minThirst = amount;
	}
	
	public float getThirst() {
		
		return this.thirst;
	}
	
	public float getMaxThirst() {
		
		return this.maxThirst;
	}
	
	public float getMinThirst() {
		
		return this.minThirst;
	}
}