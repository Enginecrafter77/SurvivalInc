package net.schoperation.schopcraft.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.schoperation.schopcraft.cap.wetness.IWetness;
import net.schoperation.schopcraft.cap.wetness.WetnessProvider;

public class SchopServerParticles {
	
	/*
	 * Responsible for spawning particles for various crap... server-side. So everyone can see them.
	 * Client-side is another file, used for sanity, so players will think they're insane when they are. Efficient.
	 */
	
	// Main method to summon particles
	public static void summonParticle(String uuid, String particleMethod, double posX, double posY, double posZ) {
		
		// basic variables
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		int playerCount = server.getCurrentPlayerCount();
		String[] playerList = server.getOnlinePlayerNames();
		
		// iterate through each player on the server
		for (int num = 0; num < playerCount; num++) {
			
			// instance of the player
			EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(playerList[num]);
			
			// the dimension/world the player is in
			WorldServer serverWorld = server.getWorld(player.dimension);
			
			// any capabilities the player has (if needed here)
			IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
			
			// is this the correct player?
			if (player.getCachedUniqueIdString().equals(uuid)) {
				
				// determine what particles need to be summoned/spawned/rendered/i used a million ways to describe that process of making particles appear
				if (particleMethod.equals("WetnessParticles")) { spawnWetnessParticles(serverWorld, posX, posY, posZ, wetness.getWetness()); }
				if (particleMethod.equals("DrinkWaterParticles")) { spawnDrinkWaterParticles(serverWorld, posX, posY, posZ); }
				
			}
		}
	}
	
	// Spawn wetness particles if a player is wet enough. haha very funny joke.
	private static void spawnWetnessParticles(WorldServer serverWorld, double posX, double posY, double posZ, float wetness) {
		
		if(!serverWorld.isRemote) {
		
			int wetnessRounded = Math.round(wetness) / 10;
			if (wetness >= 10.0f) {
				serverWorld.spawnParticle(EnumParticleTypes.DRIP_WATER, posX, posY, posZ, wetnessRounded, 0.3d, 0.5d, 0.3d, 10.0d, null);
			}
		}
	}
	
	// Spawn water particles if a player drinks from a water block directly with their bare hands.
	private static void spawnDrinkWaterParticles(WorldServer serverWorld, double posX, double posY, double posZ) {
		
		if(!serverWorld.isRemote && posX != 0.0d && posY != 0.0d && posZ != 0.0d) {
			
			double randOffset = Math.random();
			if (randOffset > 0.5) { randOffset -= 0.5; }
			int rounded = (int) Math.round(randOffset);
			if (rounded == 1) { randOffset = randOffset * -1; }
			
			serverWorld.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX+randOffset, posY, posZ+randOffset, 20, 0.5d, 1d, 0.5d, 0.1d, null);
			serverWorld.spawnParticle(EnumParticleTypes.WATER_SPLASH, posX+randOffset, posY+1, posZ+randOffset, 50, 0.2d, 0.5d, 0.2d, 0.05d, null);
		}
	}
}
