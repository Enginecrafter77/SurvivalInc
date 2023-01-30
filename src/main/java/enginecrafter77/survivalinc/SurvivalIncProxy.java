package enginecrafter77.survivalinc;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public interface SurvivalIncProxy {
	public abstract void registerRendering();

	public abstract void createHUD();

	@Nullable
	public Object getAuxiliaryEventHandler();

	public <MSG extends IMessage> IMessageHandler<MSG, ? extends IMessage> createSidedMessageHandler(Class<MSG> messageClass);

	public static <MSG extends IMessage> IMessageHandler<MSG, IMessage> getNoopMessageHandler()
	{
		return SurvivalIncProxy::noopMessageHandler;
	}

	@Nullable
	public static <MSG extends IMessage> IMessage noopMessageHandler(MSG message, MessageContext ctx)
	{
		SurvivalInc.logger.warn("No-Op message handler intercepted message. This is not supposed to happen!");
		return null;
	}

	public static class MessageHandlerAdapter<SMSG extends IMessage, DMSG extends IMessage>
	{
		public final Class<DMSG> messageClass;

		public final IMessageHandler<DMSG, ?> wrappedHandler;

		public MessageHandlerAdapter(IMessageHandler<DMSG, ?> handler, Class<DMSG> messageClass)
		{
			this.messageClass = messageClass;
			this.wrappedHandler = handler;
		}

		private IMessage delegateMessage(SMSG message, MessageContext ctx)
		{
			if(!this.messageClass.isInstance(message))
				throw new ClassCastException(String.format("MessageHandlerAdapter(%1$s) cannot process %2$s, as it is not subclass of %1$s.", this.messageClass, message.getClass()));
			DMSG castMessage = this.messageClass.cast(message);
			return this.wrappedHandler.onMessage(castMessage, ctx);
		}

		public IMessageHandler<SMSG, IMessage> createHandler()
		{
			return this::delegateMessage;
		}

		public static <SMSG extends IMessage, DMSG extends IMessage> IMessageHandler<SMSG, IMessage> wrap(IMessageHandler<DMSG, IMessage> wrapped, Class<DMSG> messageClass)
		{
			return new MessageHandlerAdapter<SMSG, DMSG>(wrapped, messageClass).createHandler();
		}
	}
}
