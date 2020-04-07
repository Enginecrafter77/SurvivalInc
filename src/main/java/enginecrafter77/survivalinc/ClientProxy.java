package enginecrafter77.survivalinc;

import enginecrafter77.survivalinc.client.ModelRegisterer;
import enginecrafter77.survivalinc.client.RenderHUD;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(new ModelRegisterer());
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new RenderHUD());
	}
}