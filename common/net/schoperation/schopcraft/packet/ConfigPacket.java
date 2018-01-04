package net.schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.schoperation.schopcraft.SchopCraft;
import net.schoperation.schopcraft.config.SchopConfig;
import net.schoperation.schopcraft.packet.ConfigPacket.ConfigMessage;

public class ConfigPacket implements IMessageHandler<ConfigMessage, IMessage> {
	
	@Override
	public IMessage onMessage(ConfigMessage message, MessageContext ctx) {
		
		if (ctx.side.isClient()) {
			
			// Sync server values to client.
			boolean enableGhost = message.enableGhost;
			boolean enableTemperature = message.enableTemperature;
			boolean enableThirst = message.enableThirst;
			boolean enableSanity = message.enableSanity;
			boolean enableWetness = message.enableWetness;
			
			SchopConfig.mechanics.enableGhost = enableGhost;
			SchopConfig.mechanics.enableTemperature = enableTemperature;
			SchopConfig.mechanics.enableThirst = enableThirst;
			SchopConfig.mechanics.enableSanity = enableSanity;
			SchopConfig.mechanics.enableWetness = enableWetness;
			
			SchopCraft.logger.info("Synced the client's config values with the server's.");
		}
		
		return null;
	}
	
	public static class ConfigMessage implements IMessage {
		
		// Variables used in the packet
		private boolean enableGhost;
		private boolean enableTemperature;
		private boolean enableThirst;
		private boolean enableSanity;
		private boolean enableWetness;
		
		// Necessary constructor.
		public ConfigMessage() {}
		
		public ConfigMessage(boolean enableGhost, boolean enableTemperature, boolean enableThirst, boolean enableSanity, boolean enableWetness) {
			
			this.enableGhost = enableGhost;
			this.enableTemperature = enableTemperature;
			this.enableThirst = enableThirst;
			this.enableSanity = enableSanity;
			this.enableWetness = enableWetness;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			
			this.enableGhost = buf.readBoolean();
			this.enableTemperature = buf.readBoolean();
			this.enableThirst = buf.readBoolean();
			this.enableSanity = buf.readBoolean();
			this.enableWetness = buf.readBoolean();
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			
			buf.writeBoolean(enableGhost);
			buf.writeBoolean(enableTemperature);
			buf.writeBoolean(enableThirst);
			buf.writeBoolean(enableSanity);
			buf.writeBoolean(enableWetness);
		}
	}
}
