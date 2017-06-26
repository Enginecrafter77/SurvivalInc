package net.schoperation.schopcraft;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.schoperation.schopcraft.gui.GuiRenderBar;
import net.schoperation.schopcraft.util.RegAndRen;
import net.schoperation.schopcraft.wetness.IWetness;
import net.schoperation.schopcraft.wetness.Wetness;
import net.schoperation.schopcraft.wetness.WetnessStorage;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		
		// register all new items + blocks here
		RegAndRen.registerAll();
		
		// register capabilities (mainly the new stats)
		CapabilityManager.INSTANCE.register(IWetness.class, new WetnessStorage(), Wetness.class);
	}
	
	public void init(FMLInitializationEvent event) {
		
		// register recipes here
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
		// render new bars
		MinecraftForge.EVENT_BUS.register(new GuiRenderBar());

	}

}
