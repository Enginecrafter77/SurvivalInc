package enginecrafter77.survivalinc.season;

import net.minecraft.block.BlockCrops;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.List;

import enginecrafter77.survivalinc.CommonProxy;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.SchopConfig;
import enginecrafter77.survivalinc.net.SeasonPacket;
import enginecrafter77.survivalinc.util.SchopWorldData;
import enginecrafter77.survivalinc.util.WorldDataMgr;

@Mod.EventBusSubscriber
public class WorldSeason {

	/*
	 * The main class that controls the seasons and the universe. Alright, I
	 * exaggerated on the universe part. This does not affect temperature.
	 * That's another file.
	 */

	// Wonderful variables for this class yay
	// Anytime these change, save them
	private static Season season;
	private static int daysIntoSeason;

	// To help set the rain stuff correctly
	private boolean didRainStart = true;

	// Handlers and Melters.
	private final WeatherHandler weatherHandler = new WeatherHandler();
	private final CycleController cycleController = new CycleController();
	private final BiomeTempController biomeTemp = new BiomeTempController();
	private final SeasonTweaks tweaks = new SeasonTweaks();
	private final SnowMelter melter = new SnowMelter();
	private final LeavesChanger leaves = new LeavesChanger();

	// This fires on server startup. Load the data from file here
	public static void getSeasonData(Season dataSeason, int days)
	{

		season = dataSeason;
		daysIntoSeason = days;

		// Change biome temperatures immediately.
		if (SchopConfig.SEASONS.aenableSeasons)
		{

			BiomeTempController temporaryController = new BiomeTempController();
			temporaryController.changeBiomeTemperatures(season, daysIntoSeason);
			temporaryController = null;
		}
	}

	@SubscribeEvent
	public void onPlayerLogsIn(PlayerLoggedInEvent event)
	{

		if (SchopConfig.SEASONS.aenableSeasons)
		{

			EntityPlayer player = event.player;

			if (player instanceof EntityPlayerMP)
			{

				// Sync server stuff with client.
				// This is needed so the snow, foliage, and stuff gets rendered
				// correctly.
				int seasonInt = SchopWorldData.seasonToInt(season);
				IMessage msg = new SeasonPacket.SeasonMessage(seasonInt, daysIntoSeason);
				CommonProxy.net.sendTo(msg, (EntityPlayerMP) player);
			}

			// Turn on day-night cycle
			cycleController.toggleCycle(true);

			// Alter cycle if needed
			if (season == Season.SUMMER)
			{

				cycleController.changeLengthOfCycle(15000);
			}

			else if (season == Season.WINTER)
			{

				cycleController.changeLengthOfCycle(9000);
			}

			else
			{

				cycleController.changeLengthOfCycle(12000);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLogsOut(PlayerLoggedOutEvent event)
	{

		if (SchopConfig.SEASONS.aenableSeasons)
		{

			// Turn off day-night cycle if no more people on
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

			int playerCount = server.getCurrentPlayerCount();

			if (playerCount <= 1)
			{

				cycleController.toggleCycle(false);
			}

			// Is this singleplayer? If so, reset their temps here.
			if (server.isSinglePlayer())
			{

				biomeTemp.resetBiomeTemperatures();
			}

			// No? Do it via packet.
			else if (event.player instanceof EntityPlayerMP)
			{

				int seasonInt = SchopWorldData.seasonToInt(season);
				IMessage msg = new SeasonPacket.SeasonMessage(seasonInt, -21);
				CommonProxy.net.sendTo(msg, (EntityPlayerMP) event.player);
			}
		}
	}

	// The clock - determines when to move on to stuff
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event)
	{

		if (event.getEntity() instanceof EntityPlayer)
		{

			// Player
			EntityPlayer player = (EntityPlayer) event.getEntity();

			// World
			World world = player.world;

			// Server-side
			if (!world.isRemote && SchopConfig.SEASONS.aenableSeasons)
			{

				// Time
				long worldTime = world.getWorldTime();

				// Is it early morning? It's not exactly 0 because of beds. And
				// it's an odd number because CycleController.
				if (worldTime % 24000 == 41)
				{

					// Increment daysIntoSeason
					daysIntoSeason++;

					// Is it the next season?
					if (daysIntoSeason > season.length)
					{

						// Head on over to the next season.
						daysIntoSeason = 1;
						
						do
						{
							season = season.getFollowing(1);
						}
						while(season.length == 0);
					}

					// Is it the start of spring or autumn? Initiate initial
					// leaf changing.
					if (daysIntoSeason == 1)
					{
						if (season == Season.SPRING || season == Season.AUTUMN)
						{
							leaves.changeInitial(season, world, player);
						}
					}

					// Save world data
					WorldDataMgr.save(season, daysIntoSeason);

					// Send new season data to client
					int seasonInt = SchopWorldData.seasonToInt(season);
					IMessage msg = new SeasonPacket.SeasonMessage(seasonInt, daysIntoSeason);
					CommonProxy.net.sendTo(msg, (EntityPlayerMP) player);

					// Change temperatures
					biomeTemp.changeBiomeTemperatures(season, daysIntoSeason);

					// Determine the weather. The season is the main factor.
					float randWeather = (float) Math.random();

					if (randWeather < season.rainfallchance)
					{

						weatherHandler.makeItRain(world, season);
						didRainStart = false;
					}

					else
					{

						weatherHandler.makeItNotRain(world);
					}

					// Change the length of day and night if needed
					if (season == Season.SUMMER)
					{

						cycleController.changeLengthOfCycle(15000);
					}

					else if (season == Season.WINTER)
					{

						cycleController.changeLengthOfCycle(9000);
					}

					else
					{

						cycleController.changeLengthOfCycle(12000);
					}

					// Log it
					SurvivalInc.logger.info("Day " + daysIntoSeason + " of " + season + ".");
				}

				// Affect daytime
				if (SchopConfig.SEASONS.aenableDayLength)
				{

					cycleController.alter(world);
				}

				// If it's going to rain, we'll need to send the rain data when
				// it starts.
				if (world.isRaining() && !didRainStart)
				{

					didRainStart = true;
					weatherHandler.applyToRain(world);
				}

				// We need to melt snow and ice manually in the spring.
				// Summer has a different melting method.
				if (season == Season.SPRING && player.dimension == 0)
				{

					melter.melt(world, player, daysIntoSeason);
				}
			}
		}
	}

	// Helps to melt snow in summer. Where there shouldn't be any snow.
	// Also does the leaves.
	@SubscribeEvent
	public void onChunkWalkIn(EntityEvent.EnteringChunk event)
	{

		// Was this a player?
		if (event.getEntity() instanceof EntityPlayer)
		{

			EntityPlayer player = (EntityPlayer) event.getEntity();

			if (SchopConfig.SEASONS.aenableSeasons)
			{

				// Is it summer? Then let's try to remove some snow and ice.
				if (season == Season.SUMMER)
				{

					if (!player.world.isRemote && player.dimension == 0)
					{

						int chunkCoordX = event.getNewChunkX();
						int chunkCoordZ = event.getNewChunkZ();
						melter.meltCompletely(chunkCoordX, chunkCoordZ, player.world);
					}
				}

				// How about spring or autumn? Let's try to change some leaves.
				else if (season == Season.SPRING || season == Season.AUTUMN)
				{

					if (!player.world.isRemote && player.dimension == 0)
					{

						int chunkCoordX = event.getNewChunkX();
						int chunkCoordZ = event.getNewChunkZ();
						leaves.change(chunkCoordX, chunkCoordZ, player.world, season);
					}
				}
			}
		}
	}

	// Change grass color in Autumn and Summer.
	@SubscribeEvent
	public void biomeGrass(BiomeEvent.GetGrassColor event)
	{

		int color;

		if (SchopConfig.SEASONS.aenableSeasons)
		{

			color = tweaks.getSeasonGrassColor(season, event.getBiome());
		}

		else
		{

			color = 0;
		}

		if (color == 0)
		{

			;
		}

		else if (event.getNewColor() != color)
		{

			event.setNewColor(color);
		}
	}

	// Add bonus harvest drops from crops in the Autumn.
	@SubscribeEvent
	public void harvestDrops(BlockEvent.HarvestDropsEvent event)
	{

		// Is it actually Autumn?
		if (!event.getWorld().isRemote && season == Season.AUTUMN && SchopConfig.SEASONS.aenableSeasons)
		{

			// Is this a legit crop?
			if (event.getState().getBlock() instanceof BlockCrops)
			{

				// Block
				BlockCrops crop = (BlockCrops) event.getState().getBlock();

				// Drops
				List<ItemStack> drops = event.getDrops();

				// Now, we should only award bonus drops if it's fully grown.
				if (crop.isMaxAge(event.getState()))
				{

					// Let's go
					drops = tweaks.addBonusHarvest(drops);
				}
			}
		}
	}

	// Make nothing grow in winter, and more grow in summer
	@SubscribeEvent
	public void affectGrowth(BlockEvent.CropGrowEvent.Pre event)
	{

		// Server-side
		if (!event.getWorld().isRemote)
		{

			// Winter?
			if (season == Season.WINTER && SchopConfig.SEASONS.aenableSeasons)
			{

				event.setResult(Event.Result.DENY);
			}

			// Summer?
			else if (season == Season.SUMMER && SchopConfig.SEASONS.aenableSeasons)
			{

				event.setResult(Event.Result.ALLOW);
			}

			// Backwards-compat code TODO remove next release
			GameRules gamerules = event.getWorld().getGameRules();
			gamerules.setOrCreateGameRule("randomTickSpeed", "3");
		}
	}
}