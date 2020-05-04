package enginecrafter77.survivalinc.net;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StatSyncHandler implements IMessageHandler<StatSyncMessage, IMessage>{
	
	@Override
	public IMessage onMessage(StatSyncMessage message, MessageContext ctx)
	{
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if(player == null) SurvivalInc.logger.error("Minecraft remote player entity is null. This is NOT a good thing. Dropping stat update...");
		else
		{
			StatTracker stats = player.getCapability(StatCapability.target, null);
			message.loadInto(stats);
			SurvivalInc.logger.info("Received stat synchronization message from server: " + stats.toString());
		}
		return null;
	}
	
}
