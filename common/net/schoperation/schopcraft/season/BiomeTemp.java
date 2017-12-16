package net.schoperation.schopcraft.season;

import java.util.Iterator;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.schoperation.schopcraft.SchopCraft;

public class BiomeTemp {
	
	/*
	 * This stores the original biome temperatures, modifies the base temps if necessary, and changes them as the seasons go by.
	 */
	
	// The array of temperatures.
	private static float[] temperatures;
	
	// This method goes through every biome and grabs the original temperature. Called at the beginning.
	public static void storeOriginalTemperatures() {
		
		// Number of biomes
		int biomeNumber = ForgeRegistries.BIOMES.getValues().size();
		
		// Allocate enough space for array
		temperatures = new float[biomeNumber];
		
		// Iterator
		Iterator<Biome> biomeList = ForgeRegistries.BIOMES.getValues().iterator();
		int counter = 0;
		
		// Iterate kek
		while (biomeList.hasNext()) {
			
			// The biome
			Biome choosenBiome = biomeList.next();
			
			// Grab the temperature and store it and go
			float biomeTemp = choosenBiome.getDefaultTemperature();
			temperatures[counter] = biomeTemp;
			SchopCraft.logger.info("Biome #" + counter + " is " + choosenBiome.getBiomeName() + ", whose temp is " + temperatures[counter]);
			counter++;
			
			
		}
	}
	
	// Want some original temp for a specific biome? Use this thingy-a-method here!
	public static float getOriginalTemperature(Biome biome) {
		
		// Iterator
		Iterator<Biome> biomeList = ForgeRegistries.BIOMES.getValues().iterator();
		int counter = 0;
		
		// Iterate kek
		while (biomeList.hasNext()) {
			
			// The biome
			Biome choosenBiome = biomeList.next();
			
			// Is this the right one? Normal biome doesn't work, how about name?
			if (biome.equals(choosenBiome)) {
				
				return temperatures[counter];
			}
			
			else {
				
				counter++;
			}
		}
		
		return counter;
	}
}
