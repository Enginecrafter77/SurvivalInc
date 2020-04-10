package enginecrafter77.survivalinc.net;

import enginecrafter77.survivalinc.season.Season;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@Deprecated
public class SeasonMessage implements IMessage {
	// Variables used in the packet
	public Season season;
	public int day;

	// Necessary constructor.
	public SeasonMessage() {}

	public SeasonMessage(Season season, int day)
	{
		this.season = season;
		this.day = day;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int ordinal = buf.readInt();
		this.season = Season.values()[ordinal];
		this.day = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(season.ordinal());
		buf.writeInt(this.day);
	}
}
