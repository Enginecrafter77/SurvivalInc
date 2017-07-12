package net.schoperation.schopcraft.cap.thirst;

public interface IThirst {
	
	// stuff we can do with thirst
	
	public void increase(float amount);
	public void decrease(float amount);
	public void set(float amount);
	
	// dealing with min and max
	public void setMax(float amount);
	public void setMin(float amount);
	
	// getting values
	public float getThirst();
	public float getMaxThirst();
	public float getMinThirst();

}
