package net.schoperation.schopcraft.cap.wetness;

public interface IWetness {
	
	// basic crap we can do with wetness
	public void increase(float amount);
	public void decrease(float amount);
	public void set(float amount);
	
	// dealing with min and max
	public void setMax(float amount);
	public void setMin(float amount);
	
	// getting values
	public float getWetness();
	public float getMaxWetness();
	public float getMinWetness();

}
