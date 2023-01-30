package enginecrafter77.survivalinc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import enginecrafter77.survivalinc.block.BlockMelting;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.ghost.GhostCommand;
import enginecrafter77.survivalinc.ghost.GhostProvider;
import enginecrafter77.survivalinc.net.*;
import enginecrafter77.survivalinc.season.*;
import enginecrafter77.survivalinc.season.calendar.SeasonCalendar;
import enginecrafter77.survivalinc.season.calendar.SeasonCalendarConstructEvent;
import enginecrafter77.survivalinc.season.calendar.SimpleSeasonCalendar;
import enginecrafter77.survivalinc.season.melting.MelterSetupEvent;
import enginecrafter77.survivalinc.season.melting.MeltingController;
import enginecrafter77.survivalinc.stats.*;
import enginecrafter77.survivalinc.stats.effect.item.*;
import enginecrafter77.survivalinc.stats.impl.HeatModifier;
import enginecrafter77.survivalinc.stats.impl.HydrationModifier;
import enginecrafter77.survivalinc.stats.impl.SanityModifier;
import enginecrafter77.survivalinc.stats.impl.WetnessModifier;
import enginecrafter77.survivalinc.stats.impl.armor.ArmorConductivityCommand;
import enginecrafter77.survivalinc.util.ExportedResource;
import enginecrafter77.survivalinc.util.FunctionalImplementation;
import enginecrafter77.survivalinc.util.RadiantHeatScanner;
import enginecrafter77.survivalinc.util.blockprop.BlockPropertyJsonParser;
import enginecrafter77.survivalinc.util.blockprop.MutableBlockProperties;
import net.minecraft.command.CommandHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeEnd;
import net.minecraft.world.biome.BiomeHell;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

@Mod(modid = SurvivalInc.MOD_ID)
public final class SurvivalInc {
	
	// Basic mod constants.
	public static final String MOD_ID = "survivalinc";
	
	// Make an instance of the mod.
	@Instance(SurvivalInc.MOD_ID)
	public static SurvivalInc instance;
	
	// Logger
	public static Logger logger;
	
	// Create proxies to load stuff correctly.
	@SidedProxy(clientSide = "enginecrafter77.survivalinc.ClientProxy", serverSide = "enginecrafter77.survivalinc.ServerProxy")
	public static SurvivalIncProxy proxy;

	public static RadiantHeatScanner heatScanner;

	@Nullable public static HeatModifier heat;
	@Nullable public static SanityModifier sanity;
	@Nullable public static WetnessModifier wetness;
	@Nullable public static HydrationModifier hydration;
	@Nullable public static GhostProvider ghost;

	public static SimpleNetworkWrapper net;
	public static ItemSituationContainer mapper;

	public static SeasonCalendar seasonCalendar;
	public static SeasonController seasonController;
	public static MeltingController meltingController;

	public static ExportedResource itemEffectConfig, armorConductivityConfig;

	public static ExportedResource blockPropertiesConfig;
	public static MutableBlockProperties blockProperties;

	// Create tab for creative mode.
	public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(SurvivalInc.MOD_ID + ":mainTab") {
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(ModItems.CANTEEN.getItem());
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		File configDir = new File(event.getModConfigurationDirectory(), SurvivalInc.MOD_ID);

		SurvivalInc.logger = event.getModLog();
		SurvivalInc.blockProperties = new MutableBlockProperties();
		SurvivalInc.heatScanner = new RadiantHeatScanner(SurvivalInc.blockProperties.view("heat", Float.class));
		SurvivalInc.mapper = new ItemSituationContainer();
		SurvivalInc.itemEffectConfig = new ExportedResource(new File(configDir, "item_effects.json"), new ResourceLocation(SurvivalInc.MOD_ID, "configbase/item_effects.json"));
		SurvivalInc.armorConductivityConfig = new ExportedResource(new File(configDir, "armor_conductivity.json"), new ResourceLocation(SurvivalInc.MOD_ID, "configbase/armor_conductivity.json"));
		SurvivalInc.blockPropertiesConfig = new ExportedResource(new File(configDir, "block_properties.json"), new ResourceLocation(SurvivalInc.MOD_ID, "configbase/block_properties.json"));
		SurvivalInc.net = NetworkRegistry.INSTANCE.newSimpleChannel(SurvivalInc.MOD_ID);

		// Register the auxiliary event handlers
		MinecraftForge.EVENT_BUS.register(this);

		Object auxHandler = SurvivalInc.proxy.getAuxiliaryEventHandler();
		if(auxHandler != null)
			MinecraftForge.EVENT_BUS.register(auxHandler);

		// Register capabilities.
		MinecraftForge.EVENT_BUS.register(StatCapability.class);
		CapabilityManager.INSTANCE.register(StatTracker.class, StatStorage.instance, StatRegisterDispatcher.instance);

		SurvivalInc.proxy.registerRendering();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		SurvivalInc.blockPropertiesConfig.load((InputStream input) -> {
			JsonParser parser = new JsonParser();
			JsonElement root = parser.parse(new InputStreamReader(input));
			BlockPropertyJsonParser loader = new BlockPropertyJsonParser(SurvivalInc.blockProperties.editingBuilder());
			loader.fromJson(root);
		});

		if(ModConfig.HEAT.enabled)
		{
			SurvivalInc.heat = new HeatModifier();
			MinecraftForge.EVENT_BUS.register(SurvivalInc.heat);
		}

		if(ModConfig.HYDRATION.enabled)
		{
			SurvivalInc.hydration = new HydrationModifier();
			MinecraftForge.EVENT_BUS.register(SurvivalInc.hydration);
		}

		if(ModConfig.SANITY.enabled)
		{
			SurvivalInc.sanity = new SanityModifier(SurvivalInc.blockProperties.view("sanity", Float.class));
			MinecraftForge.EVENT_BUS.register(SurvivalInc.sanity);
		}

		if(ModConfig.WETNESS.enabled)
		{
			SurvivalInc.wetness = new WetnessModifier();
			MinecraftForge.EVENT_BUS.register(SurvivalInc.wetness);
		}

		if(ModConfig.GHOST.enabled)
		{
			SurvivalInc.ghost = new GhostProvider();
			MinecraftForge.EVENT_BUS.register(SurvivalInc.ghost);
		}

		// Register seasons if enabled
		if(ModConfig.SEASONS.enabled)
		{
			SeasonCalendarConstructEvent scce = new SeasonCalendarConstructEvent(SimpleSeasonCalendar::new);
			MinecraftForge.EVENT_BUS.post(scce);
			SurvivalInc.seasonCalendar = scce.buildCalendar();
			SurvivalInc.seasonController = new SeasonController(SurvivalInc.seasonCalendar, ReflectiveBiomeTemperatureInjector.getInstance(), ImmutableSet.of(BiomeOcean.class, BiomeHell.class, BiomeEnd.class));

			MelterSetupEvent mse = new MelterSetupEvent();
			MinecraftForge.EVENT_BUS.post(mse);
			SurvivalInc.meltingController = mse.buildController(MeltingController::new);

			MinecraftForge.EVENT_BUS.register(SurvivalInc.seasonController);
			MinecraftForge.EVENT_BUS.register(SurvivalInc.meltingController);
		}
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		SurvivalInc.net.registerMessage(StatSyncHandler.class, StatSyncMessage.class, 0, Side.CLIENT);
		SurvivalInc.net.registerMessage(SurvivalInc.proxy.createSidedMessageHandler(SeasonSyncMessage.class), SeasonSyncMessage.class, 1, Side.CLIENT);
		SurvivalInc.net.registerMessage(EntityItemUpdater.class, EntityItemUpdateMessage.class, 2, Side.CLIENT);
		SurvivalInc.net.registerMessage(HydrationModifier::validateMessage, WaterDrinkMessage.class, 3, Side.SERVER);
		SurvivalInc.net.registerMessage(StatSyncRequestHandler.class, StatSyncRequestMessage.class, 4, Side.SERVER);
		SurvivalInc.net.registerMessage(SurvivalInc.seasonController::onSyncRequest, SeasonSyncRequest.class, 5, Side.SERVER);

		// Load the compatibility maps
		if(SurvivalInc.heat != null)
			SurvivalInc.armorConductivityConfig.load(SurvivalInc.heat.armor::load);

		SurvivalInc.proxy.registerRendering();
		SurvivalInc.proxy.createHUD();

		SurvivalInc.itemEffectConfig.load(this::loadItemEffects);
	}

	@FunctionalImplementation(of = ExportedResource.ResourceConsumer.class)
	private void loadItemEffects(InputStream input) throws IOException
	{
		ItemSituationParserSetupEvent pse = new ItemSituationParserSetupEvent();
		MinecraftForge.EVENT_BUS.post(pse);

		Collection<ItemSituation<?>> effects = pse.getParser().parse(input);
		SurvivalInc.mapper.register(effects);

		MinecraftForge.EVENT_BUS.register(SurvivalInc.mapper.createEventHandler());
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		MinecraftServer server = event.getServer();
		CommandHandler manager = (CommandHandler)server.getCommandManager();

		if(ModConfig.SEASONS.enabled) manager.registerCommand(new SeasonCommand());
		if(SurvivalInc.ghost != null) manager.registerCommand(new GhostCommand());
		if(SurvivalInc.heat != null) manager.registerCommand(new ArmorConductivityCommand(SurvivalInc.heat.armor));
		manager.registerCommand(new StatCommand());
	}

	@SubscribeEvent
	public void addParserSituations(ItemSituationParserSetupEvent event)
	{
		event.parser.addSituationFactory("in-hand", ItemInHandSituation::new);
		event.parser.addSituationFactory("in-inventory", ItemInInvSituation::new);
		event.parser.addSituationFactory("consumed", ItemConsumedSituation::new);
	}

	@SubscribeEvent
	public void constructSeasonCalendar(SeasonCalendarConstructEvent event)
	{
		event.registerSeasons(ImmutableList.copyOf(SurvivalIncSeason.values()));
	}

	@SubscribeEvent
	public void setupMelterEntries(MelterSetupEvent event)
	{
		event.setCompiler(ModConfig.SEASONS.meltingBehavior);
		event.beginEntry((BlockMelting)ModBlocks.MELTING_SNOW.get()).onSurface(1).register();
		event.beginEntry((BlockMelting)ModBlocks.MELTING_ICE.get()).onSurface(0).register();
	}
}
