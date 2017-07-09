package net.schoperation.schopcraft.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

/*
 * Responsible for affecting players with status effects, if the methods trying to do them are client-side.
 * This does them server-side. In case this is not obvious, ONLY CALL THIS ON THE SERVER. Unless you want a crash.
 */
public class SchopServerEffects {

	
	public static void affectPlayer(String uuid, String effect, int duration, int amplifier, boolean isAmbient, boolean showParticles) {
		
		// basic variables
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		int playerCount = server.getCurrentPlayerCount();
		String[] playerList = server.getOnlinePlayerNames();
		
		// iterate through each player
		for (int num = 0; num < playerCount; num++) {
			
			// instance of player
			EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(playerList[num]);
			
			// is this the right player? check uuids
			if (player.getCachedUniqueIdString().equals(uuid)) {
				
				// decipher potion effect string and affect the player accordingly
				// poison
				if (effect.equals("poison")) {
					
					player.addPotionEffect(new PotionEffect(MobEffects.POISON, duration, amplifier, isAmbient, showParticles));
				}
				
				// mining fatigue
				else if (effect.equals("mining_fatigue")) {
					
					player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, duration, amplifier, isAmbient, showParticles));
				}
				
				// nausea
				else if (effect.equals("nausea")) {
					
					player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration, amplifier, isAmbient, showParticles));
				}
				
				// instant damage
				else if (effect.equals("instant_damage")) {
					
					player.addPotionEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, duration, amplifier, isAmbient, showParticles));
				}
				
			}
		}		
	}
}
