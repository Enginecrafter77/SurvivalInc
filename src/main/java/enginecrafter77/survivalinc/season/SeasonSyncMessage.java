package enginecrafter77.survivalinc.season;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SeasonSyncMessage implements IMessage {
	public final SeasonData data;
	
	public SeasonSyncMessage(SeasonData data)
	{
		this.data = data;
	}
	
	@SideOnly(Side.CLIENT)
	public SeasonSyncMessage()
	{
		this.data = new SeasonData();
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		data.readFromNBT(tag);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag = data.writeToNBT(tag);
		ByteBufUtils.writeTag(buf, tag);
	}
	
}
