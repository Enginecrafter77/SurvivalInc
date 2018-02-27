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
			double temperatureScale = message.temperatureScale;
			double thirstScale = message.thirstScale;
			double sanityScale = message.sanityScale;
			double wetnessScale = message.wetnessScale;
			
			boolean aenableSeasons = message.aenableSeasons;
			int winterLength = message.winterLength;
			int springLength = message.springLength;
			int summerLength = message.summerLength;
			int autumnLength = message.autumnLength;
			
			SchopConfig.MECHANICS.enableGhost = enableGhost;
			SchopConfig.MECHANICS.enableTemperature = enableTemperature;
			SchopConfig.MECHANICS.enableThirst = enableThirst;
			SchopConfig.MECHANICS.enableSanity = enableSanity;
			SchopConfig.MECHANICS.enableWetness = enableWetness;
			SchopConfig.MECHANICS.temperatureScale = temperatureScale;
			SchopConfig.MECHANICS.thirstScale = thirstScale;
			SchopConfig.MECHANICS.sanityScale = sanityScale;
			SchopConfig.MECHANICS.wetnessScale = wetnessScale;
			
			SchopConfig.SEASONS.aenableSeasons = aenableSeasons;
			SchopConfig.SEASONS.winterLength = winterLength;
			SchopConfig.SEASONS.springLength = springLength;
			SchopConfig.SEASONS.summerLength = summerLength;
			SchopConfig.SEASONS.autumnLength = autumnLength;
			
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
		private double temperatureScale;
		private double thirstScale;
		private double sanityScale;
		private double wetnessScale;
		
		private boolean aenableSeasons;
		private boolean aenableDayLength;
		private int winterLength;
		private int springLength;
		private int summerLength;
		private int autumnLength;
		
		// Necessary constructor.
		public ConfigMessage() {}
		
		public ConfigMessage(boolean enableGhost, boolean enableTemperature, boolean enableThirst, boolean enableSanity, boolean enableWetness, double temperatureScale, double thirstScale, double sanityScale, double wetnessScale, boolean aenableSeasons, boolean aenableDayLength, int winterLength, int springLength, int summerLength, int autumnLength) {
			
			this.enableGhost = enableGhost;
			this.enableTemperature = enableTemperature;
			this.enableThirst = enableThirst;
			this.enableSanity = enableSanity;
			this.enableWetness = enableWetness;
			this.temperatureScale = temperatureScale;
			this.thirstScale = thirstScale;
			this.sanityScale = sanityScale;
			this.wetnessScale = wetnessScale;
			
			this.aenableSeasons = aenableSeasons;
			this.aenableDayLength = aenableDayLength;
			this.winterLength = winterLength;
			this.springLength = springLength;
			this.summerLength = summerLength;
			this.autumnLength = autumnLength;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			
			this.enableGhost = buf.readBoolean();
			this.enableTemperature = buf.readBoolean();
			this.enableThirst = buf.readBoolean();
			this.enableSanity = buf.readBoolean();
			this.enableWetness = buf.readBoolean();
			this.temperatureScale = buf.readDouble();
			this.thirstScale = buf.readDouble();
			this.sanityScale = buf.readDouble();
			this.wetnessScale = buf.readDouble();
			
			this.aenableSeasons = buf.readBoolean();
			this.aenableDayLength = buf.readBoolean();
			this.winterLength = buf.readInt();
			this.springLength = buf.readInt();
			this.summerLength = buf.readInt();
			this.autumnLength = buf.readInt();
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			
			buf.writeBoolean(enableGhost);
			buf.writeBoolean(enableTemperature);
			buf.writeBoolean(enableThirst);
			buf.writeBoolean(enableSanity);
			buf.writeBoolean(enableWetness);
			buf.writeDouble(temperatureScale);
			buf.writeDouble(thirstScale);
			buf.writeDouble(sanityScale);
			buf.writeDouble(wetnessScale);
			
			buf.writeBoolean(aenableSeasons);
			buf.writeBoolean(aenableDayLength);
			buf.writeInt(winterLength);
			buf.writeInt(springLength);
			buf.writeInt(summerLength);
			buf.writeInt(autumnLength);
		}
	}
}
