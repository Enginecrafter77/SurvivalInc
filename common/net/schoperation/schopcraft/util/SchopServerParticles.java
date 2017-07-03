package net.schoperation.schopcraft.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.schoperation.schopcraft.cap.wetness.IWetness;
import net.schoperation.schopcraft.cap.wetness.WetnessProvider;

public class SchopServerParticles {
	
	/*
	 * Responsible for spawning particles for various crap... server-side. So everyone can see them.
	 * Client-side is another file, used for sanity, so players will think they're insane when they are. Efficient.
	 */
	
	// positions different from the player's.
	private static double newPosX = 0;
	private static double newPosY = 0;
	private static double newPosZ = 0;
	private static int particleMethodPicker = -1;
	
	// called when a packet from the client is send to server, in order to render particles somewhere.
	// an extra int is sent to identify which particle method to call.
	public static void changeParticlePosition(double posX, double posY, double posZ, int methodPicker) {
		
		newPosX = posX;
		newPosY = posY;
		newPosZ = posZ;
		particleMethodPicker = methodPicker;
	}
	
	@SubscribeEvent
	public void renderParticles(LivingUpdateEvent event) {
		
		if (event.getEntity() instanceof EntityPlayerMP) {
			
			// basic variables
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			int playerCount = server.getCurrentPlayerCount();
			String[] playerlist = server.getOnlinePlayerNames();
			
			// iterate through each player on the server. There's probably an even easier way. If this is considered easy.
			for (int num = 0; num < playerCount; num++) {
				
				// the player instance
				EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(playerlist[num]);
				
				// the dimension the player is in
				WorldServer serverWorld = server.getWorld(player.dimension);
				
				// the player's capabilities/stats
				IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
				
				// the player's coordinates
				double playerPosX = player.posX;
				double playerPosY = player.posY;
				double playerPosZ = player.posZ;
				
				// spawn particles if they are eligible
				// wetness particles based on their wetness (scaled particles... coolllllllllllllllll)
				spawnWetnessParticles(serverWorld, playerPosX, playerPosY, playerPosZ, wetness.getWetness());
				
				// spawn water particles if player drinks water
				spawnDrinkWaterParticles(serverWorld, newPosX, newPosY, newPosZ);
				
				// change stuff back
				if (particleMethodPicker != -1) {
					
					changeParticlePosition(0.0d, 0.0d, 0.0d, -1);
				}	
			}	
		}	
	}
	
	// Spawn wetness particles if a player is wet enough. haha very funny joke
	private void spawnWetnessParticles(WorldServer serverWorld, double playerPosX, double playerPosY, double playerPosZ, float wetness) {
		
		if(!serverWorld.isRemote) {
		
			int wetnessRounded = Math.round(wetness) / 10;
			if (wetness >= 10.0f) {
				serverWorld.spawnParticle(EnumParticleTypes.DRIP_WATER, playerPosX, playerPosY, playerPosZ, wetnessRounded, 0.3d, 0.5d, 0.3d, 10.0d, null);
			}
		}
	}
	
	// spawn water particles if a player drinks from a water block directly with their bare hands. particleMethodPicker = 0
	private void spawnDrinkWaterParticles(WorldServer serverWorld, double newPosX, double newPosY, double newPosZ) {
		
		if(!serverWorld.isRemote && newPosX != 0.0d && newPosY != 0.0d && newPosZ != 0.0d && particleMethodPicker == 0) {
			
			double randOffset = Math.random();
			if (randOffset > 0.5) { randOffset -= 0.5; }
			int rounded = (int) Math.round(randOffset);
			if (rounded == 1) { randOffset = randOffset * -1; }
			
			serverWorld.spawnParticle(EnumParticleTypes.WATER_BUBBLE, newPosX+randOffset, newPosY, newPosZ+randOffset, 20, 0.5d, 1d, 0.5d, 0.1d, null);
			serverWorld.spawnParticle(EnumParticleTypes.WATER_SPLASH, newPosX+randOffset, newPosY+1, newPosZ+randOffset, 50, 0.2d, 0.5d, 0.2d, 0.05d, null);
		}
	}
}
