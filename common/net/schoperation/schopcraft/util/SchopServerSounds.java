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
	 */
	
	// methodpicker variable to choose sound
	private static double newPosX = 0;
	private static double newPosY = 0;
	private static double newPosZ = 0;
	private static int soundMethodPicker = -1;
	
	// called to change sounds to be played.
	public static void changeSoundMethod(double posX, double posY, double posZ, int methodPicker) {
		
		newPosX = posX;
		newPosY = posY;
		newPosZ = posZ;
		soundMethodPicker = methodPicker;
	}
	
	@SubscribeEvent
	public void playSounds(LivingUpdateEvent event) {
		
		if (event.getEntity() instanceof EntityPlayerMP) {
			
			// basic variables
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			int playerCount = server.getCurrentPlayerCount();
			String[] playerlist = server.getOnlinePlayerNames();
			
			// iterate through each player on the server. There's probably an even easier way. If this is considered easy.
			for (int num = 0; num < playerCount; num++) {
				
				// the player instance
				EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(playerlist[num]);
				
				// the player's coordinates (if needed)
				double playerPosX = player.posX;
				double playerPosY = player.posY;
				double playerPosZ = player.posZ;
				
				if (newPosX == 0 && newPosY == 0 && newPosZ == 0) {
					
					newPosX = playerPosX;
					newPosY = playerPosY;
					newPosZ = playerPosZ;
				}

				// actual position of sound
				BlockPos pos = new BlockPos(newPosX, newPosY, newPosZ);
				
				// play sounds if player is eligible
				// water splash sounds if player drinks water
				playWaterSound(player, pos);
				
				// change stuff back
				if (soundMethodPicker != -1) {
					
					changeSoundMethod(0, 0, 0, -1);
				}	
			}	
		}	
	}
	
	// plays cute splash sound when a player drinks water from a water source
	private static void playWaterSound(Entity player, BlockPos pos) {
		
		if (!player.world.isRemote && soundMethodPicker == 0) {
			
			player.world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.NEUTRAL, 0.5f, 1.5f);
		}
	}
}
