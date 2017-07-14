package net.schoperation.schopcraft.cap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
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
	
	// Below is a wakeUpTimer variable used to delay the execution of SanityModifier.onPlayerWakeUp(EntityPlayer player).
	private int wakeUpTimer = -1;
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event) {
		
		// only continue if it's a player.
		if (event.getEntity() instanceof EntityPlayer) {
			
			// instance of player
			EntityPlayer player = (EntityPlayer) event.getEntity();
			
			// now fire every method that should be fired here, passing the player as a parameter.
			WetnessModifier.onPlayerUpdate(player);
			ThirstModifier.onPlayerUpdate(player);
			SanityModifier.onPlayerUpdate(player);
			
			// fire this if the player is sleeping (not starting to sleep, legit sleeping)
			if (player.isPlayerFullyAsleep() && player.world.isRemote) {
				
				SanityModifier.onPlayerSleepInBed(player);
			}
			
			// fire this if onPlayerWakeUp is fired (the event). It'll keep counting up until it reaches a certain value.
			if (wakeUpTimer > 30 && player.world.isRemote) {
				
				// fire onWakeUp methods and reset wakeUpTimer
				SanityModifier.onPlayerWakeUp(player);
				wakeUpTimer = -1;
			}
			else if (wakeUpTimer > -1 && player.world.isRemote) {
				
				wakeUpTimer++;
			}
		}
	}
	
	// When a player interacts with a block (usually right clicking)
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		// instance of player
		EntityPlayer player = event.getEntityPlayer();
		
		// fire methods
		ThirstModifier.onPlayerInteract(player);
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
				
				// fire methods
				SanityModifier.onPlayerConsumeItem(player, itemUsed);
			}
		}
	}
	
	// When a player wakes up from bed
	@SubscribeEvent
	public void onPlayerWakeUp(PlayerWakeUpEvent event) {
		
		// instance of player
		EntityPlayer player = event.getEntityPlayer();
		
		// start timer
		if (wakeUpTimer == -1 && player.world.isRemote) {
			
			wakeUpTimer = 0;
		}
		// the methods are fired in onPlayerUpdate.	
	}
}
