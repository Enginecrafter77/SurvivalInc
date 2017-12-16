package net.schoperation.schopcraft.season;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.schoperation.schopcraft.SchopCraft;
import net.schoperation.schopcraft.SchopWorldData;
import net.schoperation.schopcraft.packet.SchopPackets;
import net.schoperation.schopcraft.packet.SeasonPacket;

@Mod.EventBusSubscriber
public class WorldSeason {
	
	/*
	 * The main class that controls the seasons and the universe. Alright, I exaggerated on the universe part.
	 * This does not affect temperature. That's another file.
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
			
			// Sync server stuff with client.
			// This is needed so the snow, foliage, and stuff gets rendered correctly.
			int seasonInt = SchopWorldData.seasonToInt(season);
			IMessage msg = new SeasonPacket.SeasonMessage(seasonInt, daysIntoSeason);
			SchopPackets.net.sendTo(msg, (EntityPlayerMP) player); 
		}
	}
	
	// The clock - determines when to move on to stuff
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event) {
		
		if (event.getEntity() instanceof EntityPlayer) {
			
			// Player
			EntityPlayer player = (EntityPlayer) event.getEntity();
			
			// Server-side
			if (!player.world.isRemote) {
				
				// ehehehehehehehe this is pretty hacky but it'll work
				// TODO dont do this here, move it to BiomeTemp#changeBiomeTemperatures()
				/*
				try {
					Field f = Biome.class.getDeclaredField("temperature");
					f.setAccessible(true);
					f.set(player.world.getBiome(player.getPosition()), 0.8f);
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
			}
		}
	}
}
