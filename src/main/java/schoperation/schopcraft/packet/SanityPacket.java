package schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.schoperation.schopcraft.cap.sanity.SanityModifier;
import net.schoperation.schopcraft.packet.SanityPacket.SanityMessage;

public class SanityPacket implements IMessageHandler<SanityMessage, IMessage> {
	
	@Override
	public IMessage onMessage(SanityMessage message, MessageContext ctx) {
		
		if (ctx.side.isServer()) {
			
			String uuid = message.uuid;
			float sanity = message.sanity;
			float maxSanity = message.maxSanity;
			float minSanity = message.minSanity;
			SanityModifier.getClientChange(uuid, sanity, maxSanity, minSanity);
		}
		
		return null;
	}
	
	public static class SanityMessage implements IMessage {
		
		// Variables used in the packet.
		private String uuid;
		private float sanity;
		private float maxSanity;
		private float minSanity;
		
		// Necessary constructor.
		public SanityMessage() {}
		
		public SanityMessage(String uuid, float sanity, float maxSanity, float minSanity) {
			
			this.uuid = uuid;
			this.sanity = sanity;
			this.maxSanity = maxSanity;
			this.minSanity = minSanity;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			
			this.uuid = ByteBufUtils.readUTF8String(buf);
			this.sanity = buf.readFloat();
			this.maxSanity = buf.readFloat();
			this.minSanity = buf.readFloat();
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			
			ByteBufUtils.writeUTF8String(buf, uuid);
			buf.writeFloat(sanity);
			buf.writeFloat(maxSanity);
			buf.writeFloat(minSanity);
		}
	}
}