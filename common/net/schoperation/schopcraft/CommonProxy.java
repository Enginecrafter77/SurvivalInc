package net.schoperation.schopcraft;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.schoperation.schopcraft.gui.GuiRenderBar;
import net.schoperation.schopcraft.util.RegAndRen;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		
		// register all new items + blocks here
		RegAndRen.registerAll();
	}
	
	public void init(FMLInitializationEvent event) {
		
		// register recipes here
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
		// render new bars
		MinecraftForge.EVENT_BUS.register(new GuiRenderBar());
		
	}

}
