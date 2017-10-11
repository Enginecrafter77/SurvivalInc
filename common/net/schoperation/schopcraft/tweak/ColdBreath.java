package net.schoperation.schopcraft.tweak;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.schoperation.schopcraft.cap.ghost.GhostProvider;
import net.schoperation.schopcraft.cap.ghost.IGhost;
import net.schoperation.schopcraft.util.SchopServerParticles;

/*
 * Used to spawn cool breath particles if in a cold biome.
 */
public class ColdBreath {
	
	// breathTimer variable to make the breath not constant
	private static int breathTimer = 0;
	
	public static void incrementTimer(EntityPlayer player) {
		
		breathTimer++;
		
		if (breathTimer > 100) {
			
			spawnParticles(player);
			breathTimer = 0;
		}
	}
	
	// Spawn the actual particles
	private static void spawnParticles(EntityPlayer player) {
		
		// Capability
		IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
		
		// Player position
		BlockPos pos = player.getPosition();
		
		// Biome
		Biome biome = player.world.getBiome(player.getPosition());
		
		// Biome temperature
		float biomeTemp = biome.getFloatTemperature(player.getPosition());
		
		// Cold breath particles when the player is in a cold biome.
		if (biomeTemp < 0.2 && !ghost.isGhost()) {
			
			SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "ColdBreathParticles", pos.getX()+player.getLookVec().x, pos.getY()+1.5, pos.getZ()+player.getLookVec().z);
		}
	}
}
