package enginecrafter77.survivalinc.ghost;

//TODO document?
public interface IGhost {
	public void create();
	public void resurrect();
	public boolean status();
	public void addEnergy(float amount);
	public void setEnergy(float amount);
	public float getEnergy();
}