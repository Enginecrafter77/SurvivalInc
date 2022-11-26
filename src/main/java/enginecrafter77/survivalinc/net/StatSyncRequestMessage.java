package enginecrafter77.survivalinc.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * StatSyncRequestMessage is a dummy signaling message
 * used to indicate that the client player has joined
 * the server, and it's ready to receive its server-stored stat data.
 * This message is supposed to be processed by {@link StatSyncRequestHandler}
 * @author Enginecrafter77
 */
public class StatSyncRequestMessage implements IMessage {

	public StatSyncRequestMessage() {}
	
	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

}
