package enginecrafter77.survivalinc.season;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * An universal transporter of an infrmation
 * that the season data has updated. This class
 * is designed to be used as event, as well as
 * being used as message for processing on Client
 * side.
 * @author Enginecrafter77
 */
public class SeasonUpdateEvent extends WorldEvent implements IMessage {

	public final SeasonData data;
	
	public SeasonUpdateEvent(World world, SeasonData data)
	{
		super(world);
		this.data = data;
	}
	
	public SeasonUpdateEvent(World world)
	{
		this(world, SeasonData.load(world));
	}
	
	@SideOnly(Side.CLIENT)
	public SeasonUpdateEvent()
	{
		// Yes this is absolutely intentional, as it will be only ever invoked on client side
		super(Minecraft.getMinecraft().world);
		this.data = new SeasonData();
	}
	
	/**
	 * @return True if the season was changed, or false if only the day has been advanced. 
	 */
	public boolean hasSeasonAdvanced()
	{
		return data.day == 0;
	}
	
	public Season getSeason()
	{
		return data.season;
	}
	
	public int getDay()
	{
		return data.day;
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
		data.writeToNBT(tag);
		ByteBufUtils.writeTag(buf, tag);
	}
}
