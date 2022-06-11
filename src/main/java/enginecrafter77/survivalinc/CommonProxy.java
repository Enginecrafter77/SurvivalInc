package enginecrafter77.survivalinc;

import enginecrafter77.survivalinc.block.BlockMelting;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.ghost.GhostCommand;
import enginecrafter77.survivalinc.ghost.GhostProvider;
import enginecrafter77.survivalinc.net.StatSyncRequestHandler;
import enginecrafter77.survivalinc.net.StatSyncRequestMessage;
import enginecrafter77.survivalinc.net.WaterDrinkMessage;
import enginecrafter77.survivalinc.season.*;
import enginecrafter77.survivalinc.season.melting.MeltingController;
import enginecrafter77.survivalinc.season.melting.MeltingController.MelterEntry;
import enginecrafter77.survivalinc.stats.*;
import enginecrafter77.survivalinc.stats.effect.item.*;
import enginecrafter77.survivalinc.stats.impl.HeatModifier;
import enginecrafter77.survivalinc.stats.impl.HydrationModifier;
import enginecrafter77.survivalinc.stats.impl.SanityModifier;
import enginecrafter77.survivalinc.stats.impl.WetnessModifier;
import enginecrafter77.survivalinc.stats.impl.armor.ArmorConductivityCommand;
import enginecrafter77.survivalinc.util.ExportedResource;
import enginecrafter77.survivalinc.util.FunctionalImplementation;
import net.minecraft.block.Block;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public abstract class CommonProxy {
	
	public SimpleNetworkWrapper net;
	public ItemSituationContainer mapper;
	
	public ExportedResource itemEffectConfig, armorConductivityConfig;
	
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
		
		File configDir = new File(event.getModConfigurationDirectory(), SurvivalInc.MOD_ID);
		this.itemEffectConfig = new ExportedResource(new File(configDir, "item_effects.json"), new ResourceLocation(SurvivalInc.MOD_ID, "configbase/item_effects.json"));
		this.armorConductivityConfig = new ExportedResource(new File(configDir, "armor_conductivity.json"), new ResourceLocation(SurvivalInc.MOD_ID, "configbase/armor_conductivity.json"));
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
		if(SurvivalInc.heat != null)
			this.armorConductivityConfig.load(SurvivalInc.heat.armor::load);

		// Radiant heat scanner maps
		for(String entry : ModConfig.HEAT.blockHeatMap)
		{
			int separator = entry.lastIndexOf(' ');
			Block target = Block.getBlockFromName(entry.substring(0, separator));
			float value = Float.parseFloat(entry.substring(separator + 1));
			SurvivalInc.heatScanner.registerBlock(target, value);
		}

		this.itemEffectConfig.load(this::loadItemEffects);
	}
	
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
	
	@FunctionalImplementation(of = ExportedResource.ResourceConsumer.class)
	private void loadItemEffects(InputStream input) throws IOException
	{
		ItemSituationParserSetupEvent pse = new ItemSituationParserSetupEvent();
		MinecraftForge.EVENT_BUS.post(pse);
		
		Collection<ItemSituation<?>> effects = pse.getParser().parse(input);
		this.mapper.register(effects);
		
		MinecraftForge.EVENT_BUS.register(this.mapper.createEventHandler());
	}
}
