package enginecrafter77.survivalinc.net;

import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;

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
	 * Constructs an empty StatSyncMessage.
	 * This constructor is also used on {@link Side#CLIENT client}
	 * side to construct dummy StatSyncMessage that is later
	 * initialized from the incoming data.
	 */
	public StatSyncMessage()
	{
		this.data = new HashMap<UUID, NBTTagCompound>();
	}
	
	public Set<UUID> getPayloadUUIDList()
	{
		return this.data.keySet();
	}
	
	public Set<Map.Entry<UUID, NBTTagCompound>> getDataSet()
	{
		return this.data.entrySet();
	}
	
	public StatSyncMessage addPlayer(EntityPlayer player)
	{
		Capability<StatTracker> capability = StatCapability.getInstance();
		IStorage<StatTracker> serializer = capability.getStorage();
		StatTracker tracker = player.getCapability(capability, null);
		NBTTagCompound data = (NBTTagCompound)serializer.writeNBT(capability, tracker, null);
		this.data.put(player.getUniqueID(), data);
		return this;
	}
	
	public StatSyncMessage addAllPlayers(World world)
	{
		for(EntityPlayer player : world.playerEntities)
			this.addPlayer(player);
		return this;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		NBTTagCompound bundle = ByteBufUtils.readTag(buf);
		if(bundle == null)
			throw new RuntimeException("Malformed stat sync message");

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
	
	@Override
	public String toString()
	{
		return "StatSync" + this.data;
	}

	public static StatSyncMessage withPlayer(EntityPlayer player)
	{
		StatSyncMessage message = new StatSyncMessage();
		message.addPlayer(player);
		return message;
	}

	public static StatSyncMessage withPlayers(Collection<EntityPlayer> players)
	{
		StatSyncMessage message = new StatSyncMessage();
		players.forEach(message::addPlayer);
		return message;
	}

	public static StatSyncMessage withAllPlayersIn(World world)
	{
		StatSyncMessage message = new StatSyncMessage();
		message.addAllPlayers(world);
		return message;
	}
}
