package enginecrafter77.survivalinc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.Collection;

import enginecrafter77.survivalinc.block.BlockMelting;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.ghost.GhostCommand;
import enginecrafter77.survivalinc.ghost.GhostProvider;
import enginecrafter77.survivalinc.net.EntityItemUpdateMessage;
import enginecrafter77.survivalinc.net.EntityItemUpdater;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import enginecrafter77.survivalinc.net.StatSyncRequestHandler;
import enginecrafter77.survivalinc.net.StatSyncRequestMessage;
import enginecrafter77.survivalinc.net.StatSyncHandler;
import enginecrafter77.survivalinc.net.WaterDrinkMessage;
import enginecrafter77.survivalinc.season.SeasonCalendar;
import enginecrafter77.survivalinc.season.SeasonCommand;
import enginecrafter77.survivalinc.season.SeasonController;
import enginecrafter77.survivalinc.season.SeasonSyncMessage;
import enginecrafter77.survivalinc.season.SurvivalIncSeason;
import enginecrafter77.survivalinc.season.melting.MeltingController;
import enginecrafter77.survivalinc.season.melting.MeltingController.MelterEntry;
import enginecrafter77.survivalinc.stats.StatCommand;
import enginecrafter77.survivalinc.stats.StatRegisterDispatcher;
import enginecrafter77.survivalinc.stats.StatStorage;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.effect.item.ItemConsumedSituation;
import enginecrafter77.survivalinc.stats.effect.item.ItemInHandSituation;
import enginecrafter77.survivalinc.stats.effect.item.ItemInInvSituation;
import enginecrafter77.survivalinc.stats.effect.item.ItemSituation;
import enginecrafter77.survivalinc.stats.effect.item.ItemSituationContainer;
import enginecrafter77.survivalinc.stats.effect.item.ItemSituationParserSetupEvent;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {
	
	public SimpleNetworkWrapper net;
	public ItemSituationContainer mapper;
	
	public File itemeffects;
	
	public void preInit(FMLPreInitializationEvent event)
	{
		// Register seasons if enabled
		if(ModConfig.SEASONS.enabled)
		{
			MinecraftForge.EVENT_BUS.register(SeasonController.instance);
			
			MeltingController.meltmap.add(new MelterEntry((BlockMelting)ModBlocks.MELTING_SNOW.get()).level(1, true)); // 1 = block above ground
			MeltingController.meltmap.add(new MelterEntry((BlockMelting)ModBlocks.MELTING_ICE.get()).level(0, true)); // 0 = ground
		}
		
		// Register capabilities.
		CapabilityManager.INSTANCE.register(StatTracker.class, StatStorage.instance, StatRegisterDispatcher.instance);
		
		this.mapper = new ItemSituationContainer();		
		MinecraftForge.EVENT_BUS.register(this.mapper);
		MinecraftForge.EVENT_BUS.register(this);
		
		this.itemeffects = new File(event.getModConfigurationDirectory(), "survivalinc/item_effects.json");
	}

	public void init(FMLInitializationEvent event)
	{
		this.net = NetworkRegistry.INSTANCE.newSimpleChannel(SurvivalInc.MOD_ID);
		this.net.registerMessage(StatSyncHandler.class, StatSyncMessage.class, 0, Side.CLIENT);
		this.net.registerMessage(SeasonController.instance, SeasonSyncMessage.class, 1, Side.CLIENT);
		this.net.registerMessage(EntityItemUpdater.class, EntityItemUpdateMessage.class, 2, Side.CLIENT);
		this.net.registerMessage(HydrationModifier.class, WaterDrinkMessage.class, 3, Side.SERVER);
		this.net.registerMessage(StatSyncRequestHandler.class, StatSyncRequestMessage.class, 4, Side.SERVER);
		
		if(ModConfig.HEAT.enabled) HeatModifier.instance.init();
		if(ModConfig.HYDRATION.enabled) HydrationModifier.instance.init();
		if(ModConfig.SANITY.enabled) SanityModifier.instance.init();
		if(ModConfig.WETNESS.enabled) WetnessModifier.instance.init();
		if(ModConfig.GHOST.enabled) GhostProvider.instance.init();
		
		SeasonCalendar calendar = SeasonController.instance.calendar;
		for(SurvivalIncSeason season : SurvivalIncSeason.values())
			calendar.registerSeason(season);
	}
	
	public void postInit(FMLPostInitializationEvent event)
	{
		if(ModConfig.SEASONS.enabled && ModConfig.SEASONS.meltController.isValid())
		{
			MeltingController.compile(ModConfig.SEASONS.meltController);
			MinecraftForge.EVENT_BUS.register(MeltingController.class);
		}
		
		// Load the compatibility maps
		try
		{
			if(!this.itemeffects.exists())
			{
				try
				{
					Files.copy(SurvivalInc.class.getResourceAsStream("/assets/survivalinc/configbase/item_effects.json"), this.itemeffects.toPath());
				}
				catch(FileAlreadyExistsException exc)
				{
					// Do nothing
				}
			}
			
			ItemSituationParserSetupEvent pse = new ItemSituationParserSetupEvent();
			MinecraftForge.EVENT_BUS.post(pse);
			
			InputStream input = new FileInputStream(this.itemeffects);
			Collection<ItemSituation<?>> effects = pse.getParser().parse(input);
			this.mapper.effects.addAll(effects);
			input.close();
		}
		catch(IOException exc)
		{
			throw new RuntimeException("Failed to load ISM JSON.");
		}
		
		// Extra compatibility maps
		if(ModConfig.HEAT.enabled || ModConfig.WETNESS.enabled) HeatModifier.instance.buildCompatMaps();
	}
	
	public void serverStarting(FMLServerStartingEvent event)
	{
		MinecraftServer server = event.getServer();
		CommandHandler manager = (CommandHandler)server.getCommandManager();
		
		if(ModConfig.SEASONS.enabled) manager.registerCommand(new SeasonCommand());
		if(ModConfig.GHOST.enabled) manager.registerCommand(new GhostCommand());
		manager.registerCommand(new StatCommand());
	}
	
	@SubscribeEvent
	public void addParserSituations(ItemSituationParserSetupEvent event)
	{
		event.parser.addSituationFactory("in-hand", ItemInHandSituation::new);
		event.parser.addSituationFactory("in-inventory", ItemInInvSituation::new);
		event.parser.addSituationFactory("consumed", ItemConsumedSituation::new);
	}
}