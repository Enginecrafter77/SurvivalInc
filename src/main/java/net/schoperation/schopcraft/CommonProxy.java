package net.schoperation.schopcraft;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.schoperation.schopcraft.cap.CapEvents;
import net.schoperation.schopcraft.cap.CapabilityHandler;
import net.schoperation.schopcraft.cap.ghost.Ghost;
import net.schoperation.schopcraft.cap.ghost.GhostStorage;
import net.schoperation.schopcraft.cap.ghost.IGhost;
import net.schoperation.schopcraft.cap.sanity.ISanity;
import net.schoperation.schopcraft.cap.sanity.Sanity;
import net.schoperation.schopcraft.cap.sanity.SanityStorage;
import net.schoperation.schopcraft.cap.temperature.ITemperature;
import net.schoperation.schopcraft.cap.temperature.Temperature;
import net.schoperation.schopcraft.cap.temperature.TemperatureStorage;
import net.schoperation.schopcraft.cap.thirst.IThirst;
import net.schoperation.schopcraft.cap.thirst.Thirst;
import net.schoperation.schopcraft.cap.thirst.ThirstStorage;
import net.schoperation.schopcraft.cap.wetness.IWetness;
import net.schoperation.schopcraft.cap.wetness.Wetness;
import net.schoperation.schopcraft.cap.wetness.WetnessStorage;
import net.schoperation.schopcraft.packet.SchopPackets;
import net.schoperation.schopcraft.season.modifier.BiomeTempController;
import net.schoperation.schopcraft.season.WorldSeason;
import net.schoperation.schopcraft.tweak.ServerCommands;
import net.schoperation.schopcraft.tweak.TweakEvents;
import net.schoperation.schopcraft.util.WorldDataMgr;
import net.schoperation.schopcraft.util.Registererer;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		
		// Register all new items and blocks.
		MinecraftForge.EVENT_BUS.register(new Registererer());
		
		// Register capabilities.
		CapabilityManager.INSTANCE.register(IWetness.class, new WetnessStorage(), Wetness::new);
		CapabilityManager.INSTANCE.register(IThirst.class, new ThirstStorage(), Thirst::new);
		CapabilityManager.INSTANCE.register(ISanity.class, new SanityStorage(), Sanity::new);
		CapabilityManager.INSTANCE.register(ITemperature.class, new TemperatureStorage(), Temperature::new);
		CapabilityManager.INSTANCE.register(IGhost.class, new GhostStorage(), Ghost::new);
	}
	
	public void init(FMLInitializationEvent event) {
		
		// Register event handlers.
		MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
		MinecraftForge.EVENT_BUS.register(new CapEvents());
		MinecraftForge.EVENT_BUS.register(new TweakEvents());
		MinecraftForge.EVENT_BUS.register(new WorldSeason());
		
		// Register network packets.
		SchopPackets.initPackets();
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	public void serverStarted(FMLServerStartedEvent event) {
		
		// Fire some simple commands on the server before players log on.
		ServerCommands.fireCommandsOnStartup();
		
		// Grab initial biome temperatures.
		BiomeTempController biomeTemp = new BiomeTempController();
		biomeTemp.storeOriginalTemperatures();
		biomeTemp = null;
				
		// Load world data from file.
		WorldDataMgr.loadFromDisk();
	}
}