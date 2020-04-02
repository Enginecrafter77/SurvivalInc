package schoperation.schopcraft;

import net.minecraft.creativetab.CreativeTabs;
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
import schoperation.schopcraft.lib.ModItems;

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

			return new ItemStack(ModItems.TAB_ICON);
		}
	};
}