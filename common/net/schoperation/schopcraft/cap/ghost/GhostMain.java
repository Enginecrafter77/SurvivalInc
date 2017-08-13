package net.schoperation.schopcraft.cap.ghost;

import java.util.Iterator;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;
import net.schoperation.schopcraft.cap.sanity.ISanity;
import net.schoperation.schopcraft.cap.sanity.SanityProvider;
import net.schoperation.schopcraft.cap.temperature.ITemperature;
import net.schoperation.schopcraft.cap.temperature.TemperatureProvider;
import net.schoperation.schopcraft.cap.thirst.IThirst;
import net.schoperation.schopcraft.cap.thirst.ThirstProvider;
import net.schoperation.schopcraft.cap.wetness.IWetness;
import net.schoperation.schopcraft.cap.wetness.WetnessProvider;
import net.schoperation.schopcraft.util.SchopServerEffects;
import net.schoperation.schopcraft.util.SchopServerParticles;

/*
 * On death, instead of respawning out of thin air, you'll become a ghost. You're quite limited on what you can do. You can't hold items, can't open GUIs, can't craft, etc.
 * However, you can't die (again), and you can be resurrected in a very cool way. The other stats (temperature, thirst, etc.) stay constant, so don't worry about those. 
 * Instead, there's an energy bar that appears above the health + hunger bar. It's pretty much "ghost stamina", like the stuff they say in those ghost hunting shows ("Man, I'm feeling this energy!" "Use my energy!" "HOLY SH*T!!!!")
 * You can use that energy to move around quicker, or haunt blocks. 
 */

public class GhostMain {
	
	// Mark the player as a ghost upon death.
	public static void onPlayerRespawn(EntityPlayer player) {
		
		// Ghost capability
		IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
		
		// Make them a ghost
		ghost.setGhost();
		
		// Put them in adventure mode.
		player.setGameType(GameType.ADVENTURE);
	}
	
	public static void onPlayerUpdate(EntityPlayer player) {
		
		// Capabilities
		IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
		IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
		ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
		ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
		IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
		
		// Cached UUID
		String uuid = player.getCachedUniqueIdString();
		
		// Server side & while they're a ghost
		if (!player.world.isRemote && ghost.isGhost()) {
			
			// Constantly set the other values to default. Ghosts don't worry about that crap.
			wetness.set(0.0f);
			thirst.set(100.0f);
			sanity.set(100.0f);
			temperature.set(68.0f);
			
			// Give ghosts invisibility and invincibility.
			SchopServerEffects.affectPlayer(uuid, "invisibility", 40, 0, false, false);
			SchopServerEffects.affectPlayer(uuid, "resistance", 40, 4, false, false);
			
			// ==========================================
			//  ENERGY (Measured in Ghastly Plasmic Units)
			// ==========================================
			
			// Increases at night
			if (!player.world.isDaytime()) {
				
				ghost.increaseEnergy(0.05f);
			}
			
			// Decreases while sprinting. If not enough energy, can't sprint.
			if (player.isSprinting()) {
				
				ghost.decreaseEnergy(0.2f);	
			}
			
			// Disable sprinting below 15, and re-enable it above 30.
			if (ghost.getEnergy() < 15.0f) {
					
				player.getFoodStats().setFoodLevel(6);
			}
			
			else if (ghost.getEnergy() > 30.0f) {
				
				player.getFoodStats().setFoodLevel(20);
			}
			
			// Become visible (with particles)
			if (ghost.getEnergy() >= 90.0f) {
				
				SchopServerParticles.summonParticle(uuid, "GhostParticles", player.posX, player.posY, player.posZ);
			}
			
			// Attribute modifier stuff for speed. More energy = more speed
			// Does the player have existing attributes with the same name? Remove them.
			// Iterate through all of modifiers. If one of them is a ghost one, delete it so another one can take its place.
			Iterator<AttributeModifier> speedModifiers = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifiers().iterator();
			
			// Speed
			while (speedModifiers.hasNext()) {
				
				AttributeModifier element = speedModifiers.next();
				
				if (element.getName().equals("ghostSpeedBuff")) {
					
					player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(element);
				}
			}
			
			// Scale the speed modifier based on current energy.
			double speedBuffAmount = (ghost.getEnergy() - 50) * 0.002;
			
			// The modifier itself.
			AttributeModifier speedBuff = new AttributeModifier("ghostSpeedBuff", speedBuffAmount, 0);
			
			// Apply the modifier.
			if (ghost.getEnergy() > 50.0f) {
				
				player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(speedBuff);
			}
			
			// temporary resurrection method for debugging
			if (player.world.isDaytime()) {
				
				// Basic stuff
				ghost.setAlive();
				player.setGameType(GameType.SURVIVAL);
				player.getFoodStats().setFoodLevel(10);
				ghost.setEnergy(0.0f);
				
				// Reset stats
				temperature.set(50.0f);
				thirst.set(75.0f);
				sanity.set(60.0f);
				wetness.set(0.0f);
			}
		}
	}
}
