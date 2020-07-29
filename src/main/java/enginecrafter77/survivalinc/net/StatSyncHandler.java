package enginecrafter77.survivalinc.net;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StatSyncHandler implements IMessageHandler<StatSyncMessage, IMessage>{
	
	@Override
	public IMessage onMessage(StatSyncMessage message, MessageContext ctx)
	{
		WorldClient world = Minecraft.getMinecraft().world;
		if(world == null) SurvivalInc.logger.error("Minecraft remote world instance is null. This is NOT a good thing. Dropping stat update...");
		else
		{
			message.loadInto(world);
			SurvivalInc.logger.info("Stat sync received. Payload: {}", message.getPayloadUUIDList());
		}
		return null;
	}
	
}
