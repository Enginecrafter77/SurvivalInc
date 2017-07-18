package net.schoperation.schopcraft.cap.thirst;

import java.util.Iterator;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeBeach;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.biome.BiomeSwamp;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.schoperation.schopcraft.cap.temperature.ITemperature;
import net.schoperation.schopcraft.cap.temperature.TemperatureProvider;
import net.schoperation.schopcraft.lib.ModDamageSources;
import net.schoperation.schopcraft.packet.PotionEffectPacket;
import net.schoperation.schopcraft.packet.SchopPackets;
import net.schoperation.schopcraft.packet.SummonInfoPacket;
import net.schoperation.schopcraft.packet.ThirstPacket;
import net.schoperation.schopcraft.util.SchopServerEffects;

/*
 * Where thirst is modified.
 */

public class ThirstModifier {
	
	// This allows the client to tell the server of any changes to the player's thirst that the server can't detect.
	public static void getClientChange(String uuid, float newThirst, float newMaxThirst, float newMinThirst) {
	
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
	
				thirst.increase(newThirst-10);
				thirst.setMax(newMaxThirst);
				thirst.setMin(newMinThirst);
			}
		}
	}
	
	public static void onPlayerUpdate(Entity player) {
		
		// get capabilities
		IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
		ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
		
		// sizzlin' server side stuff (crappy attempt at a tongue twister there)
		if (!player.world.isRemote) {
			
			// lava fries you well. This might be removed someday.
			if (player.isInLava()) {
				
				thirst.decrease(0.5f);
			}
			
			// the nether is also good at frying.
			else if (player.dimension == -1) {
				
				thirst.decrease(0.006f);
			}
			
			// overheating dehydrates very well.
			else if (temperature.getTemperature() > 90.0f) {
				
				float amountOfDehydration = temperature.getTemperature() / 10000;
				thirst.decrease(amountOfDehydration);
			}
			
			// natural dehydration. "Slow" is an understatement here.
			else {
				
				thirst.decrease(0.003f);
			}
			
			// side effects of dehydration include fatigue and dizzyness. Those are replicated here. Well, attempted.
			if (thirst.getThirst() < 4.0f) {
				
				player.attackEntityFrom(ModDamageSources.DEHYDRATION, 1.0f);
			}
			if (thirst.getThirst() < 15.0f) {
				
				SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "nausea", 100, 5, false, false);
			}
			if (thirst.getThirst() < 25.0f) {
				
				SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "weakness", 20, 1, false, false);
				SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "slowness", 20, 0, false, false);
			}
			if (thirst.getThirst() < 30.0f) {
				
				SchopServerEffects.affectPlayer(player.getCachedUniqueIdString(), "mining_fatigue", 20, 1, false, false);
			}
			
			// send thirst packet to client to render correctly.
			IMessage msg = new ThirstPacket.ThirstMessage(player.getCachedUniqueIdString(), thirst.getThirst(), thirst.getMaxThirst(), thirst.getMinThirst());
			SchopPackets.net.sendTo(msg, (EntityPlayerMP) player);
		}
	}
	
	public static void onPlayerInteract(EntityPlayer player) {
		
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
						
						// still more if statements. now see what biome the player is in, and quench thirst accordingly.
						Biome biome = player.world.getBiome(pos);
						
						if (biome instanceof BiomeOcean || biome instanceof BiomeBeach) {
							
							thirst.decrease(0.5f);
						}
						else if (biome instanceof BiomeSwamp) {
							
							thirst.increase(0.25f);
							
							// damage player for drinking dirty water
							IMessage potionMsg = new PotionEffectPacket.PotionEffectMessage(player.getCachedUniqueIdString(), "poison", 12, 3, false, false);
							SchopPackets.net.sendToServer(potionMsg);
						}
						else {
							
							thirst.increase(0.25f);
							
							// random chance to damage player
							double randomNum = Math.random();
							if (randomNum <= 0.50) { // 50% chance
								
								IMessage potionMsg = new PotionEffectPacket.PotionEffectMessage(player.getCachedUniqueIdString(), "poison", 12, 1, false, false);
								SchopPackets.net.sendToServer(potionMsg);
							}
						}
												
						// spawn particles and sounds for drinking water
						IMessage msgStuff = new SummonInfoPacket.SummonInfoMessage(player.getCachedUniqueIdString(), "WaterSound", "DrinkWaterParticles", pos.getX(), pos.getY(), pos.getZ());
						SchopPackets.net.sendToServer(msgStuff);
					}		
				}
			}
			
			// send thirst packet to server
			IMessage msg = new ThirstPacket.ThirstMessage(player.getCachedUniqueIdString(), thirst.getThirst(), thirst.getMaxThirst(), thirst.getMinThirst());
			SchopPackets.net.sendToServer(msg);	
		}
	}
}
