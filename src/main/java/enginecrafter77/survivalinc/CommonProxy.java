package enginecrafter77.survivalinc;

import enginecrafter77.survivalinc.cap.CapEvents;
import enginecrafter77.survivalinc.cap.CapabilityHandler;
import enginecrafter77.survivalinc.cap.ghost.Ghost;
import enginecrafter77.survivalinc.cap.ghost.GhostStorage;
import enginecrafter77.survivalinc.cap.ghost.IGhost;
import enginecrafter77.survivalinc.cap.wetness.IWetness;
import enginecrafter77.survivalinc.cap.wetness.Wetness;
import enginecrafter77.survivalinc.cap.wetness.WetnessStorage;
import enginecrafter77.survivalinc.net.ConfigPacket;
import enginecrafter77.survivalinc.net.HUDRenderPacket;
import enginecrafter77.survivalinc.net.SeasonPacket;
import enginecrafter77.survivalinc.net.SummonInfoPacket;
import enginecrafter77.survivalinc.season.BiomeTempController;
import enginecrafter77.survivalinc.season.WorldSeason;
import enginecrafter77.survivalinc.stats.StatManager;
import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.util.Registererer;
import enginecrafter77.survivalinc.util.ServerCommands;
import enginecrafter77.survivalinc.util.TweakEvents;
import enginecrafter77.survivalinc.util.WorldDataMgr;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

	public static SimpleNetworkWrapper net;
	
	public void preInit(FMLPreInitializationEvent event)
	{
		// Register all new items and blocks.
		MinecraftForge.EVENT_BUS.register(new Registererer());

		// Register capabilities.
		CapabilityManager.INSTANCE.register(IWetness.class, new WetnessStorage(), Wetness::new);
		CapabilityManager.INSTANCE.register(StatTracker.class, new StatRegister.Storage(), StatManager::new);
		CapabilityManager.INSTANCE.register(IGhost.class, new GhostStorage(), Ghost::new);
	}

	public void init(FMLInitializationEvent event)
	{
		// Register event handlers.
		MinecraftForge.EVENT_BUS.register(new CapEvents());
		MinecraftForge.EVENT_BUS.register(new TweakEvents());
		MinecraftForge.EVENT_BUS.register(new WorldSeason());
		MinecraftForge.EVENT_BUS.register(StatRegister.class);
		MinecraftForge.EVENT_BUS.register(CapabilityHandler.class);
		
		net = NetworkRegistry.INSTANCE.newSimpleChannel(SurvivalInc.MOD_ID);
		net.registerMessage(HUDRenderPacket.class, HUDRenderPacket.HUDRenderMessage.class, 0, Side.CLIENT);
		net.registerMessage(SummonInfoPacket.class, SummonInfoPacket.SummonInfoMessage.class, 1, Side.SERVER);
		net.registerMessage(ConfigPacket.class, ConfigPacket.ConfigMessage.class, 2, Side.CLIENT);
		net.registerMessage(SeasonPacket.class, SeasonPacket.SeasonMessage.class, 3, Side.SERVER);
	}
	
	public void postInit(FMLPostInitializationEvent event) {}

	public void serverStarted(FMLServerStartedEvent event)
	{
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