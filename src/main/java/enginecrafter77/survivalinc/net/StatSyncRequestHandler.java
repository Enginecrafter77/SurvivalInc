package enginecrafter77.survivalinc.net;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StatSyncRequestHandler implements IMessageHandler<StatSyncRequestMessage, StatSyncMessage> {
	@Override
	public StatSyncMessage onMessage(StatSyncRequestMessage message, MessageContext ctx)
	{
		NetHandlerPlayServer server = ctx.getServerHandler();
		StatSyncMessage msg = new StatSyncMessage();
		msg.addAllPlayers(server.player.world);
		SurvivalInc.logger.info("Stat sync request received from {}({}); Dispatching reponse: {}", server.player.getName(), server.player.getUniqueID().toString(), msg.toString());
		return msg;
	}
}
