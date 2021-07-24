package enginecrafter77.survivalinc;

import enginecrafter77.survivalinc.net.EntityItemUpdateMessage;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import enginecrafter77.survivalinc.season.SeasonSyncMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ServerProxy extends CommonProxy {
	@Override
	public void registerClientHandlers()
	{
		this.net.registerMessage(ServerProxy::dummyHandler, StatSyncMessage.class, 0, Side.CLIENT);
		this.net.registerMessage(ServerProxy::dummyHandler, SeasonSyncMessage.class, 1, Side.CLIENT);
		this.net.registerMessage(ServerProxy::dummyHandler, EntityItemUpdateMessage.class, 2, Side.CLIENT);
	}
	
	public static <MSG extends IMessage> IMessage dummyHandler(MSG message, MessageContext ctx)
	{
		SurvivalInc.logger.error("Dummy message handler intercepted message. This is not supposed to happend.");
		return null;
	}
}
