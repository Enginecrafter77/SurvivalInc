package net.schoperation.schopcraft;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.schoperation.schopcraft.cap.wetness.IWetness;
import net.schoperation.schopcraft.cap.wetness.Wetness;
import net.schoperation.schopcraft.cap.wetness.WetnessStorage;
import net.schoperation.schopcraft.gui.GuiRenderBar;
import net.schoperation.schopcraft.packet.SchopPackets;
import net.schoperation.schopcraft.util.RegAndRen;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		
		super.preInit(event);
		
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		
		super.init(event);
		
		// render all new items + blocks here
		RegAndRen.renderAll();
		
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
		super.postInit(event);
		
		// render new bars
		MinecraftForge.EVENT_BUS.register(new GuiRenderBar());
	}

}
