package net.schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.schoperation.schopcraft.cap.temperature.TemperatureModifier;
import net.schoperation.schopcraft.gui.GuiRenderBar;
import net.schoperation.schopcraft.packet.TemperaturePacket.TemperatureMessage;

public class TemperaturePacket implements IMessageHandler<TemperatureMessage, IMessage> {
	
	@Override
	public IMessage onMessage(TemperatureMessage message, MessageContext ctx) {
		
		if (ctx.side.isClient()) {
			
			String uuid = message.uuid;
			float temperature = message.temperature;
			float maxTemperature = message.maxTemperature;
			float minTemperature = message.minTemperature;
			float targetTemperature = message.targetTemperature;
			GuiRenderBar.getServerTemperature(temperature, maxTemperature, targetTemperature);
		}
		else {
			
			String uuid = message.uuid;
			float temperature = message.temperature;
			float maxTemperature = message.maxTemperature;
			float minTemperature = message.minTemperature;
			float targetTemperature = message.targetTemperature;
			TemperatureModifier.getClientChange(uuid, temperature, maxTemperature, minTemperature, targetTemperature);
		}
		
		return null;
	}
	
	public static class TemperatureMessage implements IMessage {
		
		// variables used in the packet
		private String uuid;
		private float temperature;
		private float maxTemperature;
		private float minTemperature;
		private float targetTemperature;
		
		// dumb constructor
		public TemperatureMessage() {}
		
		public TemperatureMessage(String uuid, float temperature, float maxTemperature, float minTemperature, float targetTemperature) {
			
			this.uuid = uuid;
			this.temperature = temperature;
			this.maxTemperature = maxTemperature;
			this.minTemperature = minTemperature;
			this.targetTemperature = targetTemperature;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			
			this.uuid = ByteBufUtils.readUTF8String(buf);
			this.temperature = buf.readFloat();
			this.maxTemperature = buf.readFloat();
			this.minTemperature = buf.readFloat();
			this.targetTemperature = buf.readFloat();
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			
			ByteBufUtils.writeUTF8String(buf, uuid);
			buf.writeFloat(temperature);
			buf.writeFloat(maxTemperature);
			buf.writeFloat(minTemperature);
			buf.writeFloat(targetTemperature);
		}
	}
}
