package enginecrafter77.survivalinc;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

import javax.annotation.Nullable;

public class ServerProxy implements SurvivalIncProxy {
	@Override
	public void registerRendering()
	{
		//NOOP
	}

	@Override
	public void createHUD()
	{
		//NOOP
	}

	@Nullable
	@Override
	public Object getAuxiliaryEventHandler()
	{
		return null;
	}

	@Override
	public <MSG extends IMessage> IMessageHandler<MSG, ? extends IMessage> createSidedMessageHandler(Class<MSG> messageClass)
	{
		return SurvivalIncProxy.getNoopMessageHandler();
	}
}
