package enginecrafter77.survivalinc.net;

import java.util.Map;
import java.util.UUID;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StatSyncHandler implements IMessageHandler<StatSyncMessage, IMessage>{
	
	@Override
	public IMessage onMessage(StatSyncMessage message, MessageContext ctx)
	{
		WorldClient world = Minecraft.getMinecraft().world;
		if(world == null) SurvivalInc.logger.error("Client world is not initialized yet. Dropping stat sync message.");
		else
		{
			this.load(message, world);
			SurvivalInc.logger.info("Stat sync received: " + message.toString());
		}
		return null;
	}
	
	public void load(StatSyncMessage message, WorldClient world)
	{
		IStorage<StatTracker> serializer = StatCapability.target.getStorage();
		for(Map.Entry<UUID, NBTTagCompound> entry : message.getDataSet())
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
	
}
