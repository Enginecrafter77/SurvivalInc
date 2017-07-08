package net.schoperation.schopcraft.cap.wetness;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.schoperation.schopcraft.packet.SchopPackets;
import net.schoperation.schopcraft.packet.WetnessPacket;
import net.schoperation.schopcraft.util.ProximityDetect;
import net.schoperation.schopcraft.util.SchopServerParticles;

/*
 * This is where the magic of changing one's wetness occurs. You'll most likely be here.
 * Geez that sounds wrong. I wonder if Klei implemented their wetness mechanic with dumb jokes too.
 */

public class WetnessModifier {
	
	
	public static void getClientChange(String uuid, float value) {
		
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

				wetness.increase(value-10);
			}
		}
	}
	
	public static void onPlayerUpdate(Entity player) {
		
		// get capability
		IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
		
		// get coords of player
		int playerPosX = (int) player.posX;
		int playerPosY = (int) player.posY;
		int playerPosZ = (int) player.posZ;
		double doublePlayerPosX = player.posX;
		double doublePlayerPosY = player.posY;
		double doublePlayerPosZ = player.posZ;
		
		// getting blocks is client-side?? should be fine.
		if (player.world.isRemote) {
			
			wetness.set(10f);
			// these if-statement blocks is for stuff that directly doesn't have to do with water bombardment.
			// check if the player is near a fire
			if (ProximityDetect.isBlockNextToPlayer(playerPosX-1, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:fire"))) {
				
				// are they in the rain? If so, the fire is less effective
				if (player.isWet()) {
					wetness.decrease(0.25f);
					//if (wetness.getWetness() < 20.0f) { wetness.increase(0.25f); }
				}
				else {
					wetness.decrease(0.5f);
				}
			}
			// check if the player is near a fire - two blocks away. if there's a block between the player and the fire, it won't count.
			else if (ProximityDetect.isBlockNearPlayer2(playerPosX-1, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:fire"), false)) {
				
				// are they in the rain? If so, the fire is less effective
				if (player.isWet()) {
					wetness.decrease(0.15f);
					//if (wetness.getWetness() < 30.0f) { wetness.increase(0.15f); }
				}
				else {
					wetness.decrease(0.25f);
				}
			}
			// check if the fire is below the player somehow... one block
			else if (ProximityDetect.isBlockUnderPlayer(playerPosX-1, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:fire"))) {
				
				// are they in the rain? If so, the fire is less effective
				if (player.isWet()) {
					wetness.decrease(0.20f);
					//if (wetness.getWetness() < 20.0f) { wetness.increase(0.20f); }
				}
				else {
					wetness.decrease(0.30f);
				}
			}
			// ...and two blocks
			else if (ProximityDetect.isBlockUnderPlayer2(playerPosX-1, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:fire"), false)) {
				
				// are they in the rain? If so, the fire is less effective
				if (player.isWet()) {
					wetness.decrease(0.10f);
					//if (wetness.getWetness() < 35.0f) { wetness.increase(0.10f); }
				}
				else {
					wetness.decrease(0.20f);
				}
			}
			// ==============================================================================================================================
			// check if the player is near lava
			if (ProximityDetect.isBlockNextToPlayer(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:lava"))) {
				
				// are they in the rain? If so, the lava is less effective
				if (player.isWet()) {
					wetness.decrease(0.5f);
					//if (wetness.getWetness() < 10.0f) { wetness.increase(0.5f); }
				}
				else {
					wetness.decrease(1f);
				}
			}
			// check if the player is near lava - two blocks away.
			else if (ProximityDetect.isBlockNearPlayer2(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:lava"), false)) {
				
				// are they in the rain? If so, the lava is less effective
				if (player.isWet()) {
					wetness.decrease(0.25f);
					//if (wetness.getWetness() < 30.0f) { wetness.increase(0.25f); }
				}
				else {
					wetness.decrease(0.45f);
				}
			}
			// check if the lava is below the player... one block
			else if (ProximityDetect.isBlockUnderPlayer(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:lava"))) {
				
				// are they in the rain? If so, the lava is less effective
				if (player.isWet()) {
					wetness.decrease(0.5f);
					//if (wetness.getWetness() < 10.0f) { wetness.increase(0.5f); }
				}
				else {
					wetness.decrease(1f);
				}
			}
			// ...and two blocks
			else if (ProximityDetect.isBlockUnderPlayer2(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:lava"), false)) {
				
				// are they in the rain? If so, the lava is less effective
				if (player.isWet()) {
					wetness.decrease(0.25f);
					//if (wetness.getWetness() < 30.0f) { wetness.increase(0.25f); }
				}
				else {
					wetness.decrease(0.45f);
				}
			}
			
			// =============================================================================================================================
			// burning furnace proximity... only same y-level
			if (ProximityDetect.isBlockNextToPlayer(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:lit_furnace"))) {
				
				// are they in the rain? If so, the furnace is less effective
				if (player.isWet()) {
					wetness.decrease(0.10f);
					//if (wetness.getWetness() < 50.0f) { wetness.increase(0.10f); }
				}
				else {
					wetness.decrease(0.40f);
				}
			}
			// two blocks
			else if (ProximityDetect.isBlockNearPlayer2(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:lit_furnace"), false)) {
				
				// are they in the rain? If so, the furnace is less effective
				if (player.isWet()) {
					wetness.decrease(0.05f);
					//if (wetness.getWetness() < 60.0f) { wetness.increase(0.05f); }
				}
				else {
					wetness.decrease(0.20f);
				}
			}
			
			// =============================================================================================================================
			// magma block proximity... only one y-level under
			if (ProximityDetect.isBlockUnderPlayer(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:magma"))) {
				
				// are they in the rain? If so, the magma block is less effective
				if (player.isWet()) {
					wetness.decrease(0.10f);
					//if (wetness.getWetness() < 50.0f) { wetness.increase(0.10f); }
				}
				else {
					wetness.decrease(0.40f);
				}
			}
			// two blocks
			else if (ProximityDetect.isBlockUnderPlayer2(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:magma"), false)) {
				
				// are they in the rain? If so, the magma block is less effective
				if (player.isWet()) {
					wetness.decrease(0.05f);
					//if (wetness.getWetness() < 60.0f) { wetness.increase(0.05f); }
				}
				else {
					wetness.decrease(0.20f);
				}
			}
			
			// send wetness data to server
			IMessage msg = new WetnessPacket.WetnessMessage(player.getCachedUniqueIdString(), wetness.getWetness());
			SchopPackets.net.sendToServer(msg);	
		}
		
		// only server-side. sends packets to client just to render on the gui bars.
		if (!player.world.isRemote) {
			
			// check if the player is in lava
			if (player.isInLava()) {
				
				wetness.decrease(5f);
			}
			// check if the player is in the nether
			else if (player.dimension == -1) {
				
				wetness.decrease(0.5f);
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
				if (player.world.isDaytime() && player.world.canBlockSeeSky(new BlockPos(playerPosX,playerPosY,playerPosZ))) { wetness.decrease(0.02f); }
				else { wetness.decrease(0.01f); }
				
			}
			
			// summon wetness particles. We don't need a packet for this, as it's already on the server.
			SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "WetnessParticles", doublePlayerPosX, doublePlayerPosY, doublePlayerPosZ);

			// send new wetness data to client in order to render correctly
			IMessage msg = new WetnessPacket.WetnessMessage(player.getCachedUniqueIdString(), wetness.getWetness());
			SchopPackets.net.sendTo(msg, (EntityPlayerMP)player);
		}
	}
}
