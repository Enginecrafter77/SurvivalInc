package net.schoperation.schopcraft.cap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.schoperation.schopcraft.cap.sanity.ISanity;
import net.schoperation.schopcraft.cap.sanity.SanityModifier;
import net.schoperation.schopcraft.cap.sanity.SanityProvider;
import net.schoperation.schopcraft.cap.thirst.IThirst;
import net.schoperation.schopcraft.cap.thirst.ThirstModifier;
import net.schoperation.schopcraft.cap.thirst.ThirstProvider;
import net.schoperation.schopcraft.cap.wetness.IWetness;
import net.schoperation.schopcraft.cap.wetness.WetnessModifier;
import net.schoperation.schopcraft.cap.wetness.WetnessProvider;
import net.schoperation.schopcraft.packet.SanityPacket;
import net.schoperation.schopcraft.packet.SchopPackets;
import net.schoperation.schopcraft.packet.ThirstPacket;
import net.schoperation.schopcraft.packet.WetnessPacket;

/*
 * This is the event handler regarding capabilities and changes to individual stats.
 * Most of the actual code is stored in the modifier classes of each stat, and fired here.
 */

public class CapEvents {
	
	// When a player logs on, give them their stats stored on the server.
	@SubscribeEvent
	public void onPlayerLogsIn(PlayerLoggedInEvent event) {
		
		EntityPlayer player = event.player;
		
		if (player instanceof EntityPlayerMP) {
			
			// send wetness packet to client
			IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
			IMessage msgWetness = new WetnessPacket.WetnessMessage(player.getCachedUniqueIdString(), wetness.getWetness(), wetness.getMaxWetness(), wetness.getMinWetness());
			SchopPackets.net.sendTo(msgWetness, (EntityPlayerMP) player);
			
			// send thirst packet to client
			IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
			IMessage msgThirst = new ThirstPacket.ThirstMessage(player.getCachedUniqueIdString(), thirst.getThirst(), thirst.getMaxThirst(), thirst.getMinThirst());
			SchopPackets.net.sendTo(msgThirst, (EntityPlayerMP) player);
			
			// send sanity packet to client
			ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
			IMessage msgSanity = new SanityPacket.SanityMessage(player.getCachedUniqueIdString(), sanity.getSanity(), sanity.getMaxSanity(), sanity.getMinSanity());
			SchopPackets.net.sendTo(msgSanity, (EntityPlayerMP) player);
			
		}
	}
	
	// When an entity is updated. So, all the time. Or should this be a tickhandler event thingy? We'll find out soon.
	// the modifier classes are responsible for sending and dealing with packets here.
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event) {
		
		// only continue if it's a player.
		if (event.getEntity() instanceof EntityPlayer) {
			
			// instance of player
			Entity player = event.getEntity();
			
			// now fire every method that should be fired here, passing the player as a parameter.
			WetnessModifier.onPlayerUpdate(player);
			ThirstModifier.onPlayerUpdate(player);
			SanityModifier.onPlayerUpdate(player);
		}
	}
	
	// When a player interacts with a block (usually right clicking)
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (event.getEntity() instanceof EntityPlayer) {
			
			// instance of player
			EntityPlayer player = (EntityPlayer) event.getEntity();
			
			// fire methods
			ThirstModifier.onPlayerInteract(player);
		}
	}
	
	// When a player (kinda) finishes using an item.
	@SubscribeEvent
	public void onPlayerUseItem(LivingEntityUseItemEvent.Tick event) {

		if (event.getEntity() instanceof EntityPlayer) {
			 
			// instance of player
			EntityPlayer player = (EntityPlayer) event.getEntity();
			
			if (!player.world.isRemote && event.getDuration() == 1) {
				
				// item instance
				ItemStack itemUsed = event.getItem();
				
				// fire method
				SanityModifier.onPlayerConsumeItem(player, itemUsed);
				
			}
		}
	}
}
