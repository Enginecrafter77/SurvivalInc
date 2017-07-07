package net.schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.schoperation.schopcraft.packet.SummonInfoPacket.SummonInfoMessage;
import net.schoperation.schopcraft.util.SchopServerParticles;
import net.schoperation.schopcraft.util.SchopServerSounds;

/*
 * Used for summoning particles and playing sounds.
 */
public class SummonInfoPacket implements IMessageHandler<SummonInfoMessage, IMessage> {
	
	@Override
	public IMessage onMessage(SummonInfoMessage message, MessageContext ctx) {
		
		if(ctx.side.isServer()) {
			
			double posX = message.posX;
			double posY = message.posY;
			double posZ = message.posZ;
			int particlePicker = message.particlePicker;
			int soundPicker = message.soundPicker;
			SchopServerParticles.changeParticlePosition(posX, posY, posZ, particlePicker);
			SchopServerSounds.changeSoundMethod(posX, posY, posZ, soundPicker);
		}
		
		return null;
	}
	
	public static class SummonInfoMessage implements IMessage {
		
		// variables used in the packet
		private double posX;
		private double posY;
		private double posZ;
		private int particlePicker;
		private int soundPicker;
		
		// dumb constructor
		public SummonInfoMessage() {}
		
		public SummonInfoMessage(double posX, double posY, double posZ, int particlePicker, int soundPicker) {
			
			this.posX = posX;
			this.posY = posY;
			this.posZ = posZ;
			this.particlePicker = particlePicker;
			this.soundPicker = soundPicker;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			
			this.posX = buf.readDouble();
			this.posY = buf.readDouble();
			this.posZ = buf.readDouble();
			this.particlePicker = buf.readInt();
			this.soundPicker = buf.readInt();
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			
			buf.writeDouble(posX);
			buf.writeDouble(posY);
			buf.writeDouble(posZ);
			buf.writeInt(particlePicker);
			buf.writeInt(soundPicker);
		}
	}
}
