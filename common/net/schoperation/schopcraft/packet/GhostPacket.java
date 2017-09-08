package net.schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.schoperation.schopcraft.gui.GuiRenderBar;
import net.schoperation.schopcraft.packet.GhostPacket.GhostMessage;

public class GhostPacket implements IMessageHandler<GhostMessage, IMessage> {
	
	@Override
	public IMessage onMessage(GhostMessage message, MessageContext ctx) {
		
		if (ctx.side.isClient()) {
			
			String uuid = message.uuid;
			boolean isGhost = message.isGhost;
			float ghostEnergy = message.ghostEnergy;
			GuiRenderBar.getServerGhostStats(isGhost, ghostEnergy);
		}
		else {
			
			/*
			String uuid = message.uuid;
			float wetness = message.wetness;
			float maxWetness = message.maxWetness;
			float minWetness = message.minWetness;
			WetnessModifier.getClientChange(uuid, wetness, maxWetness, minWetness);
			*/
		}
		
		return null;
	}
	
	public static class GhostMessage implements IMessage {
		
		// Variables used in the packet.
		private String uuid;
		private boolean isGhost;
		private float ghostEnergy;
		
		// Necessary constructor.
		public GhostMessage() {}
		
		public GhostMessage(String uuid, boolean isGhost, float ghostEnergy) {
			
			this.uuid = uuid;
			this.isGhost = isGhost;
			this.ghostEnergy = ghostEnergy;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			
			this.uuid = ByteBufUtils.readUTF8String(buf);
			this.isGhost = buf.readBoolean();
			this.ghostEnergy = buf.readFloat();
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			
			ByteBufUtils.writeUTF8String(buf, uuid);
			buf.writeBoolean(isGhost);
			buf.writeFloat(ghostEnergy);
		}
	}
}