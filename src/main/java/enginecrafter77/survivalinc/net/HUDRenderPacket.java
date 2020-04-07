package enginecrafter77.survivalinc.net;

import enginecrafter77.survivalinc.client.RenderHUD;
import enginecrafter77.survivalinc.net.HUDRenderPacket.HUDRenderMessage;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HUDRenderPacket implements IMessageHandler<HUDRenderMessage, IMessage> {

	@Override
	public IMessage onMessage(HUDRenderMessage message, MessageContext ctx)
	{
		if (ctx.side.isClient())
		{

			// Temperature
			float temperature = message.temperature;
			float maxTemperature = message.maxTemperature;

			// Thirst
			float thirst = message.thirst;
			float maxThirst = message.maxThirst;

			// Sanity
			float sanity = message.sanity;
			float maxSanity = message.maxSanity;

			// Wetness
			float wetness = message.wetness;
			float maxWetness = message.maxWetness;

			// Ghost Stats
			boolean isGhost = message.isGhost;
			float ghostEnergy = message.ghostEnergy;

			RenderHUD.retrieveStats(temperature, maxTemperature, thirst, maxThirst, sanity, maxSanity, wetness,
					maxWetness, isGhost, ghostEnergy);
		}

		return null;
	}

	public static class HUDRenderMessage implements IMessage {

		// Variables sent to the client for rendering.
		// Temperature
		private float temperature;
		private float maxTemperature;

		// Thirst
		private float thirst;
		private float maxThirst;

		// Sanity
		private float sanity;
		private float maxSanity;

		// Wetness
		private float wetness;
		private float maxWetness;

		// Ghost Stats
		private boolean isGhost;
		private float ghostEnergy;

		// Necessary constructor.
		public HUDRenderMessage()
		{}

		public HUDRenderMessage(float temperature, float maxTemperature, float thirst, float maxThirst, float sanity,
				float maxSanity, float wetness, float maxWetness, boolean isGhost, float ghostEnergy)
		{
			this.temperature = temperature;
			this.maxTemperature = maxTemperature;
			this.thirst = thirst;
			this.maxThirst = maxThirst;
			this.sanity = sanity;
			this.maxSanity = maxSanity;
			this.wetness = wetness;
			this.maxWetness = maxWetness;
			this.isGhost = isGhost;
			this.ghostEnergy = ghostEnergy;
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			this.temperature = buf.readFloat();
			this.maxTemperature = buf.readFloat();
			this.thirst = buf.readFloat();
			this.maxThirst = buf.readFloat();
			this.sanity = buf.readFloat();
			this.maxSanity = buf.readFloat();
			this.wetness = buf.readFloat();
			this.maxWetness = buf.readFloat();
			this.isGhost = buf.readBoolean();
			this.ghostEnergy = buf.readFloat();
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeFloat(temperature);
			buf.writeFloat(maxTemperature);
			buf.writeFloat(thirst);
			buf.writeFloat(maxThirst);
			buf.writeFloat(sanity);
			buf.writeFloat(maxSanity);
			buf.writeFloat(wetness);
			buf.writeFloat(maxWetness);
			buf.writeBoolean(isGhost);
			buf.writeFloat(ghostEnergy);
		}
	}
}