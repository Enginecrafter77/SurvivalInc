package enginecrafter77.survivalinc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.season.Season;
import enginecrafter77.survivalinc.stats.StatManager;
import enginecrafter77.survivalinc.stats.impl.DefaultStats;
import enginecrafter77.survivalinc.stats.impl.HeatModifier;
import enginecrafter77.survivalinc.stats.impl.SanityModifier;
import enginecrafter77.survivalinc.stats.impl.WetnessModifier;
import enginecrafter77.survivalinc.util.OperationType;

@Mod(modid = SurvivalInc.MOD_ID, name = SurvivalInc.MOD_NAME, version = SurvivalInc.VERSION, acceptedMinecraftVersions = SurvivalInc.MCVERSION, dependencies = SurvivalInc.DEPENDENCIES)
public class SurvivalInc {

	// Basic mod constants.
	public static final String MOD_ID = "survivalinc";
	public static final String MOD_NAME = "Survival Inc.";
	public static final String VERSION = "0.3.2";
	public static final String MCVERSION = "1.12";
	public static final String DEPENDENCIES = "required-after:forge@[14.23.1.2611,)";
	public static final String RESOURCE_PREFIX = MOD_ID + ":";

	// Make an instance of the mod.
	@Instance(MOD_ID)
	public static SurvivalInc instance;

	// Logger
	public static final Logger logger = LogManager.getLogger(MOD_NAME);

	// Create proxies to load stuff correctly.
	@SidedProxy(clientSide = "enginecrafter77.survivalinc.ClientProxy", serverSide = "enginecrafter77.survivalinc.CommonProxy")
	public static CommonProxy proxy;

	// Basic event handlers. All of the work is done in the proxies.
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit(event);
		
		Season.initSeasons();
		
		DefaultStats.HYDRATION.modifiers.addConditionalModifier((EntityPlayer player) -> player.isInLava(), -0.5F, OperationType.OFFSET);
		DefaultStats.HYDRATION.modifiers.addConditionalModifier((EntityPlayer player) -> player.dimension == -1, -0.006F, OperationType.OFFSET);
		DefaultStats.HYDRATION.modifiers.addConditionalModifier((EntityPlayer player) -> player.world.rand.nextBoolean(), -0.003F, OperationType.OFFSET);
		
		DefaultStats.SANITY.modifiers.addConditionalModifier((EntityPlayer player) -> !player.world.isDaytime() && !player.isPlayerSleeping(), -0.0015F, OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.addConditionalModifier(SanityModifier.isOutsideOverworld, -0.004F, OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.addModifier(SanityModifier::whenInDark, OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.addModifier(SanityModifier::whenWet, OperationType.OFFSET);
		DefaultStats.SANITY.modifiers.addModifier(SanityModifier::whenNearEntities, OperationType.OFFSET);
		
		WetnessModifier.initHumidityMap();
		DefaultStats.WETNESS.modifiers.addConditionalModifier((EntityPlayer player) -> player.dimension == -1, -0.08F, OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.addConditionalModifier((EntityPlayer player) -> player.isInLava(), -5F, OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.addConditionalModifier((EntityPlayer player) -> player.world.isRainingAt(player.getPosition().up()), 0.01F, OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.addModifier(WetnessModifier::scanSurroundings, OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.addModifier(WetnessModifier::naturalDrying, OperationType.OFFSET);
		DefaultStats.WETNESS.modifiers.addModifier(WetnessModifier::whenInWater, OperationType.OFFSET);
		
		if(ModConfig.MECHANICS.enableThirst)
			StatManager.providers.add(DefaultStats.HYDRATION);
		
		if(ModConfig.MECHANICS.enableSanity)
			StatManager.providers.add(DefaultStats.SANITY);
		
		if(ModConfig.MECHANICS.enableWetness)
			StatManager.providers.add(DefaultStats.WETNESS);
		
		if(ModConfig.MECHANICS.enableTemperature)
			StatManager.providers.add(HeatModifier.instance);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		proxy.serverStarting(event);
	}

	// Create tab for creative mode.
	public static CreativeTabs mainTab = new CreativeTabs(SurvivalInc.RESOURCE_PREFIX + "mainTab") {
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(ModItems.CANTEEN.get());
		}
	};
}