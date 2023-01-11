package enginecrafter77.survivalinc.season;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * This stores the original biome temperatures, modifying the base temps if
 * necessary. It also deals with changing the temperature of each biome
 * according to the season, and where we are in the season.
 * @author Enginecrafter77
 */
public class ReflectiveBiomeTemperatureInjector implements BiomeTemperatureInjector {
	private static final String FIELD_BIOMETEMP_SRGNAME = "field_76750_F";

	private static ReflectiveBiomeTemperatureInjector cachedInstance;

	private final Field biomeTemperatureField;
	public final Map<Biome, Float> originals;
	
	protected ReflectiveBiomeTemperatureInjector()
	{
		this.biomeTemperatureField = ObfuscationReflectionHelper.findField(Biome.class, FIELD_BIOMETEMP_SRGNAME);
		this.originals = new HashMap<Biome, Float>();

		if(!this.biomeTemperatureField.isAccessible())
			this.biomeTemperatureField.setAccessible(true);
	}

	@Override
	public float getOriginalBiomeTemperature(Biome biome)
	{
		return this.originals.computeIfAbsent(biome, Biome::getDefaultTemperature);
	}

	@Override
	public void setAbsoluteBiomeTemperature(Biome biome, float temperature)
	{
		try
		{
			if(!this.originals.containsKey(biome))
				this.originals.put(biome, biome.getDefaultTemperature());
			this.biomeTemperatureField.set(biome, temperature);
		}
		catch(ReflectiveOperationException exc)
		{
			SurvivalInc.logger.error("Biome temperature injection failed!");
			exc.printStackTrace();
		}
	}

	@Override
	public void resetBiomeTemperature(Biome biome)
	{
		// If the map doesn't contain the biome entry, it means it hasn't been affected yet,
		// and so it already has the wanted default value.
		if(this.originals.containsKey(biome))
			this.setAbsoluteBiomeTemperature(biome, this.originals.get(biome));
	}

	public static ReflectiveBiomeTemperatureInjector getInstance()
	{
		if(ReflectiveBiomeTemperatureInjector.cachedInstance == null)
			ReflectiveBiomeTemperatureInjector.cachedInstance = new ReflectiveBiomeTemperatureInjector();
		return ReflectiveBiomeTemperatureInjector.cachedInstance;
	}
}
