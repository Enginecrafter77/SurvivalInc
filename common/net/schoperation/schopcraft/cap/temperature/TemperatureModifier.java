package net.schoperation.schopcraft.cap.temperature;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.schoperation.schopcraft.cap.wetness.IWetness;
import net.schoperation.schopcraft.cap.wetness.WetnessProvider;
import net.schoperation.schopcraft.packet.SchopPackets;
import net.schoperation.schopcraft.packet.TemperaturePacket;

/*
 * Where temperature is directly and indirectly modified.
 */

public class TemperatureModifier {
	
	public static void getClientChange(String uuid, float newTemperature, float newMaxTemperature, float newMinTemperature, float newTargetTemperature) {
		
		// basic server variables
		MinecraftServer serverworld = FMLCommonHandler.instance().getMinecraftServerInstance();
		int playerCount = serverworld.getCurrentPlayerCount();
		String[] playerlist = serverworld.getOnlinePlayerNames();	
		
		// loop through each player and see if the uuid matches the sent one.
		for (int num = 0; num < playerCount; num++) {
			
			EntityPlayerMP player = serverworld.getPlayerList().getPlayerByUsername(playerlist[num]);
			String playeruuid = player.getCachedUniqueIdString();
			ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
			boolean equalStrings = uuid.equals(playeruuid);
			
			if (equalStrings) {
	
				temperature.increase(newTemperature-50);
				temperature.setMax(newMaxTemperature);
				temperature.setMin(newMinTemperature);
				temperature.increaseTarget(newTargetTemperature-50);
			}
		}
	}
	
	public static void onPlayerUpdate(Entity player) {
		
		// get capabilities
		ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
		IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
		
		// server-side
		if (!player.world.isRemote) {
			
			// player coords
			double playerPosX = player.posX;
			double playerPosY = player.posY;
			double playerPosZ = player.posZ;
			
			// =============================================
			//       FIRST FACTOR: THE BIOME
			// =============================================
			// Technically, the temperature of the biome. The temperature for the plains biome is 0.8. Desert is 2.0. Cold Taiga is -0.5. Blah blah blah
			
			// instance of biome at player position
			Biome biome = player.world.getBiome(new BlockPos(playerPosX, playerPosY, playerPosZ));
			
			// biome temperature
			float biomeTemp = biome.getFloatTemperature(new BlockPos(playerPosX, playerPosY, playerPosZ));
			
			// new target temperature based on biome. This constant right here will probs change quite a bit. Perhaps with seasons. Seasons will probably be a pain. Their temps are private... why Mojang
			float newTargetTemp = 69 * biomeTemp;
					
			// set it. any other factor will either
			temperature.setTarget(newTargetTemp);
			
			// =====================================
			//          OTHER FACTORS
			// =====================================
			
			// is the player directly in the sun?
			if (player.world.isDaytime() && player.world.canBlockSeeSky(new BlockPos(playerPosX, playerPosY, playerPosZ))) {
				
				temperature.increaseTarget(10.0f);
			}
			else if (!player.world.isDaytime() && player.world.canBlockSeeSky(new BlockPos(playerPosX, playerPosY, playerPosZ))) {
				
				temperature.decreaseTarget(10.0f);
			}
			
			// is the player in the rain?
			if (player.isWet()) {
				
				temperature.decreaseTarget(15.0f);
			}
			
			// is the player wet? (wetness) (scaled)
			temperature.decreaseTarget(wetness.getWetness() * 0.33f);
			
			// more coming tomorrow
			
			// actually affect the player's temperature. Explained in greater detail at the method.
			changeRateOfTemperature((EntityPlayer) player);
			
			// send data to client for rendering
			IMessage msg = new TemperaturePacket.TemperatureMessage(player.getCachedUniqueIdString(), temperature.getTemperature(), temperature.getMaxTemperature(), temperature.getMinTemperature(), temperature.getTargetTemperature());
			SchopPackets.net.sendTo(msg, (EntityPlayerMP) player);
			
		}
	}
	
	// this is called in order to affect the rate of the player's temperature, based on the target temperature.
	// the bigger the difference between the target temp and player temp, the quicker the player temp changes towards the target temp, positive or negative.
	private static void changeRateOfTemperature(EntityPlayer player) {
		
		// server-side mate
		if (!player.world.isRemote) {
			
			// get capability
			ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
		
			// difference between target temp and player temp
			float tempDifference = temperature.getTargetTemperature() - temperature.getTemperature();
			
			// rate at which the player's temp shall change. This constant might change as well.
			float rateOfChange = tempDifference * 0.002f;
			
			// change player's temp
			temperature.increase(rateOfChange);
			System.out.println(rateOfChange);
			
			// send data to client for rendering
			IMessage msg = new TemperaturePacket.TemperatureMessage(player.getCachedUniqueIdString(), temperature.getTemperature(), temperature.getMaxTemperature(), temperature.getMinTemperature(), temperature.getTargetTemperature());
			SchopPackets.net.sendTo(msg, (EntityPlayerMP) player);
		}
	}
}
