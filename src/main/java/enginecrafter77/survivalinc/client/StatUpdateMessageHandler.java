package enginecrafter77.survivalinc.client;

import enginecrafter77.survivalinc.net.StatUpdateMessage;
import enginecrafter77.survivalinc.stats.StatProvider;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StatUpdateMessageHandler implements IMessageHandler<StatUpdateMessage, IMessage>{
	
	@Override
	public IMessage onMessage(StatUpdateMessage message, MessageContext ctx)
	{
		for(StatProvider provider : message.tracker.getRegisteredProviders())
			RenderHUD.instance.tracker.setStat(provider, message.tracker.getStat(provider));
		return null;
	}
	
}
