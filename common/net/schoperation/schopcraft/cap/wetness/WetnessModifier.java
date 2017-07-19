package net.schoperation.schopcraft.cap.wetness;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.schoperation.schopcraft.util.ProximityDetect;
import net.schoperation.schopcraft.util.SchopServerParticles;

/*
 * This is where the magic of changing one's wetness occurs. You'll most likely be here.
 * Geez that sounds wrong. I wonder if Klei implemented their wetness mechanic with dumb jokes too.
 * This is also the first mechanic I implemented, so some old code will most likely be here.
 * The newest is temperature, so that'll look more functional.
 */

public class WetnessModifier {
	
	// This allows the client to tell the server of any changes to the player's wetness that the server can't detect.
	public static void getClientChange(String uuid, float newWetness, float newMaxWetness, float newMinWetness) {
		
		// basic server variables
		MinecraftServer serverworld = FMLCommonHandler.instance().getMinecraftServerInstance();
		int playerCount = serverworld.getCurrentPlayerCount();
		String[] playerlist = serverworld.getOnlinePlayerNames();	
		
		// loop through each player and see if the uuid matches the sent one.
		for (int num = 0; num < playerCount; num++) {
			
			EntityPlayerMP player = serverworld.getPlayerList().getPlayerByUsername(playerlist[num]);
			String playeruuid = player.getCachedUniqueIdString();
			IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
			boolean equalStrings = uuid.equals(playeruuid);
			
			if (equalStrings) {

				wetness.increase(newWetness-10);
				wetness.setMax(newMaxWetness);
				wetness.setMin(newMinWetness);
			}
		}
	}
	
	public static void onPlayerUpdate(Entity player) {
		
		// get capability
		IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
		
		// get coords of player
		BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
		int playerPosX = pos.getX();
		int playerPosY = pos.getY();
		int playerPosZ = pos.getZ();
		double doublePlayerPosX = player.posX;
		double doublePlayerPosY = player.posY;
		double doublePlayerPosZ = player.posZ;
		
		// client-side. not much right now. Kept as an example just in case.
		if (player.world.isRemote) {
			
			// pre-set wetness (since this is client side. I just picked ten because it still can't be lower than zero or higher than 100.)
			//wetness.set(10f);
			
			// send wetness data to server
			//IMessage msg = new WetnessPacket.WetnessMessage(player.getCachedUniqueIdString(), wetness.getWetness(), wetness.getMaxWetness(), wetness.getMinWetness());
			//SchopPackets.net.sendToServer(msg);	
		}
		
		// server-side
		if (!player.world.isRemote) {
			
			// check if the player is in lava
			if (player.isInLava()) {
				
				wetness.decrease(5f);
			}
			// check if the player is in the nether
			else if (player.dimension == -1) {
				
				wetness.decrease(0.08f);
			}
			// check if the player is in water
			else if (player.isInWater()) {
				
				wetness.increase(5f);
			}
			// check if the player is in the rain
			else if (player.isWet()) {
				
				wetness.increase(0.05f);
			}
			
			// otherwise, allow for natural drying off (very slow)
			else {
				
				// figure out the conditions of the world, then dry off naturally accordingly
				if (player.world.isDaytime() && player.world.canBlockSeeSky(pos)) { wetness.decrease(0.02f); }
				else { wetness.decrease(0.01f); }
			}
			
			// ==============================================================
			//                PROXIMITY DETECTION
			// ==============================================================
			
			// these if-statement blocks is for stuff that directly doesn't have to do with water bombardment.
			// check if the player is near a fire
			if (ProximityDetect.isBlockNextToPlayer(playerPosX, playerPosY, playerPosZ, Blocks.FIRE, player)) {
				
				// are they in the rain? If so, the fire is less effective
				if (player.isWet()) { wetness.decrease(0.25f); }
				else { wetness.decrease(0.5f); }
			}
			
			// check if the player is near a fire - two blocks away. if there's a block between the player and the fire, it won't count.
			else if (ProximityDetect.isBlockNearPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.FIRE, player, false)) {
				
				// are they in the rain? If so, the fire is less effective
				if (player.isWet()) { wetness.decrease(0.15f); }
				else { wetness.decrease(0.25f); }
			}
			
			// check if the fire is below the player ...one block
			else if (ProximityDetect.isBlockUnderPlayer(playerPosX, playerPosY, playerPosZ, Blocks.FIRE, player)) {
				
				// are they in the rain? If so, the fire is less effective
				if (player.isWet()) { wetness.decrease(0.20f); }
				else { wetness.decrease(0.30f); }
			}
			
			// ...and two blocks
			else if (ProximityDetect.isBlockUnderPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.FIRE, player, false)) {
				
				// are they in the rain? If so, the fire is less effective
				if (player.isWet()) { wetness.decrease(0.10f); }
				else { wetness.decrease(0.20f); }
			}
			
			// ==============================================================================================================================
			// check if the player is near lava
			if (ProximityDetect.isBlockNextToPlayer(playerPosX, playerPosY, playerPosZ, Blocks.LAVA, player)) {
				
				// are they in the rain? If so, the lava is less effective
				if (player.isWet()) { wetness.decrease(0.5f); }
				else { wetness.decrease(1f); }
			}
			
			// check if the player is near lava - two blocks away.
			else if (ProximityDetect.isBlockNearPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.LAVA, player, false)) {
				
				// are they in the rain? If so, the lava is less effective
				if (player.isWet()) { wetness.decrease(0.25f); }
				else { wetness.decrease(0.45f); }
			}
			
			// check if the lava is below the player... one block
			else if (ProximityDetect.isBlockUnderPlayer(playerPosX, playerPosY, playerPosZ, Blocks.LAVA, player)) {
				
				// are they in the rain? If so, the lava is less effective
				if (player.isWet()) { wetness.decrease(0.5f); }
				else { wetness.decrease(1f); }
			}
			
			// ...and two blocks
			else if (ProximityDetect.isBlockUnderPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.LAVA, player, false)) {
				
				// are they in the rain? If so, the lava is less effective
				if (player.isWet()) { wetness.decrease(0.25f); }
				else { wetness.decrease(0.45f); }
			}
			
			// FLOWING LAVA
			else if (ProximityDetect.isBlockNextToPlayer(playerPosX, playerPosY, playerPosZ, Blocks.FLOWING_LAVA, player)) {
				
				// are they in the rain? If so, the lava is less effective
				if (player.isWet()) { wetness.decrease(0.5f); }
				else { wetness.decrease(1f); }
			}
			
			// check if the player is near lava - two blocks away.
			else if (ProximityDetect.isBlockNearPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.FLOWING_LAVA, player, false)) {
				
				// are they in the rain? If so, the lava is less effective
				if (player.isWet()) { wetness.decrease(0.25f); }
				else { wetness.decrease(0.45f); }
			}
			
			// check if the lava is below the player... one block
			else if (ProximityDetect.isBlockUnderPlayer(playerPosX, playerPosY, playerPosZ, Blocks.FLOWING_LAVA, player)) {
				
				// are they in the rain? If so, the lava is less effective
				if (player.isWet()) { wetness.decrease(0.5f); }
				else { wetness.decrease(1f); }
			}
			
			// ...and two blocks
			else if (ProximityDetect.isBlockUnderPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.FLOWING_LAVA, player, false)) {
				
				// are they in the rain? If so, the lava is less effective
				if (player.isWet()) { wetness.decrease(0.25f); }
				else { wetness.decrease(0.45f); }
			}
			
			// =============================================================================================================================
			// burning furnace proximity... only same y-level
			if (ProximityDetect.isBlockNextToPlayer(playerPosX, playerPosY, playerPosZ, Blocks.LIT_FURNACE, player)) {
				
				// are they in the rain? If so, the furnace is less effective
				if (player.isWet()) { wetness.decrease(0.10f); }
				else { wetness.decrease(0.40f); }
			}
			
			// two blocks
			else if (ProximityDetect.isBlockNearPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.LIT_FURNACE, player, false)) {
				
				// are they in the rain? If so, the furnace is less effective
				if (player.isWet()) { wetness.decrease(0.05f); }
				else { wetness.decrease(0.20f); }
			}
			
			// =============================================================================================================================
			// magma block proximity... only one y-level under
			if (ProximityDetect.isBlockUnderPlayer(playerPosX, playerPosY, playerPosZ, Blocks.MAGMA, player)) {
				
				// are they in the rain? If so, the magma block is less effective
				if (player.isWet()) { wetness.decrease(0.10f); }
				else { wetness.decrease(0.40f); }
			}
			
			// two blocks
			else if (ProximityDetect.isBlockUnderPlayer2(playerPosX, playerPosY, playerPosZ, Blocks.MAGMA, player, false)) {
				
				// are they in the rain? If so, the magma block is less effective
				if (player.isWet()) { wetness.decrease(0.05f); }
				else { wetness.decrease(0.20f); }
			}
			
			// summon wetness particles. We don't need a packet for this, as it's already on the server.
			SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "WetnessParticles", doublePlayerPosX, doublePlayerPosY, doublePlayerPosZ);
		}
	}
}
