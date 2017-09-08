package net.schoperation.schopcraft.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.schoperation.schopcraft.SchopCraft;

@Config(modid = SchopCraft.MOD_ID)
public class ModConfig {
	
	// The values in the config file. Add on more as needed.
	@Config.Comment("If true, the temperature will be shown in Celsius. If false, it will be shown in Fahrenheit")
	public static boolean showCelsius = false;
	
	@Config.Comment("By default, the durability bar of the canteen (and HydroPouch) will show its durability. Set this to true to have the bar show the number of sips left instead, like before.")
	public static boolean showSipsInDurabilityBar = false;
	
	// This deals with changed the config values in Forge's GUI in-game.
	@Mod.EventBusSubscriber
	private static class SchopConfig {
		
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			
			// Make sure it's the right mod.
			if (event.getModID().equals(SchopCraft.MOD_ID)) {
				
				ConfigManager.sync(SchopCraft.MOD_ID, Config.Type.INSTANCE);
			}
		}
	}
}