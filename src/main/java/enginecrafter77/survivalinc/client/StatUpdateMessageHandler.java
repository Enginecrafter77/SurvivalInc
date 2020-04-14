package enginecrafter77.survivalinc.client;

import enginecrafter77.survivalinc.net.StatUpdateMessage;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StatUpdateMessageHandler implements IMessageHandler<StatUpdateMessage, IMessage>{
	
	@Override
	public IMessage onMessage(StatUpdateMessage message, MessageContext ctx)
	{		
		// I hope I know what I am doing
		
		// Set the stats for a client copy of the remote player
		StatTracker stats = Minecraft.getMinecraft().player.getCapability(StatRegister.CAPABILITY, null);
		for(StatProvider provider : message.tracker.getRegisteredProviders())
			stats.setStat(provider, message.tracker.getStat(provider));
		return null;
	}
	
}
