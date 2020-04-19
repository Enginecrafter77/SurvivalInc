package enginecrafter77.survivalinc.client;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.net.StatUpdateMessage;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StatUpdateMessageHandler implements IMessageHandler<StatUpdateMessage, IMessage>{
	
	@Override
	public IMessage onMessage(StatUpdateMessage message, MessageContext ctx)
	{
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if(player == null) SurvivalInc.logger.error("Minecraft remote player entity is null. This is NOT a good thing. Skipping stat update...");
		else
		{
			StatTracker stats = player.getCapability(StatCapability.target, null);
			for(StatProvider provider : message.tracker.getRegisteredProviders())
				stats.setStat(provider, message.tracker.getStat(provider));
		}
		return null;
	}
	
}
