package enginecrafter77.survivalinc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import enginecrafter77.survivalinc.block.BlockMelting;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.ghost.GhostCommand;
import enginecrafter77.survivalinc.ghost.GhostProvider;
import enginecrafter77.survivalinc.net.StatSyncRequestHandler;
import enginecrafter77.survivalinc.net.StatSyncRequestMessage;
import enginecrafter77.survivalinc.net.WaterDrinkMessage;
import enginecrafter77.survivalinc.season.SeasonCalendar;
import enginecrafter77.survivalinc.season.SeasonCommand;
import enginecrafter77.survivalinc.season.SeasonController;
import enginecrafter77.survivalinc.season.SeasonSyncRequest;
import enginecrafter77.survivalinc.season.SurvivalIncSeason;
import enginecrafter77.survivalinc.season.melting.MeltingController;
import enginecrafter77.survivalinc.season.melting.MeltingController.MelterEntry;
import enginecrafter77.survivalinc.stats.StatCapability;
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
import enginecrafter77.survivalinc.stats.impl.armor.ArmorConductivityCommand;
import enginecrafter77.survivalinc.util.ExternalResourceProvider;
import enginecrafter77.survivalinc.util.FunctionalImplementation;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
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

public abstract class CommonProxy {
	
	public SimpleNetworkWrapper net;
	public ItemSituationContainer mapper;
	
	public ExternalResourceProvider ism, armor_conductivity;
	
	public void preInit(FMLPreInitializationEvent event)
	{
		// Register seasons if enabled
		if(ModConfig.SEASONS.enabled)
		{
			MinecraftForge.EVENT_BUS.register(SeasonController.instance);
			
			MeltingController.meltmap.add(new MelterEntry((BlockMelting)ModBlocks.MELTING_SNOW.get()).level(1, true)); // 1 = block above ground
			MeltingController.meltmap.add(new MelterEntry((BlockMelting)ModBlocks.MELTING_ICE.get()).level(0, true)); // 0 = ground
		}
		
		// Register the auxiliary event handlers
		MinecraftForge.EVENT_BUS.register(this);
		
		// Register capabilities.
		MinecraftForge.EVENT_BUS.register(StatCapability.class);
		CapabilityManager.INSTANCE.register(StatTracker.class, StatStorage.instance, StatRegisterDispatcher.instance);
		
		this.mapper = new ItemSituationContainer();
		
		File configdir = new File(event.getModConfigurationDirectory(), SurvivalInc.MOD_ID);
		this.ism = new ExternalResourceProvider(new File(configdir, "/item_effects.json"), new ResourceLocation(SurvivalInc.MOD_ID, "configbase/item_effects.json"));
		this.armor_conductivity = new ExternalResourceProvider(new File(configdir, "armor_conductivity.json"), new ResourceLocation(SurvivalInc.MOD_ID, "configbase/armor_conductivity.json"));
	}
	
	/**
	 * A nifty hack method to avoid accessing client-only code from server. Overridden on both client and server. Client
	 * places it's designated handlers as expected, but server registers the message with dummy handlers. This allows
	 * passing client-only handlers to client processed messages.
	 */
	public abstract void registerClientHandlers();
	
	public void init(FMLInitializationEvent event)
	{
		this.net = NetworkRegistry.INSTANCE.newSimpleChannel(SurvivalInc.MOD_ID);
		this.registerClientHandlers(); // Discriminators 0 1 2
		this.net.registerMessage(HydrationModifier::validateMessage, WaterDrinkMessage.class, 3, Side.SERVER);
		this.net.registerMessage(StatSyncRequestHandler.class, StatSyncRequestMessage.class, 4, Side.SERVER);
		this.net.registerMessage(SeasonController::onSyncRequest, SeasonSyncRequest.class, 5, Side.SERVER);
		
		if(ModConfig.HEAT.enabled) HeatModifier.init();
		if(ModConfig.HYDRATION.enabled) HydrationModifier.init();
		if(ModConfig.SANITY.enabled) SanityModifier.init();
		if(ModConfig.WETNESS.enabled) WetnessModifier.init();
		if(ModConfig.GHOST.enabled) GhostProvider.init();
		
		if(ModConfig.SEASONS.enabled)
		{
			SeasonCalendar calendar = SeasonController.instance.calendar;
			for(SurvivalIncSeason season : SurvivalIncSeason.values())
				calendar.registerSeason(season);
		}
	}
	
	public void postInit(FMLPostInitializationEvent event)
	{
		if(ModConfig.SEASONS.enabled && ModConfig.SEASONS.meltController.isValid())
		{
			MeltingController.compile(ModConfig.SEASONS.meltController);
			MinecraftForge.EVENT_BUS.register(MeltingController.class);
		}
		
		// Load the compatibility maps
		if(ModConfig.HEAT.enabled) this.armor_conductivity.load(HeatModifier.instance.armor::load);
		this.ism.load(this::loadItemEffects);
		
		// Extra compatibility maps
		if(ModConfig.HEAT.enabled || ModConfig.WETNESS.enabled) HeatModifier.instance.buildCompatMaps();
	}
	
	public void serverStarting(FMLServerStartingEvent event)
	{
		MinecraftServer server = event.getServer();
		CommandHandler manager = (CommandHandler)server.getCommandManager();
		
		if(ModConfig.SEASONS.enabled) manager.registerCommand(new SeasonCommand());
		if(GhostProvider.loaded()) manager.registerCommand(new GhostCommand());
		if(HeatModifier.loaded()) manager.registerCommand(new ArmorConductivityCommand(HeatModifier.instance.armor));
		manager.registerCommand(new StatCommand());
	}
	
	@SubscribeEvent
	public void addParserSituations(ItemSituationParserSetupEvent event)
	{
		event.parser.addSituationFactory("in-hand", ItemInHandSituation::new);
		event.parser.addSituationFactory("in-inventory", ItemInInvSituation::new);
		event.parser.addSituationFactory("consumed", ItemConsumedSituation::new);
	}
	
	@FunctionalImplementation(of = ExternalResourceProvider.ResourceLoader.class)
	private void loadItemEffects(InputStream input) throws IOException
	{
		ItemSituationParserSetupEvent pse = new ItemSituationParserSetupEvent();
		MinecraftForge.EVENT_BUS.post(pse);
		
		Collection<ItemSituation<?>> effects = pse.getParser().parse(input);
		this.mapper.register(effects);
		
		MinecraftForge.EVENT_BUS.register(this.mapper.createEventHandler());
	}
}
