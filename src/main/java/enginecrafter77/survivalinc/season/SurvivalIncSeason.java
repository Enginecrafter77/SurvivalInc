package enginecrafter77.survivalinc.season;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public enum SurvivalIncSeason implements SeasonProvider {

	WINTER,
	SPRING,
	SUMMER,
	AUTUMN;
	
	@Override
	public float getPeakTemperature()
	{
		return (float)ModConfig.SEASONS.temperatures[this.ordinal()];
	}

	@Override
	public int getLength()
	{
		return ModConfig.SEASONS.durations[this.ordinal()];
	}

	@Override
	public ResourceLocation getName()
	{
		return new ResourceLocation(SurvivalInc.MOD_ID, this.name().toLowerCase());
	}

	@Override
	public String getTranslationKey()
	{
		return "season." + this.name().toLowerCase() + ".name";
	}

	@Override
	public int getPeakTemperatureDay()
	{
		return this.getLength() / 2;
	}

	@Override
	public void applySeason(World world, int day) {}
}
