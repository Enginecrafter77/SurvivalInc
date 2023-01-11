package enginecrafter77.survivalinc.season;

import net.minecraft.world.biome.Biome;

public interface BiomeTemperatureInjector {
	public float getOriginalBiomeTemperature(Biome biome);
	public void setAbsoluteBiomeTemperature(Biome biome, float temperature);

	public default void setBiomeTemperatureOffset(Biome biome, float temperatureOffset)
	{
		this.setAbsoluteBiomeTemperature(biome, this.getOriginalBiomeTemperature(biome) + temperatureOffset);
	}

	public default void resetBiomeTemperature(Biome biome)
	{
		this.setAbsoluteBiomeTemperature(biome, this.getOriginalBiomeTemperature(biome));
	}
}
