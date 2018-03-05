package schoperation.schopcraft.season.modifier;

import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import schoperation.schopcraft.SchopCraft;
import schoperation.schopcraft.season.Season;

import java.util.Random;

public class WeatherHandler {
	
	/*
	 * Handles the weather, every f*cking day. So the lying weather man on TV DOES control the weather! 
	 */
	
	// Variables used to actually change weather
	private static boolean isThunder;
	private static int tickDuration = 0;
	
	// This doesn't actually change the weather, but randomizes it, and pushes stuff to a method that does change it.
	public void makeItRain(World world, Season season) {
		
		// Time for more randomness, to determine whether to have a thunderstorm or not.
		// Summer usually has more thunderstorms than other seasons, followed by spring and autumn.
		float luckyNumber = 0f;
		
		if (season == Season.SPRING) {
			
			luckyNumber = 0.5f;
		}
		
		else if (season == Season.SUMMER) {
			
			luckyNumber = 0.75f;
		}
		
		else {
			
			luckyNumber = 0.3f;
		}
		
		// Now the actual randomness. Gonna be a lot, so may as well make a new generator.
		Random rand = new Random();
		
		// Determine if thunderstorm or not
		float rand1 = rand.nextFloat();
		
		if (rand1 < luckyNumber) {
			
			isThunder = true;
		}
		
		else {
			
			isThunder = false;
		}
		
		// Determine when to start
		// It shall be a tick number between 0 and 9000
		int ticksUntilStart;
		ticksUntilStart = rand.nextInt(9000);
		
		// Determine how long the rain shall last		
		tickDuration = rand.nextInt(18000);
		
		if (tickDuration < 9000) {
			
			tickDuration += 9000;
		}
		
		// Actually change the weather
		world.getWorldInfo().setRainTime(ticksUntilStart);
		
		SchopCraft.logger.info("Rain is on the way in " + ticksUntilStart + " ticks.");
		// The other variables will be used when it actually starts raining.
	}
	
	public void makeItNotRain(World world) {
		
		// WorldInfo instance
		WorldInfo worldinfo = world.getWorldInfo();
		
		// Make it not rain
		worldinfo.setRaining(false);
		worldinfo.setThundering(false);
		worldinfo.setRainTime(100000000);
	}
	
	public void applyToRain(World world) {
		
		// Apply the stuff
		// WorldInfo instance
		WorldInfo worldinfo = world.getWorldInfo();
		
		worldinfo.setThundering(isThunder);
		worldinfo.setRainTime(tickDuration);
		
		// Logit
		SchopCraft.logger.info("It's raining now. It's " + isThunder + " that it is thundering. It'll last for " + tickDuration + " ticks.");
	}
}