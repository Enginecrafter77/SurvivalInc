package schoperation.schopcraft;

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
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import schoperation.schopcraft.cap.stat.SanityModifier;
import schoperation.schopcraft.cap.stat.StatManager;
import schoperation.schopcraft.config.SchopConfig;
import schoperation.schopcraft.cap.stat.DefaultStats;
import schoperation.schopcraft.cap.stat.HeatModifier;
import schoperation.schopcraft.lib.ModItems;
import schoperation.schopcraft.season.Season;

@Mod(modid = SchopCraft.MOD_ID, name = SchopCraft.MOD_NAME, version = SchopCraft.VERSION, acceptedMinecraftVersions = SchopCraft.MCVERSION, dependencies = SchopCraft.DEPENDENCIES)
public class SchopCraft {

	// Basic mod constants.
	public static final String MOD_ID = "schopcraft";
	public static final String MOD_NAME = "SchopCraft";
	public static final String VERSION = "0.3.1";
	public static final String MCVERSION = "1.12";
	public static final String DEPENDENCIES = "required-after:forge@[14.23.1.2611,)";
	public static final String RESOURCE_PREFIX = MOD_ID + ":"; // schopcraft:

	// Make an instance of the mod.
	@Instance(MOD_ID)
	public static SchopCraft instance;

	// Logger
	public static final Logger logger = LogManager.getLogger(MOD_NAME);

	// Create proxies to load stuff correctly.
	@SidedProxy(clientSide = "schoperation.schopcraft.ClientProxy", serverSide = "schoperation.schopcraft.CommonProxy")
	public static CommonProxy proxy;

	// Basic event handlers. All of the work is done in the proxies.
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit(event);
		
		Season.initSeasons();
		
		DefaultStats.HYDRATION.addConditionalModifier((EntityPlayer player) -> player.isInLava(), -0.5F);
		DefaultStats.HYDRATION.addConditionalModifier((EntityPlayer player) -> player.dimension == -1, -0.006F);
		DefaultStats.HYDRATION.addConditionalModifier((EntityPlayer player) -> player.world.rand.nextBoolean(), -0.003F);
		
		DefaultStats.SANITY.addConditionalModifier((EntityPlayer player) -> !player.world.isDaytime() && !player.isPlayerSleeping(), -0.0015F);
		DefaultStats.SANITY.addConditionalModifier(SanityModifier.isOutsideOverworld, -0.004F);
		DefaultStats.SANITY.situational_modifiers.add(SanityModifier::whenInDark);
		DefaultStats.SANITY.situational_modifiers.add(SanityModifier::whenWet);
		DefaultStats.SANITY.situational_modifiers.add(SanityModifier::whenNearEntities);
		
		DefaultStats.HEAT.situational_modifiers.add(HeatModifier::whenNearHotBlock);
		DefaultStats.HEAT.situational_modifiers.add(HeatModifier::equalizeWithEnvironment);
		DefaultStats.HEAT.situational_modifiers.add(HeatModifier::applyWetnessCooldown);
		
		if(SchopConfig.MECHANICS.enableThirst)
			StatManager.providers.add(DefaultStats.HYDRATION);
		
		if(SchopConfig.MECHANICS.enableSanity)
			StatManager.providers.add(DefaultStats.SANITY);
		
		if(SchopConfig.MECHANICS.enableTemperature)
		{
			StatManager.providers.add(DefaultStats.HEAT);
			HeatModifier.initHeatMap();
		}
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
	public void serverStarted(FMLServerStartedEvent event)
	{
		proxy.serverStarted(event);
	}

	// Create tab for creative mode.
	public static CreativeTabs mainTab = new CreativeTabs(SchopCraft.RESOURCE_PREFIX + "mainTab") {
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(ModItems.CANTEEN.get());
		}
	};
}