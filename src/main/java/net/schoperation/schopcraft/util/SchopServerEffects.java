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

	// Main method.
	public static void affectPlayer(String uuid, String effect, int duration, int amplifier, boolean isAmbient, boolean showParticles) {
		
		// Basic variables.
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		int playerCount = server.getCurrentPlayerCount();
		String[] playerList = server.getOnlinePlayerNames();
		
		// Iterate through each player.
		for (int num = 0; num < playerCount; num++) {
			
			// Instance of player.
			EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(playerList[num]);
			
			// Is this the right player? check UUIDs.
			if (!player.isCreative() && player.getCachedUniqueIdString().equals(uuid) && !player.world.isRemote) {
				
				// Decipher potion effect string and affect the player accordingly.
				// Poison
				if (effect.equals("poison")) {
					
					player.addPotionEffect(new PotionEffect(MobEffects.POISON, duration, amplifier, isAmbient, showParticles));
				}
				
				// Mining fatigue
				else if (effect.equals("mining_fatigue")) {
					
					player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, duration, amplifier, isAmbient, showParticles));
				}
				
				// Nausea
				else if (effect.equals("nausea")) {
					
					player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration, amplifier, isAmbient, showParticles));
				}
				
				// Instant damage
				else if (effect.equals("instant_damage")) {
					
					player.addPotionEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, duration, amplifier, isAmbient, showParticles));
				}
				
				// Instant health
				else if (effect.equals("instant_health")) {
					
					player.addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, duration, amplifier, isAmbient, showParticles));
				}
				
				// Slowness
				else if (effect.equals("slowness")) {
					
					player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, duration, amplifier, isAmbient, showParticles));
				}
				
				// Weakness
				else if (effect.equals("weakness")) {
					
					player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, duration, amplifier, isAmbient, showParticles));
				}
				
				// Hunger
				else if (effect.equals("hunger")) {
					
					player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, duration, amplifier, isAmbient, showParticles));
				}
				
				// Invisibility
				else if (effect.equals("invisibility")) {
					
					player.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, duration, amplifier, isAmbient, showParticles));
				}
				
				// Resistance
				else if (effect.equals("resistance")) {
					
					player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, duration, amplifier, isAmbient, showParticles));
				}
				
				// Saturation
				else if (effect.equals("saturation")) {
					
					player.addPotionEffect(new PotionEffect(MobEffects.SATURATION, duration, amplifier, isAmbient, showParticles));
				}
			}
		}		
	}
}