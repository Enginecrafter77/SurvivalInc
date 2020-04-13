package enginecrafter77.survivalinc.net;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Updates the client representation of the {@link EntityItem}
 * on server side. This handler internally uses {@link Minecraft#getMinecraft()}
 * to get the {@link WorldClient client-side world} instance.
 * As such, the handler's {@link #onMessage(EntityItemUpdateMessage, MessageContext)}
 * checks the context for side, throwing exception if the packet is processed on server.
 * Either way, this is only a safety measure since the packet is always processed on the client side.
 * @see Minecraft#getMinecraft()
 * @see EntityItemUpdateMessage
 */
public class EntityItemUpdater implements IMessageHandler<EntityItemUpdateMessage, IMessage> {

	@Override
	public IMessage onMessage(EntityItemUpdateMessage message, MessageContext ctx)
	{
		if(ctx.side == Side.SERVER)
			throw new RuntimeException("EntityItemUpdate is designed to be processed on client!");
		
		// Damn, I know what I am doing!
		Minecraft instance = Minecraft.getMinecraft();
		WorldClient world = instance.world;
		Entity ent = world.getEntityByID(message.entityid);
		
		if(ent instanceof EntityItem)
		{
			EntityItem eitem = (EntityItem)ent;
			eitem.setItem(message.stack);
			return null;
		}
		
		throw new RuntimeException("Entity specified in packet is not EntityItem!");
	}

}
