package net.schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.schoperation.schopcraft.gui.GuiRenderBar;
import net.schoperation.schopcraft.packet.StatsPacket.StatsMessage;

public class StatsPacket implements IMessageHandler<StatsMessage, IMessage> {
	
	@Override
	public IMessage onMessage(StatsMessage message, MessageContext ctx) {
		
		if(ctx.side.isClient()) {
			
			float wetness = message.wetness;
			System.out.println("the packet has reached onMessage(), client side!");
			GuiRenderBar.getServerWetness(wetness);
			
		}
		
		return null;
	}
	
	public static class StatsMessage implements IMessage {
		
		// actual variables to be used and sent and crap
		private float wetness;
		
		// dumb constructor WHY FML
		public StatsMessage() {}
		
		public StatsMessage(float wetness) {
			
			this.wetness = wetness;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			
			this.wetness = buf.readFloat();
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			
			buf.writeFloat(wetness);
		}
	}
}
