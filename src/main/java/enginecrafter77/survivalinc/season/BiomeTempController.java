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
			SurvivalInc.logger.info("Setting base temperature of biome {} to {}", biome.getRegistryName().toString(), temperature);
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
	 * @param offset The calculated {@link Season#getTemperatureOffset(int) standard universal temperature offset}
	 * @return A new base temperature for the provided biome
	 */
	public float calculateNewBiomeTemperature(Biome biome, SeasonData data, float offset)
	{
		return this.originals.get(biome) + offset;
	}
	
	/**
	 * This actually changes the biome temperatures every morning... Using reflection!
	 * Yeah it's very hacky, hackish, whatever you wanna call it, but it works!
	 * @param season The season to 
	 * @param daysIntoSeason
	 */
	public void applySeason(SeasonData data)
	{		
		float offset = data.season.getTemperatureOffset(data.day);
		for(Biome biome : ForgeRegistries.BIOMES.getValuesCollection())
		{
			if(excluded.contains(biome.getClass())) continue;
			
			// A little check to fix compatibility with mods that add biomes during runtime
			if(!this.originals.containsKey(biome))
			{
				SurvivalInc.logger.info("Biome {} has not saved it's original value. Mapping to {}.", biome.getRegistryName().toString(), biome.getDefaultTemperature());
				this.originals.put(biome, biome.getDefaultTemperature());
			}
			
			this.setTemperature(biome, this.calculateNewBiomeTemperature(biome, data, offset));
		}
	}
}