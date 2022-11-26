package enginecrafter77.survivalinc.net;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.ghost.GhostEnergyRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

@Deprecated
public class GhostUpdateMessage implements IMessage {
	
	public GhostEnergyRecord record;
	public UUID player;
		
	public GhostUpdateMessage(EntityPlayer player)
	{		
		StatTracker tracker = player.getCapability(StatCapability.target, null);
		this.record = tracker.getRecord(SurvivalInc.ghost);
		this.player = player.getUniqueID();
	}
	
	@SideOnly(Side.CLIENT)
	public GhostUpdateMessage()
	{
		this.record = new GhostEnergyRecord();
		this.player = null;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.player = UUID.fromString(ByteBufUtils.readUTF8String(buf));
		this.record.deserializeNBT(ByteBufUtils.readTag(buf));
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, player.toString());
		ByteBufUtils.writeTag(buf, this.record.serializeNBT());
	}
	
}
