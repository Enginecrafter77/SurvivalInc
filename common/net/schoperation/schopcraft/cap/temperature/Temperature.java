package net.schoperation.schopcraft.cap.temperature;

import net.schoperation.schopcraft.config.ModConfig;

public class Temperature implements ITemperature {
	
	// Create temperature variables.
	private float temperature = 68.0f;
	private float maxTemperature = 120.0f;
	private float minTemperature = -20.0f;
	private float targetTemperature = 68.0f;
	
	// Methods for messing with temperature, or getting it.
	public void increase(float amount) {
		
		if (ModConfig.enableTemperature) {
			
			this.temperature += amount;
			
			if (this.temperature > this.maxTemperature) {
				
				this.temperature = this.maxTemperature;
			}
			
			else if (this.temperature < this.minTemperature) {
				
				this.temperature = this.minTemperature;
			}
		}
	}
	
	public void decrease(float amount) {
		
		if (ModConfig.enableTemperature) {
			
			this.temperature -= amount;
		
			if (this.temperature < this.minTemperature) {
				
				this.temperature = this.minTemperature;
			}
			
			else if (this.temperature > this.maxTemperature) {
				
				this.temperature = this.maxTemperature;
			}
		}
	}
		
	
	public void set(float amount) {
		
		if (ModConfig.enableTemperature) {
			
			this.temperature = amount;
			
			if (this.temperature > this.maxTemperature) {
				
				this.temperature = this.maxTemperature;
			}
			
			else if (this.temperature < this.minTemperature) {
				
				this.temperature = this.minTemperature;
			}
		}
	}
	
	public void setMax(float amount) {
		
		if (ModConfig.enableTemperature) {
			
			this.maxTemperature = amount;
		}	
	}
	
	public void setMin(float amount) {
		
		if (ModConfig.enableTemperature) {
			
			this.minTemperature = amount;
		}
	}
	
	public void increaseTarget(float amount) {
		
		if (ModConfig.enableTemperature) {
			
			this.targetTemperature += amount;
		}	
	}
	
	public void decreaseTarget(float amount) {
		
		if (ModConfig.enableTemperature) {
			
			this.targetTemperature -= amount;
		}	
	}
	
	public void setTarget(float amount) {
		
		if (ModConfig.enableTemperature) {
			
			this.targetTemperature = amount;
		}
	}
	
	public float getTemperature() {
		
		return this.temperature;
	}
	
	public float getMaxTemperature() {
		
		return this.maxTemperature;
	}
	
	public float getMinTemperature() {
		
		return this.minTemperature;
	}
	
	public float getTargetTemperature() {
		
		return this.targetTemperature;
	}
}