package schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import schoperation.schopcraft.SchopCraft;
import schoperation.schopcraft.packet.SeasonPacket.SeasonMessage;
import schoperation.schopcraft.season.Season;
import schoperation.schopcraft.season.modifier.BiomeTempController;
import schoperation.schopcraft.util.SchopWorldData;

public class SeasonPacket implements IMessageHandler<SeasonMessage, IMessage> {

	@Override
	public IMessage onMessage(SeasonMessage message, MessageContext ctx)
	{

		if (ctx.side.isClient())
		{

			// Get message values
			int seasonInt = message.season;
			int daysIntoSeason = message.daysIntoSeason;

			BiomeTempController controller = new BiomeTempController();

			// Is daysIntoSeason -21? That's the code to restore biome
			// temperatures.
			if (daysIntoSeason == -21)
			{

				controller.resetBiomeTemperatures();
			}

			else
			{

				// Get original biome temps if not gotten them already, for the
				// client.
				controller.storeOriginalTemperatures();

				// Actual season
				Season season = SchopWorldData.intToSeason(seasonInt);

				// Change temperatures
				controller.changeBiomeTemperatures(season, daysIntoSeason);

				SchopCraft.logger.info("Synced the client's season data with the server's.");
			}
		}

		return null;
	}

	public static class SeasonMessage implements IMessage {

		// Variables used in the packet
		private int season;
		private int daysIntoSeason;

		// Necessary constructor.
		public SeasonMessage()
		{}

		public SeasonMessage(int season, int daysIntoSeason)
		{

			this.season = season;
			this.daysIntoSeason = daysIntoSeason;
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{

			this.season = buf.readInt();
			this.daysIntoSeason = buf.readInt();

		}

		@Override
		public void toBytes(ByteBuf buf)
		{

			buf.writeInt(season);
			buf.writeInt(daysIntoSeason);
		}
	}
}
