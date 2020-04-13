package enginecrafter77.survivalinc.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * A packet designed to notify client-side item entities
 * that their remote represntation's stored item stack
 * was updated.
 * @author Enginecrafter77
 */
public class EntityItemUpdateMessage implements IMessage
{
	/** The item stack of the entity */
	protected ItemStack stack;
	protected int entityid;
	
	/**
	 * Constructs the packet to update the target entity.
	 * @param entity The {@link EntityItem} to be updated
	 */
	public EntityItemUpdateMessage(EntityItem entity)
	{
		this.entityid = entity.getEntityId();
		this.stack = entity.getItem();
	}
	
	public EntityItemUpdateMessage() {}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.entityid = buf.readInt(); 
		this.stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(entityid);
		ByteBufUtils.writeItemStack(buf, stack);
	}
}