package enginecrafter77.survivalinc;

import enginecrafter77.survivalinc.client.RenderHUD;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
		RenderHUD.register();
		MinecraftForge.EVENT_BUS.register(RenderHUD.instance);
	}
}