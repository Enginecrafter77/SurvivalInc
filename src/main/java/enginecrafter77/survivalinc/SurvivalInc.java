package enginecrafter77.survivalinc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import enginecrafter77.survivalinc.block.BlockMelting;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.ghost.GhostCommand;
import enginecrafter77.survivalinc.ghost.GhostProvider;
import enginecrafter77.survivalinc.season.BiomeTempController;
import enginecrafter77.survivalinc.season.SeasonCommand;
import enginecrafter77.survivalinc.season.SeasonController;
import enginecrafter77.survivalinc.season.SurvivalIncSeason;
import enginecrafter77.survivalinc.season.calendar.SeasonCalendar;
import enginecrafter77.survivalinc.season.calendar.SimpleSeasonCalendar;
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
import net.minecraft.block.Block;
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
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
	public static BiomeTempController biomeTempController;
	public static SeasonController seasonController;

	public static ExportedResource itemEffectConfig, armorConductivityConfig, sanityBlockEffectMap;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		File configDir = new File(event.getModConfigurationDirectory(), SurvivalInc.MOD_ID);

		SurvivalInc.logger = event.getModLog();
		SurvivalInc.heatScanner = new RadiantHeatScanner();
		SurvivalInc.mapper = new ItemSituationContainer();
		SurvivalInc.itemEffectConfig = new ExportedResource(new File(configDir, "item_effects.json"), new ResourceLocation(SurvivalInc.MOD_ID, "configbase/item_effects.json"));
		SurvivalInc.armorConductivityConfig = new ExportedResource(new File(configDir, "armor_conductivity.json"), new ResourceLocation(SurvivalInc.MOD_ID, "configbase/armor_conductivity.json"));
		SurvivalInc.sanityBlockEffectMap = new ExportedResource(new File(configDir, "sanity_block_effects.json"), new ResourceLocation(SurvivalInc.MOD_ID, "configbase/sanity_block_effects.json"));

		// Register seasons if enabled
		if(ModConfig.SEASONS.enabled)
		{
			SurvivalInc.seasonCalendar = new SimpleSeasonCalendar(ImmutableList.copyOf(SurvivalIncSeason.values()));
			SurvivalInc.biomeTempController = new BiomeTempController(ImmutableSet.of(BiomeOcean.class, BiomeHell.class, BiomeEnd.class));
			SurvivalInc.seasonController = new SeasonController(SurvivalInc.biomeTempController, SurvivalInc.seasonCalendar);

			MinecraftForge.EVENT_BUS.register(SurvivalInc.seasonController);
			MeltingController.meltmap.add(new MeltingController.MelterEntry((BlockMelting)ModBlocks.MELTING_SNOW.get()).level(1, true)); // 1 = block above ground
			MeltingController.meltmap.add(new MeltingController.MelterEntry((BlockMelting)ModBlocks.MELTING_ICE.get()).level(0, true)); // 0 = ground
		}

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
		SurvivalInc.net = NetworkRegistry.INSTANCE.newSimpleChannel(SurvivalInc.MOD_ID);
		SurvivalInc.proxy.registerNetworkHandlers(SurvivalInc.net);

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
			SurvivalInc.sanity = new SanityModifier();
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
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		if(ModConfig.SEASONS.enabled && ModConfig.SEASONS.meltController.isValid())
		{
			MeltingController.compile(ModConfig.SEASONS.meltController);
			MinecraftForge.EVENT_BUS.register(MeltingController.class);
		}

		// Load the compatibility maps
		if(SurvivalInc.heat != null)
			SurvivalInc.armorConductivityConfig.load(SurvivalInc.heat.armor::load);

		if(SurvivalInc.sanity != null)
			SurvivalInc.sanityBlockEffectMap.load(SurvivalInc.sanity.blockEffectMap::loadFrom);

		// Radiant heat scanner maps
		for(String entry : ModConfig.HEAT.blockHeatMap)
		{
			int separator = entry.lastIndexOf(' ');
			Block target = Block.getBlockFromName(entry.substring(0, separator));
			float value = Float.parseFloat(entry.substring(separator + 1));
			SurvivalInc.heatScanner.registerBlock(target, value);
		}

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
	
	// Create tab for creative mode.
	public static CreativeTabs mainTab = new CreativeTabs(SurvivalInc.MOD_ID + ":mainTab") {
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(ModItems.CANTEEN.getItem());
		}
	};
}
