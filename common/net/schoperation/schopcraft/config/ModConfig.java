package net.schoperation.schopcraft.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.schoperation.schopcraft.SchopCraft;

@Config(modid = SchopCraft.MOD_ID)
public class ModConfig {
	
	// the values in the config file
	@Config.Comment("If true, the temperature will be shown in Celsius. If false, Fahrenheit")
	public static boolean celsius = false;
	
	@Mod.EventBusSubscriber
	private static class SchopConfig {
		
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			
			// make sure it's the right mod
			if (event.getModID().equals(SchopCraft.MOD_ID)) {
				
				ConfigManager.sync(SchopCraft.MOD_ID, Config.Type.INSTANCE);
			}
		}
	}
}
