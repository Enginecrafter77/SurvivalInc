package net.schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.schoperation.schopcraft.SchopCraft;
import net.schoperation.schopcraft.SchopWorldData;
import net.schoperation.schopcraft.packet.SeasonPacket.SeasonMessage;
import net.schoperation.schopcraft.season.BiomeTemp;
import net.schoperation.schopcraft.season.Season;

public class SeasonPacket implements IMessageHandler<SeasonMessage, IMessage> {
	
	@Override
	public IMessage onMessage(SeasonMessage message, MessageContext ctx) {
		
		if (ctx.side.isClient()) {
			
			// Get message values
			int seasonInt = message.season;
			int daysIntoSeason = message.daysIntoSeason;
			
			// Get original biome temps if not gotten them already, for the client.
			if (BiomeTemp.temperatures == null) {
				
				BiomeTemp.storeOriginalTemperatures();
			}
			
			// Actual season
			Season season = SchopWorldData.intToSeason(seasonInt);
			
			// Change temperatures
			BiomeTemp.changeBiomeTemperatures(season, daysIntoSeason);
			
			SchopCraft.logger.info("Synced the client's season data with the server's.");
		}
		
		return null;
	}
	
	public static class SeasonMessage implements IMessage {
		
		// Variables used in the packet
		private int season;
		private int daysIntoSeason;
		
		// Necessary constructor.
		public SeasonMessage() {}
		
		public SeasonMessage(int season, int daysIntoSeason) {
			
			this.season = season;
			this.daysIntoSeason = daysIntoSeason;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			
			this.season = buf.readInt();
			this.daysIntoSeason = buf.readInt();
			
		}

		@Override
		public void toBytes(ByteBuf buf) {
			
			buf.writeInt(season);
			buf.writeInt(daysIntoSeason);
		}
	}
}
