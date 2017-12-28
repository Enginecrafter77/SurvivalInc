package net.schoperation.schopcraft.season;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
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
	
	
	// This fires on server startup. Load the data from file here
	public static void getSeasonData(Season dataSeason, int days) {
		
		season = dataSeason;
		daysIntoSeason = days;
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
		SeasonCycle.toggleCycle(true);
	}
	
	@SubscribeEvent
	public void onPlayerLogsOut(PlayerLoggedOutEvent event) {
		
		// Turn off day-night cycle if no more people on
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		int playerCount = server.getCurrentPlayerCount();
		
		if (playerCount <= 1) {
			
			SeasonCycle.toggleCycle(false);
		}
	}
	
	// To help set the rain stuff correctly
	private static boolean didRainStart = true;
	
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
				
				// Is it early morning? It's not exactly 0 because of beds.
				if (worldTime % 24000 == 40) {
					
					// Increment daysIntoSeason
					daysIntoSeason++;
					
					// Is it the next season?
					if (daysIntoSeason > season.getLength(season)) {
						
						// Head on over to the next season.
						daysIntoSeason = 0;
						season = season.nextSeason();
					}
					
					// Save world data
					DataManager.saveData(season, daysIntoSeason);
					
					// Change temperatures
					BiomeTemp.changeBiomeTemperatures(season, daysIntoSeason, true);
					
					int seasonInt = SchopWorldData.seasonToInt(season);
					IMessage msg = new SeasonPacket.SeasonMessage(seasonInt, daysIntoSeason);
					SchopPackets.net.sendTo(msg, (EntityPlayerMP) player); 
					
					// Determine the weather. The season is the main factor.
					float randWeather = (float) Math.random();
					
					if (randWeather < season.getPrecipitationChance()) {
						
						WeatherHandler.makeItRain(world, season);
						didRainStart = false;
					}
					
					else {
						
						WeatherHandler.makeItNotRain(world);
					}
					
					// Log it
					SchopCraft.logger.info("Day " + daysIntoSeason + " of " + season + ".");
				}
				
				// If it's going to rain, we'll need to send the rain data when it starts.
				if (world.isRaining() && !didRainStart) {
					
					didRainStart = true;
					WeatherHandler.applyToRain(world);
				}
				
				// We need to melt snow and ice manually in the spring and summer.
				if (season == Season.SPRING || season == Season.SUMMER) {
					
					SnowMelter.melt(world, player, season, daysIntoSeason);
				}
			}
		}
	}
}
