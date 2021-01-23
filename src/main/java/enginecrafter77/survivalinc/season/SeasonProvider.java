package enginecrafter77.survivalinc.season;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class SeasonProvider {
	
	private final ResourceLocation name;
	
	public SeasonProvider(ResourceLocation name)
	{
		this.name = name;
	}
	
	/**
	 * Returns the characteristic temperature offset of the season.
	 * The characteristic temperature offset is the offset with the
	 * greatest deviation from the original biome temperature.
	 * Generally, the characteristic temperature offset is achieved
	 * on {@link #getPeakDay()}
	 * @return The peak temperature of the season. 
	 */
	public abstract float getPeakTemperature();
	
	/** @return The length of the season in minecraft days (24k ticks) */
	public abstract int getLength();
	
	public abstract boolean allowCropGrowth();
	
	public void applySeason(World world, int day) {}
	
	public ResourceLocation getName()
	{
		return this.name;
	}
	
	public String getNameTranslationKey()
	{
		ResourceLocation name = this.getName();
		return name.getNamespace().concat(".").concat(name.getPath());
	}
	
	public String getLocalizedName()
	{
		return I18n.format(this.getNameTranslationKey(), new Object[0]);
	}
	
	public float getCustomTemperatureOn(int day)
	{
		return Float.NaN;
	}
	
	/**
	 * Peak day is the day in the current season when
	 * the temperature offset is supposed to hit the
	 * temperature returned by {@link #getPeakTemperatureOffset()}
	 * @return The day the temperature offset meets it's peak
	 */
	public int getPeakDay()
	{
		return this.getLength() / 2;
	}
	
}