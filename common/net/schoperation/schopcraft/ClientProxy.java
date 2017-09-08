package net.schoperation.schopcraft;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.schoperation.schopcraft.gui.GuiRenderBar;
import net.schoperation.schopcraft.util.client.ModelRegisterer;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		
		super.preInit(event);
		
		// Register models.
		MinecraftForge.EVENT_BUS.register(new ModelRegisterer());
		
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		
		super.init(event);
		
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
		super.postInit(event);
		
		// Render stat bars.
		MinecraftForge.EVENT_BUS.register(new GuiRenderBar());
	}
}