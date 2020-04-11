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
public class BiomeTempController extends HashMap<Biome, Float> {
	private static final long serialVersionUID = 4090434007816386553L;	
	
	public final Map<Biome, Float> originals;
	public final Set<Class<? extends Biome>> excluded;
	protected String[] possiblenames;
	
	public BiomeTempController()
	{
		this.possiblenames = new String[]{"field_76750_F", "temperature"};
		this.originals = new HashMap<Biome, Float>();
		this.excluded = new HashSet<Class<? extends Biome>>();
		
		// Store the original biome temperatures
		for(Biome biome : ForgeRegistries.BIOMES.getValuesCollection())
		{
			originals.put(biome, biome.getDefaultTemperature());
		}
	}
	
	protected Field tryAccess() throws NoSuchFieldException, ReflectiveOperationException
	{
		for(String name : possiblenames)
		{
			try
			{
				Field field = Biome.class.getDeclaredField(name);
				field.setAccessible(true);
				return field;
			}
			catch(NoSuchFieldException exc)
			{
				continue;
			}
		}
		throw new NoSuchFieldException("Temperature field not found in biome class");
	}
	
	public void setTemperature(Biome biome, float temperature)
	{
		try
		{
			Field field = this.tryAccess();
			field.setFloat(biome, temperature);
		}
		catch(NoSuchFieldException exc)
		{
			SurvivalInc.logger.error(exc.getMessage());
		}
		catch(ReflectiveOperationException exc)
		{
			exc.printStackTrace();
		}
	}
	
	public void resetTemperature(Biome biome)
	{
		this.setTemperature(biome, this.originals.get(biome));
	}

	/**
	 * This actually changes the biome temperatures every morning... Using reflection!
	 * Yeah it's very hacky, hackish, whatever you wanna call it, but it works!
	 * @param season The season to 
	 * @param daysIntoSeason
	 */
	public void applySeason(SeasonData data)
	{
		for(Biome biome : ForgeRegistries.BIOMES.getValuesCollection())
		{
			if(excluded.contains(biome.getClass())) continue;
			
			float original = this.originals.get(biome);
			original += data.season.getTemperatureOffset(data.day);
			this.setTemperature(biome, original);
		}
	}
}