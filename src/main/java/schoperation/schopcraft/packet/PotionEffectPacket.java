package schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.schoperation.schopcraft.packet.PotionEffectPacket.PotionEffectMessage;
import net.schoperation.schopcraft.util.SchopServerEffects;

/*
 * Used to add potion effects to the player on the server, if the method that wants to add an effect is on the client. 
 */
public class PotionEffectPacket implements IMessageHandler<PotionEffectMessage, IMessage> {

	@Override
	public IMessage onMessage(PotionEffectMessage message, MessageContext ctx) {
		
		if(ctx.side.isServer()) {
						
			String uuid = message.uuid;
			String effect = message.effect;
			int duration = message.duration;
			int amplifier = message.amplifier;
			boolean isAmbient = message.isAmbient;
			boolean showParticles = message.showParticles;
			SchopServerEffects.affectPlayer(uuid, effect, duration, amplifier, isAmbient, showParticles);
		}
		
		return null;
	}
	
	public static class PotionEffectMessage implements IMessage {

		// Variables used in the packet.
		private String uuid;
		private String effect;
		private int duration;
		private int amplifier;
		private boolean isAmbient;
		private boolean showParticles;
		
		// Necessary constructor.
		public PotionEffectMessage() {}
		
		public PotionEffectMessage(String uuid, String effect, int duration, int amplifier, boolean isAmbient, boolean showParticles) {
			
			this.uuid = uuid;
			this.effect = effect;
			this.duration = duration;
			this.amplifier = amplifier;
			this.isAmbient = isAmbient;
			this.showParticles = showParticles;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			
			this.uuid = ByteBufUtils.readUTF8String(buf);
			this.effect = ByteBufUtils.readUTF8String(buf);
			this.duration = buf.readInt();
			this.amplifier = buf.readInt();
			this.isAmbient = buf.readBoolean();
			this.showParticles = buf.readBoolean();
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			
			ByteBufUtils.writeUTF8String(buf, uuid);
			ByteBufUtils.writeUTF8String(buf, effect);
			buf.writeInt(duration);
			buf.writeInt(amplifier);
			buf.writeBoolean(isAmbient);
			buf.writeBoolean(showParticles);
		}
	}
}