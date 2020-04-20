package enginecrafter77.survivalinc;

import enginecrafter77.survivalinc.client.StatUpdateMessageHandler;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.ghost.GhostCommand;
import enginecrafter77.survivalinc.ghost.GhostImpl;
import enginecrafter77.survivalinc.ghost.GhostProvider;
import enginecrafter77.survivalinc.net.EntityItemUpdateMessage;
import enginecrafter77.survivalinc.net.EntityItemUpdater;
import enginecrafter77.survivalinc.net.StatUpdateMessage;
import enginecrafter77.survivalinc.season.Season;
import enginecrafter77.survivalinc.season.SeasonCommand;
import enginecrafter77.survivalinc.season.SeasonController;
import enginecrafter77.survivalinc.season.SeasonData;
import enginecrafter77.survivalinc.stats.StatCommand;
import enginecrafter77.survivalinc.stats.StatManager;
import enginecrafter77.survivalinc.stats.StatStorage;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.impl.DefaultStats;
import enginecrafter77.survivalinc.stats.impl.HeatModifier;
import enginecrafter77.survivalinc.stats.impl.HydrationModifier;
import enginecrafter77.survivalinc.stats.impl.SanityModifier;
import enginecrafter77.survivalinc.stats.impl.WetnessModifier;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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
		// Register seasons if enabled
		if(ModConfig.SEASONS.enabled) MinecraftForge.EVENT_BUS.register(SeasonController.class);
		
		// Register capabilities.
		CapabilityManager.INSTANCE.register(StatTracker.class, StatStorage.instance, StatManager::new);
		if(ModConfig.GHOST.enabled) GhostProvider.register();
	}

	public void init(FMLInitializationEvent event)
	{
		this.net = NetworkRegistry.INSTANCE.newSimpleChannel(SurvivalInc.MOD_ID);
		this.net.registerMessage(StatUpdateMessageHandler.class, StatUpdateMessage.class, 0, Side.CLIENT);
		this.net.registerMessage(SeasonController.class, SeasonData.class, 1, Side.CLIENT);
		this.net.registerMessage(EntityItemUpdater.class, EntityItemUpdateMessage.class, 2, Side.CLIENT);
		
		if(ModConfig.HEAT.enabled)
		{
			StatManager.providers.add(HeatModifier.instance);
			HeatModifier.instance.init();
		}
		
		if(ModConfig.HYDRATION.enabled)
		{
			StatManager.providers.add(DefaultStats.HYDRATION);
			MinecraftForge.EVENT_BUS.register(HydrationModifier.class);
			HydrationModifier.init();
		}
		
		if(ModConfig.SANITY.enabled)
		{
			StatManager.providers.add(DefaultStats.SANITY);
			MinecraftForge.EVENT_BUS.register(SanityModifier.class);
			SanityModifier.init();
		}
		
		if(ModConfig.WETNESS.enabled)
		{
			StatManager.providers.add(DefaultStats.WETNESS);
			MinecraftForge.EVENT_BUS.register(WetnessModifier.class);
			WetnessModifier.init();
		}
		
		if(ModConfig.GHOST.enabled)
		{
			MinecraftForge.EVENT_BUS.register(GhostImpl.class);
		}
		
		if(ModConfig.SEASONS.enabled) Season.initSeasons();
	}
	
	public void postInit(FMLPostInitializationEvent event) {}

	public void serverStarting(FMLServerStartingEvent event)
	{
		MinecraftServer server = event.getServer();
		CommandHandler manager = (CommandHandler)server.getCommandManager();
		
		if(ModConfig.SEASONS.enabled) manager.registerCommand(new SeasonCommand());
		if(ModConfig.GHOST.enabled) manager.registerCommand(new GhostCommand());
		manager.registerCommand(new StatCommand());
	}
}