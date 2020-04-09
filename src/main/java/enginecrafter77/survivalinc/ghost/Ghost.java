package enginecrafter77.survivalinc.ghost;

public class Ghost implements IGhost {
	private boolean isGhost = false;
	private float ghostEnergy = 0.00f;
	private float maxGhostEnergy = 100.00f;
	private float minGhostEnergy = 0.00f;
	
	@Override
	public void create()
	{
		this.isGhost = true;
	}
	
	@Override
	public void resurrect()
	{
		this.isGhost = false;
	}
	
	@Override
	public boolean status()
	{
		return this.isGhost;
	}

	// Methods for messing with ghost energy or getting it.
	@Override
	public void addEnergy(float amount)
	{
		this.ghostEnergy += amount;
		this.checkEnergy();
	}

	public void setEnergy(float amount)
	{
		this.ghostEnergy = amount;
		this.checkEnergy();
	}
	
	protected void checkEnergy()
	{
		if(this.ghostEnergy > this.maxGhostEnergy)
		{
			this.ghostEnergy = this.maxGhostEnergy;
		}
		
		if(this.ghostEnergy < this.minGhostEnergy)
		{
			this.ghostEnergy = this.minGhostEnergy;
		}
	}

	public float getEnergy()
	{
		return this.ghostEnergy;
	}
}