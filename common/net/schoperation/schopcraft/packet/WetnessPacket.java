package net.schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.schoperation.schopcraft.cap.wetness.WetnessModifier;
import net.schoperation.schopcraft.gui.GuiRenderBar;
import net.schoperation.schopcraft.packet.WetnessPacket.WetnessMessage;

public class WetnessPacket implements IMessageHandler<WetnessMessage, IMessage> {
	
	@Override
	public IMessage onMessage(WetnessMessage message, MessageContext ctx) {
		
		if (ctx.side.isClient()) {
			
			String uuid = message.uuid;
			float wetness = message.wetness;
			float maxWetness = message.maxWetness;
			float minWetness = message.minWetness;
			GuiRenderBar.getServerWetness(wetness, maxWetness);
		}
		else {
			
			String uuid = message.uuid;
			float wetness = message.wetness;
			float maxWetness = message.maxWetness;
			float minWetness = message.minWetness;
			WetnessModifier.getClientChange(uuid, wetness, maxWetness, minWetness);
		}
		
		return null;
	}
	
	public static class WetnessMessage implements IMessage {
		
		// Variables used in the packet.
		private String uuid;
		private float wetness;
		private float maxWetness;
		private float minWetness;
		
		// Necessary constructor.
		public WetnessMessage() {}
		
		public WetnessMessage(String uuid, float wetness, float maxWetness, float minWetness) {
			
			this.uuid = uuid;
			this.wetness = wetness;
			this.maxWetness = maxWetness;
			this.minWetness = minWetness;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			
			this.uuid = ByteBufUtils.readUTF8String(buf);
			this.wetness = buf.readFloat();
			this.maxWetness = buf.readFloat();
			this.minWetness = buf.readFloat();
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			
			ByteBufUtils.writeUTF8String(buf, uuid);
			buf.writeFloat(wetness);
			buf.writeFloat(maxWetness);
			buf.writeFloat(minWetness);
		}
	}
}