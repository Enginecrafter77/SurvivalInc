package net.schoperation.schopcraft.config;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.schoperation.schopcraft.SchopCraft;
import net.schoperation.schopcraft.packet.ConfigPacket;
import net.schoperation.schopcraft.packet.SchopPackets;

@Config(modid = SchopCraft.MOD_ID)
public class ModConfig {
	
	// The values in the config file. Add on more as needed.
	// Client only
	@Config.Comment("If true, the temperature will be shown in Celsius. If false, it will be shown in Fahrenheit")
	public static boolean showCelsius = false;
	
	@Config.Comment("By default, the durability bar of the canteen (and HydroPouch) will show its durability. Set this to true to have the bar show the number of sips left instead, like before.")
	public static boolean showSipsInDurabilityBar = false;
	
	// Server 
	@Config.Comment("Upon death, you become a ghost, freely roaming the world. Disable this setting to allow for normal respawning.")
	@Config.RequiresWorldRestart
	public static boolean enableGhost = true;
	
	@Config.Comment("Toggle the temperature mechanic.")
	@Config.RequiresWorldRestart
	public static boolean enableTemperature = true;
	
	@Config.Comment("Toggle the thirst mechanic.")
	@Config.RequiresWorldRestart
	public static boolean enableThirst = true;
	
	@Config.Comment("Toggle the sanity mechanic.")
	@Config.RequiresWorldRestart
	public static boolean enableSanity = true;
	
	@Config.Comment("Toggle the wetness mechanic.")
	@Config.RequiresWorldRestart
	public static boolean enableWetness = true;
	
	// This deals with changed the config values in Forge's GUI in-game.
	// It also deals with syncing some config values from the server to the client, so everything doesn't get messed up.
	@Mod.EventBusSubscriber
	private static class SchopConfig {
		
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			
			// Make sure it's the right mod.
			if (event.getModID().equals(SchopCraft.MOD_ID)) {
				
				ConfigManager.sync(SchopCraft.MOD_ID, Config.Type.INSTANCE);
			}
		}
		
		@SubscribeEvent
		public static void onLogin(PlayerLoggedInEvent event) {
			
			// Player instance
			EntityPlayer player = event.player;
			
			if (player instanceof EntityPlayerMP) {
				
				// Send server config values to the client. This will not affect the client's config file; it's temporary stuff.
				IMessage msg = new ConfigPacket.ConfigMessage(ModConfig.enableGhost, ModConfig.enableTemperature, ModConfig.enableThirst, ModConfig.enableSanity, ModConfig.enableWetness);
				SchopPackets.net.sendTo(msg, (EntityPlayerMP) player); 
			}
		}
	}
}