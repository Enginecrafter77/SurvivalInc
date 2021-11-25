package enginecrafter77.survivalinc;

import org.apache.logging.log4j.Logger;

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

@Mod(modid = SurvivalInc.MOD_ID)
public class SurvivalInc {
	
	// Basic mod constants.
	public static final String MOD_ID = "survivalinc";
	
	// Make an instance of the mod.
	@Instance(SurvivalInc.MOD_ID)
	public static SurvivalInc instance;
	
	// Logger
	public static Logger logger;
	
	// Create proxies to load stuff correctly.
	@SidedProxy(clientSide = "enginecrafter77.survivalinc.ClientProxy", serverSide = "enginecrafter77.survivalinc.ServerProxy")
	public static CommonProxy proxy;
	
	// Basic event handlers. All of the work is done in the proxies.
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		SurvivalInc.logger = event.getModLog();
		SurvivalInc.proxy.preInit(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		SurvivalInc.proxy.init(event);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		SurvivalInc.proxy.postInit(event);
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		SurvivalInc.proxy.serverStarting(event);
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
