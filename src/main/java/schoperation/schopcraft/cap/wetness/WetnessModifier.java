package schoperation.schopcraft.cap.wetness;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import schoperation.schopcraft.config.SchopConfig;
import schoperation.schopcraft.util.ProximityDetect;
import schoperation.schopcraft.util.SchopServerParticles;

/*
 * This is where the magic of changing one's wetness occurs. You'll most likely be here.
 * Geez that sounds wrong. I wonder if Klei implemented their wetness mechanic with dumb jokes too.
 * This is also the first mechanic I implemented, so some old code will most likely be here.
 * The newest is temperature, so that'll look more functional.
 */

public class WetnessModifier {
	
	public void onPlayerUpdate(Entity player) {
		
		// Capability
		IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
		
		// Coordinates of player (block position and actual).
		BlockPos pos = player.getPosition();
		BlockPos posFace = new BlockPos(player.posX, player.posY+1, player.posZ);
		int playerPosX = pos.getX();
		int playerPosY = pos.getY();
		int playerPosZ = pos.getZ();
		double doublePlayerPosX = player.posX;
		double doublePlayerPosY = player.posY;
		double doublePlayerPosZ = player.posZ;
		
		// Server-side.
		if (!player.world.isRemote) {
			
			// Modifier from config
			float modifier = (float) SchopConfig.MECHANICS.wetnessScale;
			
			// Check if the player is in lava.
			if (player.isInLava()) {
				
				wetness.decrease(5f);
			}
			
			// Check if the player is in the nether.
			else if (player.dimension == -1) {
				
				wetness.decrease(0.08f * modifier);
			}
			
			// Check if the player is in water, whether that be rain or water.
			else if (player.isWet()) {
				
				// In water?
				if (player.isInWater()) {
					
					// Hey now, the player could just be in one block of water at their feet. If so, just go to 40% wetness Or somewhere around there.
					// We'll check if water is in the player's face. If so, 100%. If not, 40%.
					if (player.world.getBlockState(posFace).getBlock() != Blocks.WATER && player.world.getBlockState(posFace).getBlock() != Blocks.FLOWING_WATER) {
					
						if (wetness.getWetness() < 40) { wetness.increase(1.25f * modifier); }
					}
					
					else {
						
						wetness.increase(5f * modifier);
					}
				}
				
				// In rain?
				if (player.world.isRainingAt(posFace)) {
				
					wetness.increase(0.01f * modifier);
				}
			}
				
			// Otherwise, allow for natural drying off (very slow).
			else {
				
				// Figure out the conditions of the world, then dry off naturally accordingly.
				if (player.world.isDaytime() && player.world.canBlockSeeSky(pos)) { wetness.decrease(0.01f * modifier); }
				else { wetness.decrease(0.005f * modifier); }
			}
			
			// ==============================================================
			//                PROXIMITY DETECTION
			// ==============================================================
			
			// These if-statement blocks is for stuff that directly doesn't have to do with water bombardment.
			// Check if the player is near a fire.
			if (ProximityDetect.isBlockNextToPlayer(playerPosX, playerPosY, playerPosZ, Blocks.FIRE, player)) {
				
				// Are they in the rain? If so, the fire is less effective.
				if (player.isWet()) { wetness.decrease(0.25f * modifier); }
				else { wetness.decrease(0.5f * modifier); }
			}
			
			// Check if the player is near a fire - two blocks away. if there's a block between the player and the fire, it won't count.
			else if (ProximityDetect.isBlockNearPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.FIRE, player, false)) {
				
				// Are they in the rain? If so, the fire is less effective.
				if (player.isWet()) { wetness.decrease(0.15f * modifier); }
				else { wetness.decrease(0.25f * modifier); }
			}
			
			// Check if the fire is below the player ...one block.
			else if (ProximityDetect.isBlockUnderPlayer(playerPosX, playerPosY, playerPosZ, Blocks.FIRE, player)) {
				
				// Are they in the rain? If so, the fire is less effective.
				if (player.isWet()) { wetness.decrease(0.20f * modifier); }
				else { wetness.decrease(0.30f * modifier); }
			}
			
			// ...And two blocks.
			else if (ProximityDetect.isBlockUnderPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.FIRE, player, false)) {
				
				// Are they in the rain? If so, the fire is less effective.
				if (player.isWet()) { wetness.decrease(0.10f * modifier); }
				else { wetness.decrease(0.20f * modifier); }
			}
			
			// Check if the fire is at the player's face... one block.
			else if (ProximityDetect.isBlockAtPlayerFace(playerPosX, playerPosY, playerPosZ, Blocks.FIRE, player)) {
				
				// Are they in the rain? If so, the fire is less effective.
				if (player.isWet()) { wetness.decrease(0.25f * modifier); }
				else { wetness.decrease(0.5f * modifier); }
			}
		
			// ...And two blocks.
			else if (ProximityDetect.isBlockAtPlayerFace2(playerPosX, playerPosY, playerPosZ, Blocks.FIRE, player, false)) {
				
				if (player.isWet()) { wetness.decrease(0.15f * modifier); }
				else { wetness.decrease(0.25f * modifier); }
			}
			
			// Check if the player is near lava.
			if (ProximityDetect.isBlockNextToPlayer(playerPosX, playerPosY, playerPosZ, Blocks.LAVA, player)) {
				
				// Are they in the rain? If so, the lava is less effective.
				if (player.isWet()) { wetness.decrease(0.5f * modifier); }
				else { wetness.decrease(1f * modifier); }
			}
			
			// Check if the player is near lava - two blocks away.
			else if (ProximityDetect.isBlockNearPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.LAVA, player, false)) {
				
				// are they in the rain? If so, the lava is less effective.
				if (player.isWet()) { wetness.decrease(0.25f * modifier); }
				else { wetness.decrease(0.45f * modifier); }
			}
			
			// Check if the lava is below the player... one block.
			else if (ProximityDetect.isBlockUnderPlayer(playerPosX, playerPosY, playerPosZ, Blocks.LAVA, player)) {
				
				// Are they in the rain? If so, the lava is less effective.
				if (player.isWet()) { wetness.decrease(0.5f * modifier); }
				else { wetness.decrease(1f * modifier); }
			}
			
			// ...And two blocks.
			else if (ProximityDetect.isBlockUnderPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.LAVA, player, false)) {
				
				// Are they in the rain? If so, the lava is less effective.
				if (player.isWet()) { wetness.decrease(0.25f * modifier); }
				else { wetness.decrease(0.45f * modifier); }
			}
			
			// FLOWING LAVA
			else if (ProximityDetect.isBlockNextToPlayer(playerPosX, playerPosY, playerPosZ, Blocks.FLOWING_LAVA, player)) {
				
				// Are they in the rain? If so, the lava is less effective.
				if (player.isWet()) { wetness.decrease(0.5f * modifier); }
				else { wetness.decrease(1f * modifier); }
			}
			
			// Check if the player is near lava - two blocks away.
			else if (ProximityDetect.isBlockNearPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.FLOWING_LAVA, player, false)) {
				
				// Are they in the rain? If so, the lava is less effective.
				if (player.isWet()) { wetness.decrease(0.25f * modifier); }
				else { wetness.decrease(0.45f * modifier); }
			}
			
			// Check if the lava is below the player... one block.
			else if (ProximityDetect.isBlockUnderPlayer(playerPosX, playerPosY, playerPosZ, Blocks.FLOWING_LAVA, player)) {
				
				// Are they in the rain? If so, the lava is less effective.
				if (player.isWet()) { wetness.decrease(0.5f * modifier); }
				else { wetness.decrease(1f * modifier); }
			}
			
			// ...And two blocks.
			else if (ProximityDetect.isBlockUnderPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.FLOWING_LAVA, player, false)) {
				
				// Are they in the rain? If so, the lava is less effective.
				if (player.isWet()) { wetness.decrease(0.25f * modifier); }
				else { wetness.decrease(0.45f * modifier); }
			}
			
			// Burning furnace proximity... only same y-level.
			if (ProximityDetect.isBlockNextToPlayer(playerPosX, playerPosY, playerPosZ, Blocks.LIT_FURNACE, player)) {
				
				// Are they in the rain? If so, the furnace is less effective.
				if (player.isWet()) { wetness.decrease(0.10f * modifier); }
				else { wetness.decrease(0.40f * modifier); }
			}
			
			// Two blocks.
			else if (ProximityDetect.isBlockNearPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.LIT_FURNACE, player, false)) {
				
				// Are they in the rain? If so, the furnace is less effective.
				if (player.isWet()) { wetness.decrease(0.05f * modifier); }
				else { wetness.decrease(0.20f * modifier); }
			}
			
			// Magma block proximity... only one y-level under.
			if (ProximityDetect.isBlockUnderPlayer(playerPosX, playerPosY, playerPosZ, Blocks.MAGMA, player)) {
				
				// Are they in the rain? If so, the magma block is less effective.
				if (player.isWet()) { wetness.decrease(0.10f * modifier); }
				else { wetness.decrease(0.40f * modifier); }
			}
			
			// Two blocks.
			else if (ProximityDetect.isBlockUnderPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.MAGMA, player, false)) {
				
				// Are they in the rain? If so, the magma block is less effective.
				if (player.isWet()) { wetness.decrease(0.05f * modifier); }
				else { wetness.decrease(0.20f * modifier); }
			}
			
			// Summon wetness particles. 
			SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "WetnessParticles", doublePlayerPosX, doublePlayerPosY, doublePlayerPosZ);
		}
	}
}