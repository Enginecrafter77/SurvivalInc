package enginecrafter77.survivalinc;

import enginecrafter77.survivalinc.net.*;
import enginecrafter77.survivalinc.season.SeasonController;
import enginecrafter77.survivalinc.season.SeasonSyncMessage;
import enginecrafter77.survivalinc.season.SeasonSyncRequest;
import enginecrafter77.survivalinc.stats.impl.HydrationModifier;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

public class ServerProxy implements SurvivalIncProxy {
	@Override
	public void registerNetworkHandlers(SimpleNetworkWrapper net)
	{
		net.registerMessage(ServerProxy::dummyHandler, StatSyncMessage.class, 0, Side.CLIENT);
		net.registerMessage(ServerProxy::dummyHandler, SeasonSyncMessage.class, 1, Side.CLIENT);
		net.registerMessage(ServerProxy::dummyHandler, EntityItemUpdateMessage.class, 2, Side.CLIENT);
		net.registerMessage(HydrationModifier::validateMessage, WaterDrinkMessage.class, 3, Side.SERVER);
		net.registerMessage(StatSyncRequestHandler.class, StatSyncRequestMessage.class, 4, Side.SERVER);
		net.registerMessage(SeasonController::onSyncRequest, SeasonSyncRequest.class, 5, Side.SERVER);
	}

	@Override
	public void registerRendering()
	{
		//NOOP
	}

	@Override
	public void createHUD()
	{
		//NOOP
	}

	@Nullable
	@Override
	public Object getAuxiliaryEventHandler()
	{
		return null;
	}

	public static <MSG extends IMessage> IMessage dummyHandler(MSG message, MessageContext ctx)
	{
		SurvivalInc.logger.error("Dummy message handler intercepted message. This is not supposed to happend.");
		return null;
	}
}
