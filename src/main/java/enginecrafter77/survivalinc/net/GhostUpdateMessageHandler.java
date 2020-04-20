package enginecrafter77.survivalinc.net;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.ghost.Ghost;
import enginecrafter77.survivalinc.ghost.GhostProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GhostUpdateMessageHandler implements IMessageHandler<GhostUpdateMessage, IMessage> {

	@Override
	public IMessage onMessage(GhostUpdateMessage message, MessageContext ctx)
	{
		WorldClient world = Minecraft.getMinecraft().world;
		EntityPlayer player = world.getPlayerEntityByUUID(message.player);
		
		Ghost ghost = player.getCapability(GhostProvider.target, null);
		ghost.setStatus(message.status);
		
		SurvivalInc.logger.info("Received ghost update message ({}) about player {}", message.status, player.getDisplayName());
		return null;
	}

}
