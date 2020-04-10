package enginecrafter77.survivalinc;

import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.ghost.Ghost;
import enginecrafter77.survivalinc.ghost.GhostCommand;
import enginecrafter77.survivalinc.ghost.GhostImpl;
import enginecrafter77.survivalinc.ghost.GhostStorage;
import enginecrafter77.survivalinc.net.SummonInfoPacket;
import enginecrafter77.survivalinc.season.BiomeTempController;
import enginecrafter77.survivalinc.season.SeasonCommand;
import enginecrafter77.survivalinc.season.SeasonController;
import enginecrafter77.survivalinc.season.SeasonData;
import enginecrafter77.survivalinc.stats.StatManager;
import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

	public SimpleNetworkWrapper net;
	
	public void preInit(FMLPreInitializationEvent event)
	{
		// Register all new items and blocks.
		MinecraftForge.EVENT_BUS.register(ModItems.class);
		MinecraftForge.EVENT_BUS.register(GenericEventHander.class);
		MinecraftForge.EVENT_BUS.register(StatRegister.class);
		// Register seasons if enabled
		if(ModConfig.SEASONS.aenableSeasons) MinecraftForge.EVENT_BUS.register(SeasonController.class);
		
		// Register capabilities.
		CapabilityManager.INSTANCE.register(StatTracker.class, new StatRegister.Storage(), StatManager::new);
		CapabilityManager.INSTANCE.register(Ghost.class, new GhostStorage(), GhostImpl::new);
	}

	public void init(FMLInitializationEvent event)
	{
		this.net = NetworkRegistry.INSTANCE.newSimpleChannel(SurvivalInc.MOD_ID);
		this.net.registerMessage(SummonInfoPacket.class, SummonInfoPacket.SummonInfoMessage.class, 1, Side.SERVER);
		this.net.registerMessage(SeasonController.class, SeasonData.class, 3, Side.CLIENT);
	}
	
	public void postInit(FMLPostInitializationEvent event) {}

	public void serverStarted(FMLServerStartedEvent event)
	{
		BiomeTempController biomeTemp = new BiomeTempController();
		biomeTemp.storeOriginalTemperatures();
	}
	
	public void serverStarting(FMLServerStartingEvent event)
	{
		MinecraftServer server = event.getServer();
		CommandHandler manager = (CommandHandler)server.getCommandManager();
		manager.registerCommand(new SeasonCommand());
		manager.registerCommand(new GhostCommand());
	}
}