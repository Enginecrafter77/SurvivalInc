package net.schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.schoperation.schopcraft.gui.GuiRenderBar;
import net.schoperation.schopcraft.packet.GuiRenderPacket.GuiRenderMessage;

public class GuiRenderPacket implements IMessageHandler<GuiRenderMessage, IMessage> {
	
	@Override
	public IMessage onMessage(GuiRenderMessage message, MessageContext ctx) {
		
		if (ctx.side.isClient()) {
			
			String uuid = message.uuid;
			
			float temperature = message.temperature;
			float maxTemperature = message.maxTemperature;
			float minTemperature = message.minTemperature;
			float targetTemperature = message.targetTemperature;
			
			float thirst = message.thirst;
			float maxThirst = message.maxThirst;
			float minThirst = message.minThirst;
			
			// Sanity
			float sanity = message.sanity;
			float maxSanity = message.maxSanity;
			float minSanity = message.minSanity;
			
			// Wetness
			float wetness = message.wetness;
			float maxWetness = message.maxWetness;
			float minWetness = message.minWetness;
			
			// Ghost Stats
			boolean isGhost = message.isGhost;
			float ghostEnergy = message.ghostEnergy;
			GuiRenderBar.getServerStats(temperature, maxTemperature, minTemperature, targetTemperature, thirst, maxThirst, minThirst, sanity, maxSanity, minSanity, wetness, maxWetness, minWetness, isGhost, ghostEnergy);
		}
		
		return null;
	}
	
	public static class GuiRenderMessage implements IMessage {
		
		// Variables sent to the client for rendering.
		private String uuid;
		
		// Temperature
		private float temperature;
		private float maxTemperature;
		private float minTemperature;
		private float targetTemperature;
		
		// Thirst
		private float thirst;
		private float maxThirst;
		private float minThirst;
		
		// Sanity
		private float sanity;
		private float maxSanity;
		private float minSanity;
		
		// Wetness
		private float wetness;
		private float maxWetness;
		private float minWetness;
		
		// Ghost Stats
		private boolean isGhost;
		private float ghostEnergy;
		
		// Necessary constructor.
		public GuiRenderMessage() {}
		
		public GuiRenderMessage(String uuid, float temperature, float maxTemperature, float minTemperature, float targetTemperature, float thirst, float maxThirst, float minThirst, float sanity, float maxSanity, float minSanity, float wetness, float maxWetness, float minWetness, boolean isGhost, float ghostEnergy) {
			
			this.uuid = uuid;
			this.temperature = temperature;
			this.maxTemperature = maxTemperature;
			this.minTemperature = minTemperature;
			this.targetTemperature = targetTemperature;
			this.thirst = thirst;
			this.maxThirst = maxThirst;
			this.minThirst = minThirst;
			this.sanity = sanity;
			this.maxSanity = maxSanity;
			this.minSanity = minSanity;
			this.wetness = wetness;
			this.maxWetness = maxWetness;
			this.minWetness = minWetness;
			this.isGhost = isGhost;
			this.ghostEnergy = ghostEnergy;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			
			this.uuid = ByteBufUtils.readUTF8String(buf);
			this.temperature = buf.readFloat();
			this.maxTemperature = buf.readFloat();
			this.minTemperature = buf.readFloat();
			this.targetTemperature = buf.readFloat();
			this.thirst = buf.readFloat();
			this.maxThirst = buf.readFloat();
			this.minThirst = buf.readFloat();
			this.sanity = buf.readFloat();
			this.maxSanity = buf.readFloat();
			this.minSanity = buf.readFloat();
			this.wetness = buf.readFloat();
			this.maxWetness = buf.readFloat();
			this.minWetness = buf.readFloat();
			this.isGhost = buf.readBoolean();
			this.ghostEnergy = buf.readFloat();
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			
			ByteBufUtils.writeUTF8String(buf, uuid);
			buf.writeFloat(temperature);
			buf.writeFloat(maxTemperature);
			buf.writeFloat(minTemperature);
			buf.writeFloat(targetTemperature);
			buf.writeFloat(thirst);
			buf.writeFloat(maxThirst);
			buf.writeFloat(minThirst);
			buf.writeFloat(sanity);
			buf.writeFloat(maxSanity);
			buf.writeFloat(minSanity);
			buf.writeFloat(wetness);
			buf.writeFloat(maxWetness);
			buf.writeFloat(minWetness);
			buf.writeBoolean(isGhost);
			buf.writeFloat(ghostEnergy);
		}
	}
}