package enginecrafter77.survivalinc.season;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import net.minecraft.util.ResourceLocation;

public class SurvivalIncSeason extends SeasonProvider {

	private final int index;
	
	public SurvivalIncSeason(String name, int index)
	{
		super(new ResourceLocation(SurvivalInc.MOD_ID, name));
		this.index = index;
	}

	@Override
	public float getPeakTemperature()
	{
		return (float)ModConfig.SEASONS.temperatures[index];
	}

	@Override
	public int getLength()
	{
		return ModConfig.SEASONS.durations[this.index];
	}

	@Override
	public boolean allowCropGrowth()
	{
		return true;
	}
	
	
	
}
