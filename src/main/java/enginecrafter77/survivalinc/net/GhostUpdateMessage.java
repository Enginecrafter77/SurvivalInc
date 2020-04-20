package enginecrafter77.survivalinc.net;

import java.util.UUID;

import enginecrafter77.survivalinc.ghost.GhostProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class GhostUpdateMessage implements IMessage {
	
	public UUID player;
	public boolean status;
	
	public GhostUpdateMessage(EntityPlayer player, boolean value)
	{
		this.player = player.getUniqueID();
		this.status = value;
	}
	
	public GhostUpdateMessage(EntityPlayer player)
	{
		this(player, player.getCapability(GhostProvider.target, null).getStatus());
	}
	
	public GhostUpdateMessage()
	{
		this.status = false;
		this.player = null;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.player = UUID.fromString(ByteBufUtils.readUTF8String(buf));
		this.status = buf.readBoolean();
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, player.toString());
		buf.writeBoolean(status);
	}
	
}
