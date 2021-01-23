package enginecrafter77.survivalinc.season;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import enginecrafter77.survivalinc.SurvivalInc;

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
	 * @param data The current season data
	 * @param offset The calculated {@link SeasonProvider#getTemperatureOffset(int) standard universal temperature offset}
	 * @return A new base temperature for the provided biome
	 */
	public float calculateNewBiomeTemperature(Biome biome, SeasonCalendarDate date, float offset)
	{
		return this.originals.get(biome) + offset;
	}
	
	/**
	 * This actually changes the biome temperatures every morning... Using reflection!
	 * Yeah it's very hacky, hackish, whatever you wanna call it, but it works!
	 * @param season The season to 
	 * @param daysIntoSeason
	 */
	public void applySeason(SeasonCalendarDate date)
	{		
		long time = System.currentTimeMillis();
		int processed = 0;
		
		float offset = this.getTemperatureOffset(date);
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
	
	private SeasonCalendarDate getSeasonPeak(SeasonCalendar calendar, SeasonProvider season)
	{
		return new SeasonCalendarDate(calendar, calendar.indexOf(season), season.getPeakDay());
	}
	
	/**
	 * Calculates the temperature offset in the
	 * specified day in the current season.
	 * Temperature offsets are designed to have
	 * a fairly smooth transition between seasons.
	 * @param days The day in the season
	 * @return The current temperature offset.
	 */
	public float getTemperatureOffset(SeasonCalendarDate date)
	{
		SeasonProvider season = date.getSeason();
		int day = date.getDay();
		
		// Indicates which way around we are going. 1 means forward while -1 means backward.
		int way = day > season.getPeakDay() ? 1 : -1;
		
		// The season we are aiming at with our calculations (e.g. Spring day 10 is beyond half, so temperature shifts towards summer)
		SeasonProvider target_season = date.getNextSeason(way);
		
		// The current absolute day of year
		int absolute_day = date.getDayInYear();
		
		// The absolute day of half of this season (where temperature is supposed to achieve the specified value)
		SeasonCalendarDate local = this.getSeasonPeak(date.calendar, season);
		
		// The absolute day of half of the target season (where temperature is supposed to achieve the specified value)
		SeasonCalendarDate target = this.getSeasonPeak(date.calendar, target_season);
		
		int localpeak = local.getDayInYear();
		int targetpeak = target.getDayInYear();
		
		/*
		 * Remember your math teacher saying that when you multiply
		 * inequality with negative number, it flips around?
		 * This basically checks if we are wrapping around the year
		 * edge, and if so, adds/subtracts a whole year length.
		 */
		if(target.compareTo(local) != way)
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
			targetpeak += way * date.calendar.getYearLength();
		}
		
		float value = BiomeTempController.interpolateTemperature(absolute_day, localpeak, targetpeak, season.getPeakTemperature(), target_season.getPeakTemperature());
		SurvivalInc.logger.debug("{} --> {} / {} --> {}", date.toString(), local.toString(), target.toString(), value);
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
	private static float interpolateTemperature(float x, float d0, float d1, float a, float b)
	{
		return (b - a) * ((x - d0) / (d1 - d0)) + a;
	}
}