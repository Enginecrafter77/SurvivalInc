package enginecrafter77.survivalinc.net;

import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class StatUpdateMessage implements IMessage {
	
	public NBTTagCompound trackerdata;
	
	public StatUpdateMessage(StatTracker tracker)
	{
		this.trackerdata = (NBTTagCompound)StatCapability.target.getStorage().writeNBT(StatCapability.target, tracker, null);
	}
	
	public StatUpdateMessage()
	{
		this.trackerdata = null;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.trackerdata = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, this.trackerdata);
	}
}
