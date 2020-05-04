package enginecrafter77.survivalinc.net;

import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Stat Sync Message can be used to effectively
 * sync server-side stat data kept about player
 * with the player's client.
 * @author Enginecrafter77
 */
public class StatSyncMessage implements IMessage {
	
	public final IStorage<StatTracker> serializer;
	private NBTTagCompound trackerdata;
	
	/**
	 * Constructs new stat sync message, and serializes
	 * the provided stat tracker as data into it.
	 * @param tracker The tracker to serialize data from
	 */
	public StatSyncMessage(StatTracker tracker)
	{
		this.serializer = StatCapability.target.getStorage();
		this.trackerdata = (NBTTagCompound)serializer.writeNBT(StatCapability.target, tracker, null);
	}
	
	/**
	 * Constructor used by the client-side packet handling
	 */
	@SideOnly(Side.CLIENT)
	public StatSyncMessage()
	{
		this.serializer = StatCapability.target.getStorage();
		this.trackerdata = null;
	}
	
	/**
	 * Deserializes the data stored in this message
	 * into the specified stat tracker.
	 * @param tracker The tracker to load the data into
	 */
	public void loadInto(StatTracker tracker)
	{
		if(this.trackerdata == null) throw new NullPointerException("Stat sync data not deserialized yet");
		
		serializer.readNBT(StatCapability.target, tracker, null, this.trackerdata);
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
