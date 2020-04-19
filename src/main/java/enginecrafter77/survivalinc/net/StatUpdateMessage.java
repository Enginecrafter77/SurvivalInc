package enginecrafter77.survivalinc.net;

import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class StatUpdateMessage implements IMessage {
	
	public final IStorage<StatTracker> serializer;
	public final StatTracker tracker;
	
	public StatUpdateMessage(StatTracker tracker)
	{
		this.serializer = StatCapability.target.getStorage();
		this.tracker = tracker;
	}
	
	public StatUpdateMessage()
	{
		this(StatCapability.target.getDefaultInstance()); 
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		serializer.readNBT(StatCapability.target, tracker, null, tag);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		NBTBase tag = serializer.writeNBT(StatCapability.target, tracker, null);
		ByteBufUtils.writeTag(buf, (NBTTagCompound)tag);
	}
}
