package net.schoperation.schopcraft.cap.temperature;

public interface ITemperature {
	
	// Stuff we can do with temperature.
	public void increase(float amount);
	public void decrease(float amount);
	public void set(float amount);
	
	// Messing with mins and maxes.
	public void setMax(float amount);
	public void setMin(float amount);
	
	// Messing with the "target temperature."
	public void increaseTarget(float amount);
	public void decreaseTarget(float amount);
	public void setTarget(float amount);
	
	// Getting values.
	public float getTemperature();
	public float getMaxTemperature();
	public float getMinTemperature();
	public float getTargetTemperature();
}