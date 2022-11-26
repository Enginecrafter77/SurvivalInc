package enginecrafter77.survivalinc.season;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface SeasonProvider {
	
	/** @return The length of the season in minecraft days (24k ticks) */
	public int getLength();
	
	public ResourceLocation getName();
	
	public String getTranslationKey();
	
	/**
	 * Returns the characteristic temperature offset of the season.
	 * The characteristic temperature offset is the offset with the
	 * greatest deviation from the original biome temperature.
	 * Generally, the characteristic temperature offset is achieved
	 * on {@link #getPeakTemperatureDay()}
	 * @return The peak temperature of the season. 
	 */
	public float getPeakTemperature();
	
	/**
	 * Peak day is the day in the current season when
	 * the temperature offset is supposed to hit the
	 * temperature returned by {@link #getPeakTemperature()}
	 * @return The day the temperature offset meets its peak
	 */
	public int getPeakTemperatureDay();
	
	public void applySeason(World world, int day);
	
}
