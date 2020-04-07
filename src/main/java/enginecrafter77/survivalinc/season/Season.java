package enginecrafter77.survivalinc.season;

import java.util.ArrayList;
import java.util.List;

import enginecrafter77.survivalinc.config.SchopConfig;

public enum Season {

	WINTER(SchopConfig.SEASONS.winterLength, 0.6f),
	SPRING(SchopConfig.SEASONS.springLength, 0.7f),
	SUMMER(SchopConfig.SEASONS.summerLength, 0.3f),
	AUTUMN(SchopConfig.SEASONS.autumnLength, 0.4f);
	
	/** The length of the season in minecraft days */
	public final int length;
	
	/** The chance of rainfall occurring in this season */
	public final float rainfallchance;
	
	/**
	 * The season is divided into n parts, where
	 * n is the number of element in this list.
	 * The temperature of the environment is
	 * calculated by {@link #getTemperatureOffset(int)}
	 */
	public final List<Float> thermodelta;
	
	private Season(int length, float rainfallchance)
	{
		// We will usually only need 2 changes
		this.thermodelta = new ArrayList<Float>(2);
		this.rainfallchance = rainfallchance;
		this.length = length;
	}
	
	private int getIndex()
	{
		Season[] orderedlist = Season.values();
		for(int index = 0; index < orderedlist.length; index++)
		{
			if(orderedlist[index] == this)
				return index;
		}
		throw new Error("Element " + this.name() + " has no index. This error should NEVER happen.");
	}
	
	private static Season valueOf(int index)
	{
		return Season.values()[index];
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
		return Season.valueOf((this.getIndex() + count) % Season.values().length);
	}
	
	/**
	 * Initializes the {@link #thermodelta} values of all seasons
	 */
	public static void initSeasons()
	{
		WINTER.thermodelta.add(-0.6F);
		WINTER.thermodelta.add(-1F);
		SPRING.thermodelta.add(0.2F);
		SPRING.thermodelta.add(0F);
		SUMMER.thermodelta.add(0F);
		SUMMER.thermodelta.add(0.5F);
		AUTUMN.thermodelta.add(-0.6F);
		AUTUMN.thermodelta.add(-0.3F);
	}
	
	/**
	 * Calculates the temperature difference based on
	 * the initialized {@link #thermodelta} values.
	 * The values must be first initialized. This generally
	 * involves running {@link #initSeasons()} method. Failing
	 * to do so before invoking this method results in arithmetic
	 * exception caused by zero division.
	 * The offset is calculated by the following equation:
	 * <code>
	 * 	offset = days / (season_length / thermodelta_count)
	 * </code>
	 * @param days The number of minecraft days elapsed since the  of the season
	 * @return The current temperature offset
	 */
	public float getTemperatureOffset(int days)
	{
		// The length of 1 season logical part
		int seasonpart = this.length / this.thermodelta.size();
		// The part we are currently in.
		int part = days / seasonpart;
		// Return the temperature delta of the current season part
		return this.thermodelta.get(part);
	}
}