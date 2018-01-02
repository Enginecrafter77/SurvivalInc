package net.schoperation.schopcraft.season;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.schoperation.schopcraft.SchopCraft;
import net.schoperation.schopcraft.SchopWorldData;
import net.schoperation.schopcraft.packet.SchopPackets;
import net.schoperation.schopcraft.packet.SeasonPacket;
import net.schoperation.schopcraft.util.DataManager;

@Mod.EventBusSubscriber
public class WorldSeason {
	
	/*
	 * The main class that controls the seasons and the universe. Alright, I exaggerated on the universe part.
	 * This does not affect temperature. That's another file.
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
	private final SnowMelter melter = new SnowMelter();
	private final LeavesChanger leaves = new LeavesChanger();
	
	
	// This fires on server startup. Load the data from file here
	public static void getSeasonData(Season dataSeason, int days) {
		
		season = dataSeason;
		daysIntoSeason = days;
		
		// Change biome temperatures immediately.
		BiomeTempController temporaryController = new BiomeTempController();
		temporaryController.changeBiomeTemperatures(season, daysIntoSeason);
		temporaryController = null;
	}
	
	
	@SubscribeEvent
	public void onPlayerLogsIn(PlayerLoggedInEvent event) {
		
		EntityPlayer player = event.player;
		
		if (player instanceof EntityPlayerMP) {
			
			// Sync server stuff with client.
			// This is needed so the snow, foliage, and stuff gets rendered correctly.
			int seasonInt = SchopWorldData.seasonToInt(season);
			IMessage msg = new SeasonPacket.SeasonMessage(seasonInt, daysIntoSeason);
			SchopPackets.net.sendTo(msg, (EntityPlayerMP) player); 
		}
		
		// Turn on day-night cycle
		cycleController.toggleCycle(true);
		
		// Alter cycle if needed
		if (season == Season.SUMMER) {
			
			cycleController.changeLengthOfCycle(15000);
		}
		
		else if (season == Season.WINTER) {
			
			cycleController.changeLengthOfCycle(9000);
		}
		
		else {
			
			cycleController.changeLengthOfCycle(12000);
		}
	}
	
	@SubscribeEvent
	public void onPlayerLogsOut(PlayerLoggedOutEvent event) {

		// Turn off day-night cycle if no more people on
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		int playerCount = server.getCurrentPlayerCount();
		
		if (playerCount <= 1) {
			
			cycleController.toggleCycle(false);
		}
		
		// Is this singleplayer? If so, reset their temps here.
		if (server.isSinglePlayer()) {
			
			biomeTemp.resetBiomeTemperatures();
		}
		
		// No? Do it via packet.
		else if (event.player instanceof EntityPlayerMP) {
			
			int seasonInt = SchopWorldData.seasonToInt(season);
			IMessage msg = new SeasonPacket.SeasonMessage(seasonInt, -21);
			SchopPackets.net.sendTo(msg, (EntityPlayerMP) event.player); 
		}
	}
	
	// The clock - determines when to move on to stuff
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event) {
		
		if (event.getEntity() instanceof EntityPlayer) {
			
			// Player
			EntityPlayer player = (EntityPlayer) event.getEntity();
			
			// World
			World world = player.world;
			
			// Server-side
			if (!world.isRemote) {
				
				// Time
				long worldTime = world.getWorldTime();
				
				// Is it early morning? It's not exactly 0 because of beds. And it's an odd number because CycleController.
				if (worldTime % 24000 == 41) {
					
					// Increment daysIntoSeason
					daysIntoSeason++;
					
					// Is it the next season?
					if (daysIntoSeason > season.getLength(season)) {
						
						// Head on over to the next season.
						daysIntoSeason = 0;
						season = season.nextSeason();
					}
					
					// Is it the start of spring or autumn? Initiate initial leaf changing.
					if (daysIntoSeason == 0) {
						
						if (season == Season.SPRING || season == Season.AUTUMN) {
							
							leaves.changeInitial(season, world, player);
						}
					}
					
					// Save world data
					DataManager.saveData(season, daysIntoSeason);
					
					// Change temperatures
					biomeTemp.changeBiomeTemperatures(season, daysIntoSeason);
					
					// Send new season data to client
					int seasonInt = SchopWorldData.seasonToInt(season);
					IMessage msg = new SeasonPacket.SeasonMessage(seasonInt, daysIntoSeason);
					SchopPackets.net.sendTo(msg, (EntityPlayerMP) player); 
					
					// Determine the weather. The season is the main factor.
					float randWeather = (float) Math.random();
					
					if (randWeather < season.getPrecipitationChance()) {
						
						weatherHandler.makeItRain(world, season);
						didRainStart = false;
					}
					
					else {
						
						weatherHandler.makeItNotRain(world);
					}
					
					// Change the length of day and night if needed
					if (season == Season.SUMMER) {
						
						cycleController.changeLengthOfCycle(15000);
					}
					
					else if (season == Season.WINTER) {
						
						cycleController.changeLengthOfCycle(9000);
					}
					
					else {
						
						cycleController.changeLengthOfCycle(12000);
					}
					
					// Log it
					SchopCraft.logger.info("Day " + daysIntoSeason + " of " + season + ".");
				}
				
				// Affect daytime
				cycleController.alter(world);
				
				// If it's going to rain, we'll need to send the rain data when it starts.
				if (world.isRaining() && !didRainStart) {
					
					didRainStart = true;
					weatherHandler.applyToRain(world);
				}
				
				// We need to melt snow and ice manually in the spring.
				// Summer has a different melting method.
				if (season == Season.SPRING) {
					
					melter.melt(world, player, daysIntoSeason);
				}
			}
		}
	}
	
	// Helps to melt snow in summer. Where there shouldn't be any snow.
	// Also does the leaves.
	@SubscribeEvent
	public void onChunkWalkIn(EntityEvent.EnteringChunk event) {
		
		// Was this a player?
		if (event.getEntity() instanceof EntityPlayer) {
			
			EntityPlayer player = (EntityPlayer) event.getEntity();
			
			// Is it summer? Then let's try to remove some snow and ice.
			if (season == Season.SUMMER && !player.world.isRemote) {
				
				int chunkCoordX = event.getNewChunkX();
				int chunkCoordZ = event.getNewChunkZ();
				melter.meltCompletely(chunkCoordX, chunkCoordZ, player.world);
			}
			
			// How about spring or autumn? Let's try to change some leaves.
			else if (season == Season.SPRING || season == Season.AUTUMN) {
				
				if (!player.world.isRemote) {
					
					int chunkCoordX = event.getNewChunkX();
					int chunkCoordZ = event.getNewChunkZ();
					leaves.change(chunkCoordX, chunkCoordZ, player.world, season);
				}
			}
		}
	}
	
	// Change grass color in Autumn and Summer
	// It'll stay in this class as it isn't much code.
	@SubscribeEvent
	public void biomeGrass(BiomeEvent.GetGrassColor event) {
		
		// Get biome and original temperature of that biome.
		Biome biome = event.getBiome();
		float originalTemperature = biomeTemp.getOriginalTemperature(biome);
		
		// Determine what season it is
		if (season == Season.SUMMER) {
			
			// Is the temperature above 0.8? We'll change its grass color then.
			if (originalTemperature >= 0.80f) {
				
				// New grass color
				final int summerGrassColor = 13296206;
				
				// Is the grass color already this color?
				if (event.getNewColor() != summerGrassColor) {
					
					event.setNewColor(summerGrassColor);
				}
			}
		}
		
		else if (season == Season.AUTUMN) {
			
			// BELOW 0.8(1)?
			if (originalTemperature <= 0.80f) {
				
				// New grass color
				final int autumnGrassColor = 13925888;
				
				// Is the grass color already this color?
				if (event.getNewColor() != autumnGrassColor) {
					
					event.setNewColor(autumnGrassColor);
				}
			}
		}
	}
}