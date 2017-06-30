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
}
