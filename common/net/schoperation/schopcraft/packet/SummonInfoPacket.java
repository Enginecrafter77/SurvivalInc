package net.schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.schoperation.schopcraft.packet.SummonInfoPacket.SummonInfoMessage;
import net.schoperation.schopcraft.util.SchopServerParticles;
import net.schoperation.schopcraft.util.SchopServerSounds;

public class SummonInfoPacket implements IMessageHandler<SummonInfoMessage, IMessage> {
	
	@Override
	public IMessage onMessage(SummonInfoMessage message, MessageContext ctx) {
		
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
			SchopServerSounds.changeSoundMethod(posX, posY, posZ, methodPicker);
		}
		
		return null;
	}
	
	public static class SummonInfoMessage implements IMessage {
		
		// variables used in the packet
		private double posX;
		private double posY;
		private double posZ;
		private int methodPicker;
		
		// dumb constructor
		public SummonInfoMessage() {}
		
		public SummonInfoMessage(double posX, double posY, double posZ, int methodPicker) {
			
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
