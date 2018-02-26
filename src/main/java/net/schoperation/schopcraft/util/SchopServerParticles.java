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
	
	// Main method to summon particles.
	public static void summonParticle(String uuid, String particleMethod, double posX, double posY, double posZ) {
		
		// Basic variables.
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		int playerCount = server.getCurrentPlayerCount();
		String[] playerList = server.getOnlinePlayerNames();
		
		// Iterate through each player on the server.
		for (int num = 0; num < playerCount; num++) {
			
			// Instance of the player.
			EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(playerList[num]);
			
			// The dimension/world the player is in.
			WorldServer serverWorld = server.getWorld(player.dimension);
			
			// Any capabilities the player has (if needed here).
			IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
			
			// Is this the correct player?
			if (player.getCachedUniqueIdString().equals(uuid) && !player.world.isRemote) {
				
				// Determine what particles need to be summoned/spawned/rendered/i used a million ways to describe that process of making particles appear
				if (particleMethod.equals("WetnessParticles")) { spawnWetnessParticles(serverWorld, posX, posY, posZ, wetness.getWetness()); }
				else if (particleMethod.equals("DrinkWaterParticles")) { spawnDrinkWaterParticles(serverWorld, posX, posY, posZ); }
				else if (particleMethod.equals("SweatParticles")) { spawnSweatParticles(serverWorld, posX, posY, posZ); }
				else if (particleMethod.equals("ColdBreathParticles")) { spawnColdBreathParticles(serverWorld, posX, posY, posZ); }
				else if (particleMethod.equals("GhostParticles")) { spawnGhostParticles(serverWorld, posX, posY, posZ); }
				else if (particleMethod.equals("ResurrectionFlameParticles")) { spawnResurrectionFlameParticles(serverWorld, posX, posY, posZ); }
				else if (particleMethod.equals("ResurrectionEnchantmentParticles")) { spawnResurrectionEnchantmentParticles(serverWorld, posX, posY, posZ); }
				else if (particleMethod.equals("TowelWaterParticles")) { spawnTowelWaterParticles(serverWorld, posX, posY, posZ); }
				
			}
		}
	}
	
	// Spawn wetness particles if a player is wet enough. haha very funny joke.
	private static void spawnWetnessParticles(WorldServer serverWorld, double posX, double posY, double posZ, float wetness) {
		
		int wetnessRounded = Math.round(wetness) / 10;
		if (wetness >= 10.0f) {
			serverWorld.spawnParticle(EnumParticleTypes.DRIP_WATER, posX, posY, posZ, wetnessRounded, 0.3d, 0.5d, 0.3d, 10.0d, null);
		}
	}
	
	// Spawn water particles if a player drinks from a water block directly with their bare hands.
	private static void spawnDrinkWaterParticles(WorldServer serverWorld, double posX, double posY, double posZ) {
		
		if(posX != 0.0d && posY != 0.0d && posZ != 0.0d) {
			
			double randOffset = Math.random();
			if (randOffset > 0.5) { randOffset -= 0.5; }
			int rounded = (int) Math.round(randOffset);
			if (rounded == 1) { randOffset = randOffset * -1; }
			
			serverWorld.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX+randOffset, posY, posZ+randOffset, 20, 0.5d, 1d, 0.5d, 0.1d, null);
			serverWorld.spawnParticle(EnumParticleTypes.WATER_SPLASH, posX+randOffset, posY+1, posZ+randOffset, 50, 0.2d, 0.5d, 0.2d, 0.05d, null);
		}
	}
	
	// Spawn some sweat particles when the player is hot enough. Don't take it THAT way.
	private static void spawnSweatParticles(WorldServer serverWorld, double posX, double posY, double posZ) {
	
		serverWorld.spawnParticle(EnumParticleTypes.WATER_SPLASH, posX, posY, posZ, 1, 0.05, 0.0, 0.05, 0.25, null);
	}
	
	// Spawn some cool snow particles to imitate a person's breath, when they're in a cold biome.
	private static void spawnColdBreathParticles(WorldServer serverWorld, double posX, double posY, double posZ) {
		
		serverWorld.spawnParticle(EnumParticleTypes.CLOUD, posX, posY, posZ, 1, 0, 0.1, 0, 0.1, new int[0]);
	}
	
	// Spawn some weird ghost particles to make a ghost visible (or what those ghost hunting shows call it: manifest themselves)
	private static void spawnGhostParticles(WorldServer serverWorld, double posX, double posY, double posZ) {
		
		serverWorld.spawnParticle(EnumParticleTypes.CLOUD, posX, posY, posZ, 10, 0.25, 1.0, 0.25, 0.05, null);
	}
	
	// Spawn cool flame particles during resurrection
	private static void spawnResurrectionFlameParticles(WorldServer serverWorld, double posX, double posY, double posZ) {
			
		serverWorld.spawnParticle(EnumParticleTypes.FLAME, posX+0.5, posY+1.75, posZ+3.5, 0, 0, 0.5, -1, 0.15, null);
		serverWorld.spawnParticle(EnumParticleTypes.FLAME, posX+0.5, posY+1.75, posZ-2.5, 0, 0, 0.5, 1, 0.15, null);
		serverWorld.spawnParticle(EnumParticleTypes.FLAME, posX+3.5, posY+1.75, posZ+0.5, 0, -1, 0.5, 0, 0.15, null);
		serverWorld.spawnParticle(EnumParticleTypes.FLAME, posX-2.5, posY+1.75, posZ+0.5, 0, 1, 0.5, 0, 0.15, null);
	}
	
	// Spawn enchantment particles during resurrection
	private static void spawnResurrectionEnchantmentParticles(WorldServer serverWorld, double posX, double posY, double posZ) {
		
		serverWorld.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, posX, posY, posZ, 50, 3, 2, 3, 1, null);
	}
	
	// Spawn water drip particles from a player holding a towel.
	private static void spawnTowelWaterParticles(WorldServer serverWorld, double posX, double posY, double posZ) {
		
		serverWorld.spawnParticle(EnumParticleTypes.WATER_SPLASH, posX, posY, posZ, 0, 0.05, 0.05, 0.05, 0.05, null);
	}
}