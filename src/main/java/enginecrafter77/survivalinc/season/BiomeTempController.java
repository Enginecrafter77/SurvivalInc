package enginecrafter77.survivalinc.season;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.season.SeasonCalendar.SeasonCalendarEntry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This stores the original biome temperatures, modifying the base temps if
 * necessary. It also deals with changing the temperature of each biome
 * according to the season, and where we are in the season.
 */
public class BiomeTempController {
	private static final String[] possiblenames = new String[]{"field_76750_F", "temperature"};
	
	public final Map<Biome, Float> originals;
	public final Set<Class<? extends Biome>> excluded;
	
	protected final Field target;
	
	public BiomeTempController() throws NoSuchFieldException
	{
		this.originals = new HashMap<Biome, Float>();
		this.excluded = new HashSet<Class<? extends Biome>>();
		
		// Reflectively locate the target field
		Field field = null;
		for(String name : possiblenames)
		{
			try
			{
				field = Biome.class.getDeclaredField(name);
				field.setAccessible(true);
			}
			catch(ReflectiveOperationException exc)
			{
				continue; // Try the next possible field
			}
		}
		
		if(field == null) throw new NoSuchFieldException("Temperature field not found in Biome.class");
		else this.target = field;
	}
	
	protected void setTemperature(Biome biome, float temperature)
	{
		try
		{
			target.setFloat(biome, temperature);
		}
		catch(ReflectiveOperationException exc)
		{
			SurvivalInc.logger.error("Biome temperature injection failed!");
			exc.printStackTrace();
		}
	}
	
	protected void resetTemperature(Biome biome)
	{
		// If the map doesn't contain the biome entry, it means it hasn't been affected yet,
		// and so it already has the wanted default value.
		if(this.originals.containsKey(biome))
		{
			this.setTemperature(biome, this.originals.get(biome));
		}
	}
	
	/**
	 * A method used to calculate a new base temperature
	 * for the given biome type. Override this method
	 * if you want to use a different type of calculation.
	 * @param biome The biome to calculate new base temperature for
	 * @param date The current season date
	 * @param offset The calculated standard universal temperature offset
	 * @return A new base temperature for the provided biome
	 */
	public float calculateNewBiomeTemperature(Biome biome, SeasonCalendarDate date, float offset)
	{
		return this.originals.get(biome) + offset;
	}
	
	/**
	 * This actually changes the biome temperatures every morning... Using reflection!
	 * Yeah, it's very hacky, hackish, whatever you want to call it, but it works!
	 * @param date
	 */
	public void applySeason(SeasonCalendarDate date)
	{		
		long time = System.currentTimeMillis();
		int processed = 0;
		
		float offset = this.getSeasonalTemperatureOffset(date);
		for(Biome biome : ForgeRegistries.BIOMES.getValuesCollection())
		{
			if(excluded.contains(biome.getClass())) continue;
			
			// A little check to fix compatibility with mods that add biomes during runtime
			if(!this.originals.containsKey(biome))
			{
				SurvivalInc.logger.debug("Biome {} has not saved it's original value. Mapping to {}.", biome.getRegistryName().toString(), biome.getDefaultTemperature());
				this.originals.put(biome, biome.getDefaultTemperature());
			}
			
			this.setTemperature(biome, this.calculateNewBiomeTemperature(biome, date, offset));
			processed++;
		}
		
		time = System.currentTimeMillis() - time;
		SurvivalInc.logger.info("Processed {} biomes in {} ms", processed, time);
	}
	
	/**
	 * Gets the peak point for the specified calendar season.
	 * @param season The season to get the peak for.
	 * @return The date the temperature reaches it's extreme in the specified season.
	 */
	private static SeasonCalendarDate getPeak(SeasonCalendarEntry season)
	{
		return new SeasonCalendarDate(season, season.getSeason().getPeakTemperatureDay());
	}
	
	/**
	 * Returns the nearest peak point to the specified date.
	 * @param date The date to find the nearest peak to
	 * @param way The way of iteration. 1 means forward, while -1 means backward.
	 * @return The nearest peak point in the specified direction.
	 */
	private static SeasonCalendarDate nearestPeak(SeasonCalendarDate date, int way)
	{
		SeasonCalendarEntry season = date.getCalendarEntry();
		SeasonCalendarDate peak = BiomeTempController.getPeak(season);
		
		// Test if date is already past the local peak. If so, advance the season in the specified way
		if(date.compareTo(peak) == way) peak = BiomeTempController.getPeak(season.getFollowing(way));
		
		return peak;
	}
	
	/**
	 * Calculates uniform temperature offset
	 * on the specified date in calendar year.
	 * Generally, this offset is applied to
	 * every biome's base temperature.
	 * @param date The current date in calendar year
	 * @return The current temperature offset.
	 */
	public float getSeasonalTemperatureOffset(SeasonCalendarDate date)
	{
		// If the date is exactly on the peak date
		if(BiomeTempController.getPeak(date.getCalendarEntry()).compareTo(date) == 0) return date.getCalendarEntry().getSeason().getPeakTemperature();
		
		// The absolute day of half of this season (where temperature is supposed to achieve the specified value)
		SeasonCalendarDate previous_peak = BiomeTempController.nearestPeak(date, -1);
		
		// The absolute day of half of the target season (where temperature is supposed to achieve the specified value)
		SeasonCalendarDate next_peak = BiomeTempController.nearestPeak(date, 1);
		
		int year = date.getCalendarEntry().getCalendar().getYearLength();
		
		// The current absolute day of year
		int a_day = date.getDayInYear();
		int pp_day = previous_peak.getDayInYear();
		int np_day = next_peak.getDayInYear();
		
		// Store the temperatures inside variables for easier access
		float pp_temp = previous_peak.getCalendarEntry().getSeason().getPeakTemperature();
		float np_temp = next_peak.getCalendarEntry().getSeason().getPeakTemperature();
		
		/*
		 * We always assume that the target date is in the direction
		 * specified by "way" ahead of local. If this is not the case,
		 * we are most probably flipping around the year boundary. If
		 * so, we need to adjust the date accordingly so that the interpolation
		 * works reliably.
		 * 
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
		if(np_day < pp_day) np_day += year;
		if(a_day < pp_day) a_day += year;
		
		float value = BiomeTempController.interpolateTemperature(a_day, pp_day, np_day, pp_temp, np_temp);
		SurvivalInc.logger.info("STI {}@{} --[{}]--> {}@{} => {}", previous_peak.toString(), pp_temp, date.toString(), next_peak.toString(), np_temp, value);
		return value;
	}
	
	/**
	 * Calculates the current temperature offset by utilizing
	 * simple linear interpolation method.
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
	public static float interpolateTemperature(float x, float d0, float d1, float a, float b)
	{
		return (b - a) * ((x - d0) / (d1 - d0)) + a;
	}
}
