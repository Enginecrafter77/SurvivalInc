package net.schoperation.schopcraft;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.schoperation.schopcraft.util.RegAndRen;

@Mod(modid = SchopCraft.MOD_ID, name = SchopCraft.MOD_NAME, version = SchopCraft.VERSION, dependencies = SchopCraft.DEPENDENCIES)
public class SchopCraft {
	
	// Mod Constants
	public static final String MOD_ID = "schopcraft";
	public static final String MOD_NAME = "SchopCraft Universalis";
	public static final String VERSION = "0.1.0";
	public static final String DEPENDENCIES = "required-after:forge@[14.21.0.2344,)";
	public static final String RESOURCE_PREFIX = MOD_ID.toLowerCase() + ":"; // schopcraft:
	
	// make an instance
	@Instance(MOD_ID)
	public static SchopCraft instance;
	
	// create proxies to allow either client-side only crap or both server-side and client-side
	@SidedProxy(clientSide = "net.schoperation.schopcraft.ClientProxy", serverSide = "net.schoperation.schopcraft.CommonProxy")
	public static CommonProxy proxy;
	
	// event handler. handles crap. Go figure. Stop reading these dumb comments, I'm just as clueless.
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		proxy.preInit(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		
		proxy.init(event);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
		proxy.postInit(event);
	}
	
	// create tabs for creative mode
	public static CreativeTabs mainTab = new CreativeTabs(SchopCraft.RESOURCE_PREFIX + "mainTab") {
		
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(RegAndRen.tabIcon);
		}
		
	};

}
