package enginecrafter77.survivalinc.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class WaterDrinkMessage implements IMessage {
	
	private byte hand;
	
	public WaterDrinkMessage(EnumHand hand)
	{
		this.hand = (byte)hand.ordinal();
	}
	
	public WaterDrinkMessage()
	{
		this.hand = 0;
	}
	
	public EnumHand getHand()
	{
		return EnumHand.values()[hand];
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.hand = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(hand);
	}

}
