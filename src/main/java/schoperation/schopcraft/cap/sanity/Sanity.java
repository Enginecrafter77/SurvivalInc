package schoperation.schopcraft.cap.sanity;

public class Sanity implements ISanity {
	
	// Create sanity variables.
	private float sanity = 100.00f;
	private float maxSanity = 100.00f;
	private float minSanity = 0.00f;
	
	// Methods for messing with sanity or getting it.
	public void increase(float amount) {
		
		this.sanity += amount;
		
		if (this.sanity > this.maxSanity) {
			
			this.sanity = this.maxSanity;
		}
		
		else if (this.sanity < this.minSanity) {
			
			this.sanity = this.minSanity;
		}
	}
	
	public void decrease(float amount) {
		
		this.sanity -= amount;
		
		if (this.sanity < this.minSanity) {
			
			this.sanity = this.minSanity;
		}
		
		else if (this.sanity > this.maxSanity) {
			
			this.sanity = this.maxSanity;
		}
	}
	
	public void set(float amount) {
		
		this.sanity = amount;
		
		if (this.sanity > this.maxSanity) {
			
			this.sanity = this.maxSanity;
		}
		
		else if (this.sanity < this.minSanity) {
			
			this.sanity = this.minSanity;
		}
	}
	
	public void setMax(float amount) {
		
		this.maxSanity = amount;
	}
	
	public void setMin(float amount) {
		
		this.minSanity = amount;
	}
	
	public float getSanity() {
		
		return this.sanity;
	}
	
	public float getMaxSanity() {
		
		return this.maxSanity;
	}
	
	public float getMinSanity() {
		
		return this.minSanity;
	}
}