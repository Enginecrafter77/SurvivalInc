package net.schoperation.schopcraft.cap;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.schoperation.schopcraft.cap.ghost.GhostMain;
import net.schoperation.schopcraft.cap.ghost.GhostProvider;
import net.schoperation.schopcraft.cap.ghost.IGhost;
import net.schoperation.schopcraft.cap.sanity.ISanity;
import net.schoperation.schopcraft.cap.sanity.SanityModifier;
import net.schoperation.schopcraft.cap.sanity.SanityProvider;
import net.schoperation.schopcraft.cap.temperature.ITemperature;
import net.schoperation.schopcraft.cap.temperature.TemperatureModifier;
import net.schoperation.schopcraft.cap.temperature.TemperatureProvider;
import net.schoperation.schopcraft.cap.thirst.IThirst;
import net.schoperation.schopcraft.cap.thirst.ThirstModifier;
import net.schoperation.schopcraft.cap.thirst.ThirstProvider;
import net.schoperation.schopcraft.cap.wetness.IWetness;
import net.schoperation.schopcraft.cap.wetness.WetnessModifier;
import net.schoperation.schopcraft.cap.wetness.WetnessProvider;
import net.schoperation.schopcraft.packet.GhostPacket;
import net.schoperation.schopcraft.packet.SanityPacket;
import net.schoperation.schopcraft.packet.SchopPackets;
import net.schoperation.schopcraft.packet.TemperaturePacket;
import net.schoperation.schopcraft.packet.ThirstPacket;
import net.schoperation.schopcraft.packet.WetnessPacket;

/*
 * This is the event handler regarding capabilities and changes to individual stats.
 * Most of the actual code is stored in the modifier classes of each stat, and fired here.
 */
@Mod.EventBusSubscriber
public class CapEvents {
	
	// When a player logs on, give them their stats stored on the server.
	@SubscribeEvent
	public void onPlayerLogsIn(PlayerLoggedInEvent event) {
		
		EntityPlayer player = event.player;
		
		if (player instanceof EntityPlayerMP) {
			
			// Cached UUID
			String uuid = player.getCachedUniqueIdString();
			
			// Wetness packet
			IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
			IMessage msgWetness = new WetnessPacket.WetnessMessage(uuid, wetness.getWetness(), wetness.getMaxWetness(), wetness.getMinWetness());
			SchopPackets.net.sendTo(msgWetness, (EntityPlayerMP) player);
			
			// Thirst packet
			IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
			IMessage msgThirst = new ThirstPacket.ThirstMessage(uuid, thirst.getThirst(), thirst.getMaxThirst(), thirst.getMinThirst());
			SchopPackets.net.sendTo(msgThirst, (EntityPlayerMP) player);
			
			// Sanity packet
			ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
			IMessage msgSanity = new SanityPacket.SanityMessage(uuid, sanity.getSanity(), sanity.getMaxSanity(), sanity.getMinSanity());
			SchopPackets.net.sendTo(msgSanity, (EntityPlayerMP) player);
			
			// Temperature packet
			ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
			IMessage msgTemperature = new TemperaturePacket.TemperatureMessage(uuid, temperature.getTemperature(), temperature.getMaxTemperature(), temperature.getMinTemperature(), temperature.getTargetTemperature());
			SchopPackets.net.sendTo(msgTemperature, (EntityPlayerMP) player);
			
			// Ghost stats packet
			IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
			IMessage msgGhost = new GhostPacket.GhostMessage(uuid, ghost.isGhost(), ghost.getEnergy());
			SchopPackets.net.sendTo(msgGhost, (EntityPlayerMP) player);
		}
	}
	
	// When an entity is updated. So, all the time.
	// This also deals with packets to the client. The modifiers themselves can send packets to the server if they need to.
	
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
			TemperatureModifier.onPlayerUpdate(player);
			GhostMain.onPlayerUpdate(player);
			
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
			
			// send capability data to clients for rendering
			if (!player.world.isRemote) {
					
				// Wetness packet
				IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
				IMessage msgWetness = new WetnessPacket.WetnessMessage(player.getCachedUniqueIdString(), wetness.getWetness(), wetness.getMaxWetness(), wetness.getMinWetness());
				SchopPackets.net.sendTo(msgWetness, (EntityPlayerMP) player);
				
				// Thirst packet
				IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
				IMessage msgThirst = new ThirstPacket.ThirstMessage(player.getCachedUniqueIdString(), thirst.getThirst(), thirst.getMaxThirst(), thirst.getMinThirst());
				SchopPackets.net.sendTo(msgThirst, (EntityPlayerMP) player);
				
				// Sanity packet
				ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
				IMessage msgSanity = new SanityPacket.SanityMessage(player.getCachedUniqueIdString(), sanity.getSanity(), sanity.getMaxSanity(), sanity.getMinSanity());
				SchopPackets.net.sendTo(msgSanity, (EntityPlayerMP) player);
				
				// Temperature packet
				ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
				IMessage msgTemperature = new TemperaturePacket.TemperatureMessage(player.getCachedUniqueIdString(), temperature.getTemperature(), temperature.getMaxTemperature(), temperature.getMinTemperature(), temperature.getTargetTemperature());
				SchopPackets.net.sendTo(msgTemperature, (EntityPlayerMP) player);
				
				// Ghost stats packet
				IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
				IMessage msgGhost = new GhostPacket.GhostMessage(player.getCachedUniqueIdString(), ghost.isGhost(), ghost.getEnergy());
				SchopPackets.net.sendTo(msgGhost, (EntityPlayerMP) player);
			}
		}
	}
	
	// When a player interacts with a block (usually right clicking)
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		// instance of player
		EntityPlayer player = event.getEntityPlayer();
		
		// Cancel interacting with blocks if the player is a ghost. This must be done here.
		// Capability
		IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
		
		// Cancel if ghost
		if (ghost.isGhost() && event.isCancelable()) {
			
			event.setCanceled(true);
		}
		
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
				TemperatureModifier.onPlayerConsumeItem(player, itemUsed);
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
	
	// When an entity's drops are dropped (so usually when one dies)
	@SubscribeEvent
	public void onDropsDropped(LivingDropsEvent event) {
		
		// the entity that was killed
		Entity entityKilled = event.getEntity();
		
		// their drops
		List<EntityItem> drops = event.getDrops();
		
		// the looting level of the weapon
		int lootingLevel = event.getLootingLevel();
		
		// source of damage
		DamageSource damageSource = event.getSource();
		
		// fire methods
		SanityModifier.onDropsDropped(entityKilled, drops, lootingLevel, damageSource);
	}
	
	// When a player respawns.
	@SubscribeEvent
	public void onRespawn(PlayerRespawnEvent event) {
		
		// Instance of player
		EntityPlayer player = event.player;
		
		// Going through the end portal back to the overworld counts as respawning. This shouldn't make you a ghost.
		if (!event.isEndConquered()) {
			
			GhostMain.onPlayerRespawn(player);
		}
		
		else {
			
			// Set no gravity
			player.setNoGravity(true);
			
			// Move player away from portal
			player.setLocationAndAngles(player.posX+3, player.posY+1, player.posZ+3, 0.0f, 0.0f);
			
			// Set gravity
			player.setNoGravity(false);
		}
	}
	
	// When a player attempts to pick up items
	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event) {
		
		// Cancel picking up the items if the player is a ghost. This has to be done here.
		// Instance of player
		EntityPlayer player = event.getEntityPlayer();
		
		// Capability
		IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
		
		// Cancel if ghost
		if (ghost.isGhost()) {
			
			event.setCanceled(true);
		}
	}
}
