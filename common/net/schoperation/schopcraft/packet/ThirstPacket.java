package net.schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.schoperation.schopcraft.cap.thirst.ThirstModifier;
import net.schoperation.schopcraft.gui.GuiRenderBar;
import net.schoperation.schopcraft.packet.ThirstPacket.ThirstMessage;

public class ThirstPacket implements IMessageHandler<ThirstMessage, IMessage> {
	
	@Override
	public IMessage onMessage(ThirstMessage message, MessageContext ctx) {
		
		if(ctx.side.isClient()) {
			
			String uuid = message.uuid;
			float thirst = message.thirst;
			GuiRenderBar.getServerThirst(thirst);
		}
		else {
			
			String uuid = message.uuid;
			float thirst = message.thirst;
			ThirstModifier.getClientChange(uuid, thirst);
		}
		
		return null;
	}
	
	public static class ThirstMessage implements IMessage {
		
		// variables used in the packet
		private String uuid;
		private float thirst;
		
		// dumb constructor
		public ThirstMessage() {}
		
		public ThirstMessage(String uuid, float thirst) {
			
			this.uuid = uuid;
			this.thirst = thirst;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			
			this.uuid = ByteBufUtils.readUTF8String(buf);
			this.thirst = buf.readFloat();
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			
			ByteBufUtils.writeUTF8String(buf, uuid);
			buf.writeFloat(thirst);
		}
	}
}
