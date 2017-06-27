package net.schoperation.schopcraft.cap.wetness;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.schoperation.schopcraft.packet.SchopPackets;
import net.schoperation.schopcraft.packet.StatsPacket;

/*
 * This is where the magic of changing one's wetness occurs. You'll most likely be here.
 */
public class WetnessModifier {
	
	
	@SubscribeEvent
	public void onPlayerLogsIn(PlayerLoggedInEvent event) {
		
		EntityPlayer player = event.player;
		
		if (player instanceof EntityPlayerMP) {
			
			IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
			IMessage msg = new StatsPacket.StatsMessage(wetness.getWetness());
			SchopPackets.net.sendTo(msg, (EntityPlayerMP)player);
		}
		
	}
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event) {
		
		
		// is this a player? kek
		if (event.getEntity() instanceof EntityPlayer) {
			
			Entity player = event.getEntity();
			IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
			
			
			// check if the player is in water...
			if (player.isInWater() && !player.world.isRemote) {
				
				wetness.increase(1f);
				IMessage msg = new StatsPacket.StatsMessage(wetness.getWetness());
				SchopPackets.net.sendTo(msg, (EntityPlayerMP)player);;
			}	
		}
	}
}
