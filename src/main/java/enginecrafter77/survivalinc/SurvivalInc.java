package enginecrafter77.survivalinc;

import net.minecraft.creativetab.CreativeTabs;
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