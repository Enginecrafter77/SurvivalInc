package schoperation.schopcraft.season;

import schoperation.schopcraft.config.SchopConfig;

/*
 * Basic properties for the seasons. Lotta switch statements, because enum stuff
 */

public enum Season {
	
	WINTER, 
	SPRING, 
	SUMMER, 
	AUTUMN;
	
	// Goto next season
	public Season nextSeason() {
		
		switch(Season.this) {
		
			case WINTER: return SPRING;
			case SPRING: return SUMMER;
			case SUMMER: return AUTUMN;
			case AUTUMN: return WINTER;
			default: return SPRING;
		}
	}
	
	// Get previous season
	public Season prevSeason(Season season) {
		
		switch(season) {
		
			case WINTER: return AUTUMN;
			case SPRING: return WINTER;
			case SUMMER: return SPRING;
			case AUTUMN: return SUMMER;
			default: return SPRING;
		}
	}
	
	// Methods for setting the seasons apart.
	// Length of the season in Minecraft days
	public int getLength(Season season) {
		
		switch(season) {
		
			case WINTER: return SchopConfig.SEASONS.winterLength;
			case SPRING: return SchopConfig.SEASONS.springLength;
			case SUMMER: return SchopConfig.SEASONS.summerLength;
			case AUTUMN: return SchopConfig.SEASONS.autumnLength;
			default: return 14;
		}
	}
	
	/* Temperature difference of the season, relation to the original temperature of the biome.
	 * With the changing seasons come the changing of the biome temperatures. Mid-spring and early-autumn are the points where the temperatures are normal (vanilla values).
	 * The temperatures discretely increase from mid-winter to mid-summer, and decrease for the other half of the time. 
	 * Since I've made the effective temperature limited to between -0.2 and 1.5:
	 * 
	 * -From mid-spring to mid-summer, the biome temperature will gradually increase by 0.5.
	 * -From mid-summer to mid-autumn, it will decrease gradually by 0.8.
	 * -From mid-autumn to mid-winter, it will decrease gradually by 0.7.
	 * -From mid-winter to mid-spring, it will increase gradually by 1.0.
	 * 
	 * (unevenly)
	 * 
	 * The longer the length of a season, the more gradual the change is.
	 * It also has to check if we are in the middle of a season, because in the summer and winter, it changes direction.
	 * Remember, this is called at the beginning and middle of each season.
	 */
	public float getTemperatureDifference(int daysElapsed) {
		
		switch(Season.this) {
		
			case WINTER: 
				
				// If we're at the second half of the season
				if (daysElapsed > (getLength(WINTER) / 2)) {
					
					return -0.6f;
				}
				
				else {
					
					return -1.0f;
				}
				
			case SPRING:
				
				if (daysElapsed > (getLength(SPRING) / 2)) {
					
					return 0.2f;
				}
				
				else {
					
					return 0.0f;
				}
				
			case SUMMER:
				
				if (daysElapsed > (getLength(SUMMER) / 2)) {
					
					return 0.0f;
				}
				
				else {
					
					return 0.5f;
				}
				
			case AUTUMN: 
				
				if (daysElapsed > (getLength(AUTUMN) / 2)) {
					
					return -0.6f;
				}
				
				else {
					
					return -0.3f;
				}
				
			default: return 0.0f;
		}
	}
	
	// Chance of getting precipitation on a particular day during a season.
	public float getPrecipitationChance() {
		
		switch(Season.this) {
		
			case WINTER: return 0.60f;
			case SPRING: return 0.70f;
			case SUMMER: return 0.30f;
			case AUTUMN: return 0.40f;
			default: return 0.50f;
		}
	}
}