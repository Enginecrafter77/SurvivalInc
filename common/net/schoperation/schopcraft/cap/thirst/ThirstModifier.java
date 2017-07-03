package net.schoperation.schopcraft.cap.thirst;

import java.util.Iterator;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.schoperation.schopcraft.packet.SchopPackets;
import net.schoperation.schopcraft.packet.SummonInfoPacket;
import net.schoperation.schopcraft.packet.ThirstPacket;

/*
 * Where thirst is modified.
 */

public class ThirstModifier {
	
	public static void getClientChange(String uuid, float value) {
	
		// basic server variables
		MinecraftServer serverworld = FMLCommonHandler.instance().getMinecraftServerInstance();
		int playerCount = serverworld.getCurrentPlayerCount();
		String[] playerlist = serverworld.getOnlinePlayerNames();	
		
		// loop through each player and see if the uuid matches the sent one.
		for (int num = 0; num < playerCount; num++) {
			
			EntityPlayerMP player = serverworld.getPlayerList().getPlayerByUsername(playerlist[num]);
			String playeruuid = player.getCachedUniqueIdString();
			IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
			boolean equalStrings = uuid.equals(playeruuid);
			
			if (equalStrings) {
	
				thirst.increase(value-10);
			}
		}
	}
	
	public static void onPlayerUpdate(Entity player) {
		
		// get capability
		IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
		
		// client-side only crap
		if (player.world.isRemote) {
			// do nothing right now! YAY!
			
		}
		
		// sizzlin' server side stuff (crappy attempt at a tongue twister there)
		if (!player.world.isRemote) {
			
			// lava fries you well. This might be removed someday.
			if (player.isInLava()) {
				
				thirst.decrease(0.5f);
			}
			else {
				
				// natural dehydration. "Slow" is an understatement here.
				thirst.decrease(0.002f);
			}
			
			
			// send thirst packet to client to render correctly.
			IMessage msg = new ThirstPacket.ThirstMessage(player.getCachedUniqueIdString(), thirst.getThirst());
			SchopPackets.net.sendTo(msg, (EntityPlayerMP)player);
		}
	}
	
	public static void onPlayerInteract(Entity player) {
		
		// get capability
		IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
		
		// client-side crap
		if (player.world.isRemote) {
			
			thirst.set(10f);
			
			// this is for drinking water with your bare hands. Pretty ineffective.
			RayTraceResult raytrace = player.rayTrace(2, 1.0f);
			
			// if there's something
			if (raytrace != null) {
				
				// if it isn't a block (water isn't considered a block in this case).
				if (raytrace.typeOfHit == RayTraceResult.Type.MISS) {
					
					BlockPos pos = raytrace.getBlockPos();
					Iterator<ItemStack> handItems = player.getHeldEquipment().iterator();
					
					// if it is water and the player isn't holding jack squat (main hand)
					if (player.world.getBlockState(pos).getMaterial() == Material.WATER && handItems.next().isEmpty()) {
						
						thirst.increase(0.25f);
												
						// spawn particles and sounds
						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(pos.getX(), pos.getY(), pos.getZ(), 0);
						SchopPackets.net.sendToServer(msgStuff);
					}		
				}
			}
			
			// send thirst packet to server
			IMessage msg = new ThirstPacket.ThirstMessage(player.getCachedUniqueIdString(), thirst.getThirst());
			SchopPackets.net.sendToServer(msg);	
		}
	}
}
