package enginecrafter77.survivalinc.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Stat Sync Message can be used to effectively
 * sync server-side stat data kept about players
 * with the player's client.
 * @author Enginecrafter77
 */
public class StatSyncMessage implements IMessage {
	
	/** A tag compound storing all the player data */
	private final HashMap<UUID, NBTTagCompound> data;
	
	/**
	 * Constructs a new stat sync message, which
	 * contains tracker data from all players
	 * currently in the world. This message is
	 * effectively the same as sending numerous
	 * messages constructed using {@link #StatSyncMessage(EntityPlayer)},
	 * albeit this method is more effective CPU-time-wise.
	 * @param world The world to pull the tracker data from
	 */
	public StatSyncMessage(World world)
	{
		this.data = new HashMap<UUID, NBTTagCompound>();	
		for(EntityPlayer player : world.playerEntities)
			this.addPlayerTrackerData(player);
	}
	
	/**
	 * Constructs a new stat sync message, containing
	 * sync information about only one player.
	 * @param player The player to send data about
	 */
	public StatSyncMessage(EntityPlayer player)
	{
		this.data = new HashMap<UUID, NBTTagCompound>();
		this.addPlayerTrackerData(player);
	}
	
	/**
	 * Constructs an empty StatSyncMessage. In order for
	 * the message to be actually useful, {@link #addPlayerTrackerData(EntityPlayer)}
	 * needs to be called on the object at least once.
	 * 
	 * This constructor is also used on {@link Side#CLIENT client}
	 * side to construct dummy StatSyncMessage, that is later
	 * initialized from the incoming data.
	 */
	public StatSyncMessage()
	{
		this.data = new HashMap<UUID, NBTTagCompound>();
	}
	
	@SideOnly(Side.CLIENT)
	public void loadInto(WorldClient world)
	{
		IStorage<StatTracker> serializer = StatCapability.target.getStorage();
		for(Map.Entry<UUID, NBTTagCompound> entry : this.data.entrySet())
		{
			EntityPlayer player = world.getPlayerEntityByUUID(entry.getKey());
			
			if(player == null)
			{
				SurvivalInc.logger.error("Player {} not found on client.", entry.getKey().toString());
				continue;
			}
			
			StatTracker tracker = player.getCapability(StatCapability.target, null);
			serializer.readNBT(StatCapability.target, tracker, null, entry.getValue());
		}
	}
	
	public Set<UUID> getPayloadUUIDList()
	{
		return this.data.keySet();
	}
	
	public void addPlayerTrackerData(EntityPlayer player)
	{
		IStorage<StatTracker> serializer = StatCapability.target.getStorage();
		StatTracker tracker = player.getCapability(StatCapability.target, null);
		NBTTagCompound data = (NBTTagCompound)serializer.writeNBT(StatCapability.target, tracker, null);
		this.data.put(player.getUniqueID(), data);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		NBTTagCompound bundle = ByteBufUtils.readTag(buf);
		
		for(String key : bundle.getKeySet())
		{
			NBTTagCompound trackerdata = bundle.getCompoundTag(key);
			UUID playeruuid = UUID.fromString(key);
			this.data.put(playeruuid, trackerdata);
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		NBTTagCompound bundle = new NBTTagCompound();
		for(Map.Entry<UUID, NBTTagCompound> entry : this.data.entrySet())
			bundle.setTag(entry.getKey().toString(), entry.getValue());
		ByteBufUtils.writeTag(buf, bundle);
	}
}
