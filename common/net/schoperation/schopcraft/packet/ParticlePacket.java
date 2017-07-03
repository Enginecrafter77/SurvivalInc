package net.schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.schoperation.schopcraft.packet.ParticlePacket.ParticleMessage;
import net.schoperation.schopcraft.util.SchopServerParticles;

public class ParticlePacket implements IMessageHandler<ParticleMessage, IMessage> {
	
	@Override
	public IMessage onMessage(ParticleMessage message, MessageContext ctx) {
		
		if(ctx.side.isClient()) {
			
			double posX = message.posX;
			double posY = message.posY;
			double posZ = message.posZ;
			int methodPicker = message.methodPicker;
		}
		else {
			
			double posX = message.posX;
			double posY = message.posY;
			double posZ = message.posZ;
			int methodPicker = message.methodPicker;
			SchopServerParticles.changeParticlePosition(posX, posY, posZ, methodPicker);
		}
		
		return null;
	}
	
	public static class ParticleMessage implements IMessage {
		
		// variables used in the packet
		private double posX;
		private double posY;
		private double posZ;
		private int methodPicker;
		
		// dumb constructor
		public ParticleMessage() {}
		
		public ParticleMessage(double posX, double posY, double posZ, int methodPicker) {
			
			this.posX = posX;
			this.posY = posY;
			this.posZ = posZ;
			this.methodPicker = methodPicker;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			
			this.posX = buf.readDouble();
			this.posY = buf.readDouble();
			this.posZ = buf.readDouble();
			this.methodPicker = buf.readInt();
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			
			buf.writeDouble(posX);
			buf.writeDouble(posY);
			buf.writeDouble(posZ);
			buf.writeInt(methodPicker);
		}
	}
}
