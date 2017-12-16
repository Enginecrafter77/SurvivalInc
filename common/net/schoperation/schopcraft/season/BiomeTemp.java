package net.schoperation.schopcraft.season;

import java.util.Iterator;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.schoperation.schopcraft.SchopCraft;

public class BiomeTemp {
	
	/*
	 * This stores the original biome temperatures, modifying the base temps if necessary.
	 * It also deals with changing the temperature of each biome according to the season, and where we are in the season.
	 */
	
	// The array of temperatures. Plz don't touch it outside of this class. If outside, you can only look.
	public static float[] temperatures;
	
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
			counter++;
		}
		
		SchopCraft.logger.info("Stored " + (counter + 1) + " biome temperatures.");
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
			
			// Is this the right one?
			if (biome.equals(choosenBiome)) {
				
				return temperatures[counter];
			}
			
			else {
				
				counter++;
			}
		}
		
		return counter;
	}
	
	// TODO do this damn thing
	public static void changeBiomeTemperatures(Season season, int daysIntoSeason) {
		
		
	}
}
