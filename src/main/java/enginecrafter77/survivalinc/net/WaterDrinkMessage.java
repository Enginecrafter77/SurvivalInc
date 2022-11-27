package enginecrafter77.survivalinc.net;

import enginecrafter77.survivalinc.stats.impl.WaterVolume;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class WaterDrinkMessage implements IMessage {
	
	private WaterVolume drinkvolume;
	private BlockPos block;
	private Vec3d hit;
	private byte hand;
	
	public WaterDrinkMessage(WaterVolume drinkvolume, BlockPos block, Vec3d hit, EnumHand hand)
	{
		this.drinkvolume = drinkvolume;
		this.hand = (byte)hand.ordinal();
		this.block = block;
		this.hit = hit;
	}
	
	public WaterDrinkMessage(WaterVolume drinkvolume, RayTraceResult raytrace, EnumHand hand)
	{
		this(drinkvolume, raytrace.getBlockPos(), raytrace.hitVec, hand);
	}
	
	public WaterDrinkMessage()
	{
		this.drinkvolume = null;
		this.block = BlockPos.ORIGIN;
		this.hit = Vec3d.ZERO;
		this.hand = 0;
	}
	
	public EnumHand getHand()
	{
		return EnumHand.values()[hand];
	}
	
	public BlockPos getWaterBlockPosition()
	{
		return this.block;
	}
	
	public Vec3d getHitPosition()
	{
		return this.hit;
	}
	
	public WaterVolume getWaterVolume()
	{
		return this.drinkvolume;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		// DrinkVolume
		this.drinkvolume = WaterVolume.fromNBT(ByteBufUtils.readTag(buf));
		
		// Hit vector
		this.hit = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
		
		// BlockPos
		this.block = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		
		// Hand
		this.hand = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		// DrinkVolume
		ByteBufUtils.writeTag(buf, this.drinkvolume.serializeNBT());
		
		// Hit vector
		buf.writeDouble(this.hit.x);
		buf.writeDouble(this.hit.y);
		buf.writeDouble(this.hit.z);
		
		// BlockPos
		buf.writeInt(this.block.getX());
		buf.writeInt(this.block.getY());
		buf.writeInt(this.block.getZ());
		
		// Hand
		buf.writeByte(this.hand);
	}

}
