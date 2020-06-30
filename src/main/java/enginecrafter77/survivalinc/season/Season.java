package enginecrafter77.survivalinc.season;

import enginecrafter77.survivalinc.config.ModConfig;

public enum Season {
	WINTER(0.6f, 0x5BAE92),
	SPRING(0.7f, 0x000000),
	SUMMER(0.3f, 0xCAE24E),
	AUTUMN(0.4f, 0xD47E00);
	
	/** The chance of rainfall occurring in this season */
	public final float rainfallchance;
	
	/** The color of grass during this season */
	public final int grasscolor;
	
	private Season(float rainfallchance, int grasscolor)
	{
		this.rainfallchance = rainfallchance;
		this.grasscolor = grasscolor;
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
		
		if(index < 0)
			index = seasons.length + index;
		
		return seasons[index];
	}
	
	/**
	 * Returns the peak temperature offset of the season.
	 * The peak temperature offset is the offset with the
	 * greatest deviation from the original biome temperature.
	 * Generally, the peak temperature offset is achieved in
	 * the exact half of the season.
	 * @return The peak temperature of the season. 
	 */
	public float getPeakTemperatureOffset()
	{
		return (float)ModConfig.SEASONS.temperatures[this.ordinal()];
	}
	
	/** @return The length of the season in minecraft days. */
	public int getLength()
	{
		return ModConfig.SEASONS.durations[this.ordinal()];
	}
	
	/**
	 * Calculates the length of one year. One
	 * year is considered a period during which
	 * all seasons happen, so the last season
	 * rolls over to the first season. The year
	 * length is the time in minecraft days
	 * between two of these roll-overs. In
	 * other words, the length of minecraft
	 * year is the sum of the lengths of individual
	 * seasons.
	 * @return The length of minecraft seasonal year.
	 */
	public static int getYearLength()
	{
		int counter = 0;
		for(Season current : Season.values())
			counter += current.getLength();
		return counter;
	}
	
	/**
	 * Returns the absolute day in the year analogical to the day in the current season.
	 * @param day The day of the season
	 * @return The analogical day in the year
	 */
	public int getAbsoluteDay(int day)
	{
		for(int index = 0; index < this.ordinal(); index++)
			day += Season.values()[index].getLength();
		return day;
	}
	
	/**
	 * Calculates the temperature offset in the
	 * specified day in the current season.
	 * Temperature offsets are designed to have
	 * a fairly smooth transition between seasons.
	 * @param days The day in the season
	 * @return The current temperature offset.
	 */
	public float getTemperatureOffset(int days)
	{
		// Indicates which way around we are going. 1 means forward while -1 means backward.
		int way = days > (this.getLength() / 2) ? 1 : -1;
		
		// The season we are aiming at with our calculations (e.g. Spring day 10 is beyond half, so temperature shifts towards summer)
		Season target = this.getFollowing(way);
		
		// The current absolute day of year
		int currentabs = this.getAbsoluteDay(days);
		
		// The absolute day of half of this season (where temperature is supposed to achieve the specified value)
		int localabs = this.getAbsoluteDay(this.getLength() / 2);
		
		// The absolute day of half of the target season (where temperature is supposed to achieve the specified value)
		int targetabs = target.getAbsoluteDay(target.getLength() / 2);
		
		/*
		 * Remember your math teacher saying that when you multiply
		 * inequality with negative number, it flips around?
		 * This basically checks if we are wrapping around the year
		 * edge, and fixes the targettings.
		 */
		if(way * target.ordinal() < way * this.ordinal())
		{
			/*
			 * When we are wrapping positively across the
			 * year edge (way = 1), then the target absolute
			 * day will be lower than the local absolute.
			 * This assures that for current calculations,
			 * the absolute day is shifted by the whole year
			 * length. Imagine it as if the season is logically
			 * following this (like in perpetual loop), but in
			 * fact it gets wrapped around.
			 * Consider this example:
			 * 				|			#
			 * -----------------------------------------
			 * 	1	2	3	4	5	6	7	8	[9]	[10]
			 * 
			 * In this example, we are currently at day 7,
			 * which is beyond the temperature amplitude day(4).
			 * We are reaching the end of the year. So the absolute
			 * days 1 and 2 are re-mapped to absolute days 9 and 10
			 * respectively. Likewise, this happens at the very
			 * beginning of new year.
			 * 		#		|
			 * -------------------------------
			 * 	1	2	3	4	5	6	7	8
			 * 
			 * Here, the values lesser than 4 are subtracted by
			 * the length of the year. Why? Because it puts
			 * the numbers behind 0, which signifies their meaning
			 * that they are beyond the edge of the year, and are
			 * as far away from the edge as the original number.
			 * It may be hard to imagine, but it works perfectly.
			 */
			targetabs += way * Season.getYearLength();
		}
		
		float value = Season.calculateSlope(currentabs, localabs, targetabs, this.getPeakTemperatureOffset(), target.getPeakTemperatureOffset());
		
		System.out.format("\tSeason %s day %02d aiming towards %s(%02d)[%f] => \t %f\n", this.name(), days, target.name(), target.getLength(), target.getPeakTemperatureOffset(), value);
		
		return value;
	}
	
	/**
	 * Calculates the current temperature offset using the
	 * specified function variable values.
	 * 
	 * Mathematically, this function is a simple linear
	 * equation that, when plotted, creates a nice slope
	 * crossing points A[d0,a] and B[d1,b]. In fact, the
	 * line connecting these points in Cartesian plane
	 * is a part of the function used by this method.
	 * 
	 * So how it works? The call to this method is equivalent to
	 * <pre>
	 *  		   x - d0
	 * 	y = (b-a)--------- + a
	 *  		  d1 - d0
	 * </pre>
	 * 
	 * This method basically computes the difference between the temperatures,
	 * and applies it to the fraction of the days elapsed from the total days.
	 * Finally, the temperature correction equal to <i>a</i> is applied.
	 * @param x The current day
	 * @param d0 The day of point A
	 * @param d1 The day of point B
	 * @param a The temperature of point A
	 * @param b The temperature of point B
	 * @return The value linearly scaled between points A and B
	 */
	private static float calculateSlope(float x, float d0, float d1, float a, float b)
	{
		System.out.format("FX: X=%f, d0=%f, d1=%f, a=%f, b=%f ---\\/\n", x, d0, d1, a, b);
		return (b - a) * ((x - d0) / (d1 - d0)) + a;
	}
}