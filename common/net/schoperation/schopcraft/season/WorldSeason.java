package net.schoperation.schopcraft.season;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.schoperation.schopcraft.SchopCraft;

@Mod.EventBusSubscriber
public class WorldSeason {
	
	/*
	 * The main class that controls the seasons, the temperature, and the universe. Alright, I exaggerated on the universe part.
	 */
	
	// Wonderful global variables for this class yay
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
			
			// Sync server season and daysIntoSeason with client
			// TODO Gotta use a packet, that's for next time
			if (player.world.isRemote) {
				
				SchopCraft.logger.info("Hello! According to the logical CLIENT, the season is " + season + " and daysIntoSeason is " + daysIntoSeason);
			}
			
			else {
				
				SchopCraft.logger.info("Hello! According to the logical SERVER, the season is " + season + " and daysIntoSeason is " + daysIntoSeason);
			}
		}
	}
	
	// The clock
	@SubscribeEvent
	public void worldTick(WorldTickEvent event) {
		
	}
}
