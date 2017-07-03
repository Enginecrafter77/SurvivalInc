package net.schoperation.schopcraft;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.schoperation.schopcraft.cap.CapEvents;
import net.schoperation.schopcraft.cap.CapabilityHandler;
import net.schoperation.schopcraft.cap.thirst.IThirst;
import net.schoperation.schopcraft.cap.thirst.Thirst;
import net.schoperation.schopcraft.cap.thirst.ThirstStorage;
import net.schoperation.schopcraft.cap.wetness.IWetness;
import net.schoperation.schopcraft.cap.wetness.Wetness;
import net.schoperation.schopcraft.cap.wetness.WetnessStorage;
import net.schoperation.schopcraft.packet.SchopPackets;
import net.schoperation.schopcraft.util.Registererer;
import net.schoperation.schopcraft.util.SchopServerParticles;
import net.schoperation.schopcraft.util.SchopServerSounds;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		
		// register all new items + blocks here
		MinecraftForge.EVENT_BUS.register(new Registererer());
		
		// register capabilities (mainly the new stats)
		CapabilityManager.INSTANCE.register(IWetness.class, new WetnessStorage(), Wetness.class);
		CapabilityManager.INSTANCE.register(IThirst.class, new ThirstStorage(), Thirst.class);
		
		// register event handlers
		MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
		MinecraftForge.EVENT_BUS.register(new CapEvents());
		MinecraftForge.EVENT_BUS.register(new SchopServerParticles());
		MinecraftForge.EVENT_BUS.register(new SchopServerSounds());
		
	}
	
	public void init(FMLInitializationEvent event) {
		
		// register recipes here
		
		
		// register packets here
		SchopPackets.initPackets();
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
		

	}

}
