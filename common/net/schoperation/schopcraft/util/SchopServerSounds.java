package net.schoperation.schopcraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SchopServerSounds {
	
	/*
	 * Responsible for playing sounds server-side, so everyone hears them. Client-side is a different file.
	 * Only call playSound on the server, if you can't figure that out.
	 */
	
	// Main method to play sounds
	public static void playSound(String uuid, String soundMethod, double posX, double posY, double posZ) {
		
		// basic variables
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		int playerCount = server.getCurrentPlayerCount();
		String[] playerList = server.getOnlinePlayerNames();
		
		// iterate through each player on the server
		for (int num = 0; num < playerCount; num++) {
			
			// instance of the player
			EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(playerList[num]);
			
			// position of the sound
			BlockPos pos = new BlockPos(posX, posY, posZ);
			
			// is this the right player? If not, go to the next player
			if (player.getCachedUniqueIdString().equals(uuid)) {
				
				// now determine what sound needs to be played.
				if (soundMethod.equals("WaterSound")) { playWaterSound(player, pos); }
				
			}
		}
	}
	
	// plays cute splash sound when a player drinks water from a water source
	private static void playWaterSound(Entity player, BlockPos pos) {
		
		if (!player.world.isRemote) {
			
			player.world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.NEUTRAL, 0.5f, 1.5f);
		}
	}
}
