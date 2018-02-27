package net.schoperation.schopcraft.cap;

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
import net.schoperation.schopcraft.config.SchopConfig;
import net.schoperation.schopcraft.packet.HUDRenderPacket;
import net.schoperation.schopcraft.packet.SchopPackets;

import java.util.List;

/*
 * This is the event handler regarding capabilities and changes to individual stats.
 * Most of the actual code is stored in the modifier classes of each stat, and fired here.
 */
@Mod.EventBusSubscriber
public class CapEvents {
	
	// Modifiers
	private final TemperatureModifier tempMod = new TemperatureModifier();
	private final ThirstModifier thirstMod = new ThirstModifier();
	private final SanityModifier sanityMod = new SanityModifier();
	private final WetnessModifier wetnessMod = new WetnessModifier();
	private final GhostMain ghostMain = new GhostMain();
	
	// When a player logs on, give them their stats stored on the server.
	@SubscribeEvent
	public void onPlayerLogsIn(PlayerLoggedInEvent event) {
		
		EntityPlayer player = event.player;
		
		if (player instanceof EntityPlayerMP) {
			
			// Capabilities
			IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
			IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
			ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
			ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
			IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
			
			// Send data to client for rendering.
			IMessage msgGui = new HUDRenderPacket.HUDRenderMessage(temperature.getTemperature(), temperature.getMaxTemperature(), thirst.getThirst(), thirst.getMaxThirst(), sanity.getSanity(), sanity.getMaxSanity(), wetness.getWetness(), wetness.getMaxWetness(), ghost.isGhost(), ghost.getEnergy());
			SchopPackets.net.sendTo(msgGui, (EntityPlayerMP) player);
		}
	}
	
	// When an entity is updated. So, all the time.
	// This also deals with packets to the client. The modifiers themselves can send packets to the server if they need to.
	// Below is a wakeUpTimer variable used to delay the execution of SanityModifier.onPlayerWakeUp(EntityPlayer player).
	private int wakeUpTimer = -1;
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event) {
		
		// Only continue if it's a player.
		if (event.getEntity() instanceof EntityPlayer) {
			
			// Instance of player.
			EntityPlayer player = (EntityPlayer) event.getEntity();
			
			// Whether the player's stats should be changed. Not in creative and spectator mode.
			boolean shouldStatsChange = true;
			
			if (player.isCreative() || player.isSpectator()) {
				
				shouldStatsChange = false;
			}
			
			// Now fire every method that should be fired here, passing the player as a parameter.
			// If in creative mode (or if the mechanic is disabled in the config), don't fire these at all.
			if (shouldStatsChange) {
				
				if (SchopConfig.MECHANICS.enableTemperature) {
					
					tempMod.onPlayerUpdate(player);
				}
				
				if (SchopConfig.MECHANICS.enableThirst) {
					
					thirstMod.onPlayerUpdate(player);
				}
				
				if (SchopConfig.MECHANICS.enableSanity) {
					
					sanityMod.onPlayerUpdate(player);
				}
				
				if (SchopConfig.MECHANICS.enableWetness) {
					
					wetnessMod.onPlayerUpdate(player);
				}
				
				if (SchopConfig.MECHANICS.enableGhost) {
					
					ghostMain.onPlayerUpdate(player);
				}
			}
			
			// Fire this if the player is sleeping (not starting to sleep, legit sleeping).
			if (player.isPlayerFullyAsleep() && player.world.isRemote && shouldStatsChange && SchopConfig.MECHANICS.enableSanity) {
				
				sanityMod.onPlayerSleepInBed(player);
			}
			
			// Fire this if onPlayerWakeUp is fired (the event). It'll keep counting up until it reaches a certain value.
			if (wakeUpTimer > 30 && player.world.isRemote && shouldStatsChange && SchopConfig.MECHANICS.enableSanity) {
				
				// Fire onWakeUp methods and reset wakeUpTimer.
				sanityMod.onPlayerWakeUp(player);
				wakeUpTimer = -1;
			}
			else if (wakeUpTimer > -1 && player.world.isRemote) {
				
				wakeUpTimer++;
			}
			
			// Send capability data to clients for rendering
			if (!player.world.isRemote) {
					
				// Capabilities.
				IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
				IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
				ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
				ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
				IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);

				// Send data to client for rendering.
				IMessage msgGui = new HUDRenderPacket.HUDRenderMessage(temperature.getTemperature(), temperature.getMaxTemperature(), thirst.getThirst(), thirst.getMaxThirst(), sanity.getSanity(), sanity.getMaxSanity(), wetness.getWetness(), wetness.getMaxWetness(), ghost.isGhost(), ghost.getEnergy());
				SchopPackets.net.sendTo(msgGui, (EntityPlayerMP) player);
			}
		}
	}
	
	// When a player interacts with a block (usually right clicking something).
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		// Instance of player.
		EntityPlayer player = event.getEntityPlayer();
		
		// Cancel interacting with blocks if the player is a ghost. This must be done here.
		IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
		
		if (ghost.isGhost() && event.isCancelable()) {
			
			event.setCanceled(true);
		}
		
		// Fire methods.
		if (SchopConfig.MECHANICS.enableThirst && !player.isCreative() && !player.isSpectator()) {
			
			thirstMod.onPlayerInteract(player);
		}
	}
	
	// When a player (kind of) finishes using an item. Technically one tick before it's actually consumed.
	@SubscribeEvent
	public void onPlayerUseItem(LivingEntityUseItemEvent.Tick event) {

		if (event.getEntity() instanceof EntityPlayer) {
			 
			// Instance of player.
			EntityPlayer player = (EntityPlayer) event.getEntity();
			
			if (!player.world.isRemote && event.getDuration() == 1) {
				
				// Instance of item.
				ItemStack itemUsed = event.getItem();
				
				// Fire methods.
				if (!player.isCreative()) {
					
					if (SchopConfig.MECHANICS.enableTemperature) {
						
						tempMod.onPlayerConsumeItem(player, itemUsed);
					}
					
					if (SchopConfig.MECHANICS.enableSanity) {
						
						sanityMod.onPlayerConsumeItem(player, itemUsed);
					}
				}
			}
		}
	}
	
	// When a player wakes up from bed.
	@SubscribeEvent
	public void onPlayerWakeUp(PlayerWakeUpEvent event) {
		
		// Instance of player.
		EntityPlayer player = event.getEntityPlayer();
		
		// Start wakeUpTimer.
		if (wakeUpTimer == -1 && player.world.isRemote) {
			
			wakeUpTimer = 0;
		}
		
		// The methods related to onPlayerWakeUp are fired in onPlayerUpdate.	
	}
	
	// When an entity's drops are dropped (so usually when one dies).
	@SubscribeEvent
	public void onDropsDropped(LivingDropsEvent event) {
		
		// The entity that was killed.
		Entity entityKilled = event.getEntity();
		
		// A list of their drops.
		List<EntityItem> drops = event.getDrops();
		
		// The looting level of the weapon.
		int lootingLevel = event.getLootingLevel();
		
		// Damage source.
		DamageSource damageSource = event.getSource();
		
		// Fire methods.
		if (SchopConfig.MECHANICS.enableSanity) {
			
			sanityMod.onDropsDropped(entityKilled, drops, lootingLevel, damageSource);
		}
	}
	
	// When a player respawns.
	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		
		// Instance of player.
		EntityPlayer player = event.player;
		
		// Going through the end portal back to the overworld counts as respawning. This shouldn't make you a ghost.
		if (!event.isEndConquered()) {
			
			ghostMain.onPlayerRespawn(player);
		}
		
		else {
			
			// Set no gravity.
			player.setNoGravity(true);
			
			// Move player away from portal.
			player.setLocationAndAngles(player.posX+3, player.posY+1, player.posZ+3, 0.0f, 0.0f);
			
			// Set gravity back.
			player.setNoGravity(false);
		}
	}
	
	// When a player attempts to pick up items.
	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event) {
		
		// Cancel picking up the items if the player is a ghost. This has to be done here.
		EntityPlayer player = event.getEntityPlayer();
		IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
		
		if (ghost.isGhost()) {
			
			event.setCanceled(true);
		}
	}
}