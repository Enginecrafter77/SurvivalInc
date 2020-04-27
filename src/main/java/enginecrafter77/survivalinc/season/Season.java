package enginecrafter77.survivalinc.season;

import enginecrafter77.survivalinc.config.ModConfig;

public enum Season {
	
	WINTER(ModConfig.SEASONS.winterLength, -0.25F, 0.6f, 0x5BAE92),
	SPRING(ModConfig.SEASONS.springLength, 0.075F, 0.7f, 0x000000),
	SUMMER(ModConfig.SEASONS.summerLength, 0.25F, 0.3f, 0xCAE24E),
	AUTUMN(ModConfig.SEASONS.autumnLength, -0.075F, 0.4f, 0xD47E00);
	
	/** The length of the season in minecraft days */
	public final int length;
	
	/** The chance of rainfall occurring in this season */
	public final float rainfallchance;
	
	/** The temperature of the current season */
	public final float temperature;
	
	/** The color of grass during this season */
	public final int grasscolor;
	
	private Season(int length, float temperature, float rainfallchance, int grasscolor)
	{
		this.rainfallchance = rainfallchance;
		this.temperature = temperature;
		this.grasscolor = grasscolor;
		this.length = length;
	}
	
	/**
	 * Returns the n-th season following the current season.
	 * Negative numbers are also allowed, meaning that the
	 * traversal should go backwards.
	 * Passing 1 essentially requests the next season, whereas
	 * passing -1 requests the previous season.
	 * @param count The n variable, i.e. the number of seasons to traverse
	 * @return The season being the n-th in order of the following season list
	 */
	public Season getFollowing(int count)
	{
		Season[] seasons = Season.values();
		int index = (this.ordinal() + count) % seasons.length;
		return seasons[Math.abs(index)];
	}
	
	public static int getYearLength()
	{
		int counter = 0;
		for(Season current : Season.values())
			counter += current.length;
		return counter;
	}
	
	// Temporary fix using cosine-managed temperature
	public float getTemperatureOffset(int days)
	{
		days += this.ordinal() * 14;
		return (float)(-0.25D * Math.cos(Math.PI * (days - 7) / 28D));
	}
}