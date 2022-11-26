package enginecrafter77.survivalinc.net;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.StatCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@Deprecated
public class GhostUpdateMessageHandler implements IMessageHandler<GhostUpdateMessage, IMessage> {

	@Override
	public IMessage onMessage(GhostUpdateMessage message, MessageContext ctx)
	{
		WorldClient world = Minecraft.getMinecraft().world;
		EntityPlayer player = world.getPlayerEntityByUUID(message.player);
		player.getCapability(StatCapability.target, null).setRecord(SurvivalInc.ghost, message.record);
		SurvivalInc.logger.info("Received ghost update message ({}) about player {}", message.record, player.getDisplayName());
		return null;
	}

}
