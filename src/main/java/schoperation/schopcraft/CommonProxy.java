package schoperation.schopcraft;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import schoperation.schopcraft.cap.CapEvents;
import schoperation.schopcraft.cap.CapabilityHandler;
import schoperation.schopcraft.cap.ghost.Ghost;
import schoperation.schopcraft.cap.ghost.GhostStorage;
import schoperation.schopcraft.cap.ghost.IGhost;
import schoperation.schopcraft.cap.stat.StatManager;
import schoperation.schopcraft.cap.stat.StatRegister;
import schoperation.schopcraft.cap.stat.StatTracker;
import schoperation.schopcraft.cap.wetness.IWetness;
import schoperation.schopcraft.cap.wetness.Wetness;
import schoperation.schopcraft.cap.wetness.WetnessStorage;
import schoperation.schopcraft.packet.ConfigPacket;
import schoperation.schopcraft.packet.HUDRenderPacket;
import schoperation.schopcraft.packet.SeasonPacket;
import schoperation.schopcraft.packet.SummonInfoPacket;
import schoperation.schopcraft.season.WorldSeason;
import schoperation.schopcraft.season.modifier.BiomeTempController;
import schoperation.schopcraft.tweak.ServerCommands;
import schoperation.schopcraft.tweak.TweakEvents;
import schoperation.schopcraft.util.Registererer;
import schoperation.schopcraft.util.WorldDataMgr;

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
		
		net = NetworkRegistry.INSTANCE.newSimpleChannel(SchopCraft.MOD_ID);
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