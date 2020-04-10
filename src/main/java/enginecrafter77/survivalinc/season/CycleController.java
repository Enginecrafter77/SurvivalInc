package enginecrafter77.survivalinc.season;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CycleController {

	/*
	 * Everything to do with the day and night cycle, whether it be toggling it
	 * or modifying it as the seasons go on.
	 */

	// Timer used to add or subtract a tick
	private int tickTimer = 0;

	// Add/subtract a tick every <targetTicks> ticks
	private int targetTicks = 0;
	private int targetTicksNight = 0;

	// isNight bool
	private boolean isNight = false;
	
	public void applySeason(Season season)
	{
		// Turn on day-night cycle
		this.toggleCycle(true);
		
		// Alter cycle if needed
		switch(season)
		{
		case SUMMER:
			this.changeLengthOfCycle(15000);
			break;
		case WINTER:
			this.changeLengthOfCycle(9000);
			break;
		default:
			this.changeLengthOfCycle(12000);
			break;
		}
	}
	
	public void toggleCycle(boolean enable)
	{
		// Server
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

		// Server world
		World world = server.getEntityWorld();

		// Gamerules
		GameRules gamerules = world.getGameRules();

		// Toggle it.
		if(enable)
		{
			gamerules.setOrCreateGameRule("doDaylightCycle", "true");
			SurvivalInc.logger.info("Day-night cycle enabled.");
		}

		else
		{
			gamerules.setOrCreateGameRule("doDaylightCycle", "false");
			SurvivalInc.logger.info("Day-night cycle disabled.");
		}
	}

	// Every morning we'll check and see how long the day phase of the cycle
	// should be according to season.
	// That new length is pushed into this method.
	public void changeLengthOfCycle(int lengthOfDay)
	{

		// The normal length of day in ticks is 12000.
		// We'll need to find the difference between the new length and 12000;
		// that's the amount of extra ticks we'll need.
		int dayDiff = lengthOfDay - 12000;

		// Added ticks will be spread evenly amongst each game tick. Well,
		// almost.
		if (dayDiff == 0)
		{

			targetTicks = 0;
			targetTicksNight = 0;
		}

		else
		{

			targetTicks = 12000 / dayDiff;
			targetTicksNight = targetTicks * -1;
		}
	}

	// This actually does the tick addition/subtraction
	// Tick addition means a shorter phase.
	// Tick subtraction means a longer phase.
	public void alter(World world)
	{

		// Figure out whether to add ticks or subtract ticks.
		// This is determined by whether it's daytime or nighttime, and whether
		// targetTicks is positive or negative
		// If add, add one to tickTimer. If subtract, subtract one from
		// tickTimer.

		// No change? Do nothing
		if (targetTicks == 0)
		{

			;
		}

		// Longer day, shorter night
		else if (targetTicks > 0)
		{

			// Daytime?
			if (world.getWorldTime() % 24000 >= 0 && world.getWorldTime() % 24000 < 12000)
			{

				isNight = false;
				tickTimer++;
				subtractTick(world);
			}

			else
			{

				isNight = true;
				tickTimer--;
				addTick(world);
			}
		}

		// Shorter day, longer night
		else
		{

			// Daytime?
			if (world.getWorldTime() % 24000 >= 0 && world.getWorldTime() % 24000 < 12000)
			{

				isNight = false;
				tickTimer--;
				addTick(world);
			}

			else
			{

				isNight = true;
				tickTimer++;
				subtractTick(world);
			}
		}
	}

	// Add tick (shorter phase)
	private void addTick(World world)
	{

		// Current world time
		long currentTime = world.getWorldTime();

		// Now... is tickTimer at its target?
		if (tickTimer < targetTicks && !isNight)
		{

			world.setWorldTime(currentTime + 1);
			tickTimer = 0;
		}

		else if (tickTimer < targetTicksNight && isNight)
		{

			world.setWorldTime(currentTime + 1);
			tickTimer = 0;
		}
	}

	// Subtract tick (longer phase)
	private void subtractTick(World world)
	{

		// Current world time
		long currentTime = world.getWorldTime();

		// Now... is tickTimer at its target?
		if (tickTimer > targetTicks && !isNight)
		{

			world.setWorldTime(currentTime - 1);
			tickTimer = 0;
		}

		else if (tickTimer > targetTicksNight && isNight)
		{

			world.setWorldTime(currentTime - 1);
			tickTimer = 0;
		}
	}
}