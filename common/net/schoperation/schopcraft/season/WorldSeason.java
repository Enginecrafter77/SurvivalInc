package net.schoperation.schopcraft.season;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

@Mod.EventBusSubscriber
public class WorldSeason {
	
	/*
	 * The main class that controls the seasons, the temperature, and the universe. Alright, I exaggerated on the universe part.
	 */
	
	// This fires on server startup. Load the data from file here
	public static void loadSeasonData() {
		
		// Instance of the server.
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		// Instance of the server world.
		World world = server.getEntityWorld();
		
		// Load stuff from world data file
		SeasonData data = SeasonData.load(world);
		
		// Enum Season
		Season season = data.getSeasonFromData();
		
		// Seasonticks
		int seasonTicks = data.seasonTicks; // TODO perhaps just save how many days have gone by instead of ticks?
		
		
		season = season.nextSeason();
		System.out.println("NEXT SEASON IS: " + season);
	}
	
	
	@SubscribeEvent
	public void onPlayerLogsIn(PlayerLoggedInEvent event) {
		
		EntityPlayer player = event.player;
		
		if (player instanceof EntityPlayerMP) {
			
			// Sync server season and seasonTicks with client
		}
	}
	
	// The clock
	@SubscribeEvent
	public void worldTick(WorldTickEvent event) {
		
	}
}
