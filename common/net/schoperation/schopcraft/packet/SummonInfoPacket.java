package net.schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.schoperation.schopcraft.packet.SummonInfoPacket.SummonInfoMessage;
import net.schoperation.schopcraft.util.SchopServerParticles;
import net.schoperation.schopcraft.util.SchopServerSounds;
import net.schoperation.schopcraft.util.client.SchopClientParticles;
import net.schoperation.schopcraft.util.client.SchopClientSounds;

/*
 * Used for summoning particles and playing sounds.
 */
public class SummonInfoPacket implements IMessageHandler<SummonInfoMessage, IMessage> {
	
	@Override
	public IMessage onMessage(SummonInfoMessage message, MessageContext ctx) {
		
		if (ctx.side.isServer()) {
			
			String uuid = message.uuid;
			String soundPicker = message.soundPicker;
			String particlePicker = message.particlePicker;
			double posX = message.posX;
			double posY = message.posY;
			double posZ = message.posZ;
			SchopServerParticles.summonParticle(uuid, particlePicker, posX, posY, posZ);
			SchopServerSounds.playSound(uuid, soundPicker, posX, posY, posZ);
		}
		else {
			
			String uuid = message.uuid;
			String soundPicker = message.soundPicker;
			String particlePicker = message.particlePicker;
			double posX = message.posX;
			double posY = message.posY;
			double posZ = message.posZ;
			SchopClientParticles.summonParticle(uuid, particlePicker, posX, posY, posZ);
			SchopClientSounds.playSound(uuid, soundPicker, posX, posY, posZ);
		}
		
		return null;
	}
	
	public static class SummonInfoMessage implements IMessage {
		
		// variables used in the packet
		private String uuid;
		private String soundPicker;
		private String particlePicker;
		private double posX;
		private double posY;
		private double posZ;
		
		// dumb constructor
		public SummonInfoMessage() {}
		
		public SummonInfoMessage(String uuid, String soundPicker, String particlePicker, double posX, double posY, double posZ) {
			
			this.uuid = uuid;
			this.soundPicker = soundPicker;
			this.particlePicker = particlePicker;
			this.posX = posX;
			this.posY = posY;
			this.posZ = posZ;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			
			this.uuid = ByteBufUtils.readUTF8String(buf);
			this.soundPicker = ByteBufUtils.readUTF8String(buf);
			this.particlePicker = ByteBufUtils.readUTF8String(buf);
			this.posX = buf.readDouble();
			this.posY = buf.readDouble();
			this.posZ = buf.readDouble();
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			
			ByteBufUtils.writeUTF8String(buf, uuid);
			ByteBufUtils.writeUTF8String(buf, soundPicker);
			ByteBufUtils.writeUTF8String(buf, particlePicker);
			buf.writeDouble(posX);
			buf.writeDouble(posY);
			buf.writeDouble(posZ);
		}
	}
}
