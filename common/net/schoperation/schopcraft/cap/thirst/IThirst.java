package net.schoperation.schopcraft.cap.thirst;

public interface IThirst {
	
	// stuff we can do with thirst
	
	public void increase(float amount);
	public void decrease(float amount);
	public void set(float amount);
	public float getThirst();
	public float getMaxThirst();
	public float getMinThirst();

}
