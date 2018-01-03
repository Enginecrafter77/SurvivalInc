package net.schoperation.schopcraft.season;

import net.minecraft.world.biome.Biome;

public class SeasonTweaks {
	
	/*
	 * This class contains some miscellaneous season crap.
	 * Crap that doesn't need its own class, nor would it be nice cluttering WorldSeason.class 
	 */
	
	/*
	 * These variables and this method changes grass colors as the seasons progress.
	 */
	// Grass colors.
	private final int SUMMER_GRASS_COLOR = 13296206;
	private final int AUTUMN_GRASS_COLOR = 13925888;
	
	public int getSeasonGrassColor(Season season, Biome biome) {
		
		// Get temperature
		float temp = biome.getDefaultTemperature();
		
		// Determine what season it is
		if (season == Season.SUMMER) {
			
			// Is the temperature above 0.5? We'll change its grass color then.
			if (temp >= 0.50f) {
				
				return SUMMER_GRASS_COLOR;
			}
			
			else {
				
				return 0;
			}
		}
		
		else if (season == Season.AUTUMN) {
			
			// BELOW 0.8?
			if (temp <= 0.80f) {
					
				return AUTUMN_GRASS_COLOR;
			}
			else {
				
				return 0;
			}
		}
		
		else {
			
			return 0;
		}
	}
}