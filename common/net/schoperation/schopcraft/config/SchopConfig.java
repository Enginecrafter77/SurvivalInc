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

@Config(modid = SchopCraft.MOD_ID, name = "SchopCraft/" + SchopCraft.MOD_NAME, category="")
public class SchopConfig {
	
	// The values in the config file. Add on more as needed.
	// Sub-categories
	@Config.Name("client")
	public static final Client client = new Client();
	
	@Config.Name("mechanics")
	public static final Mechanics mechanics = new Mechanics();
	
	@Config.Name("seasons")
	public static final Seasons seasons = new Seasons();
	
	// Actual static classes
	
	@Config.LangKey("config.schopcraft:client")
	public static class Client {
		
		@Config.LangKey("config.schopcraft:client.showCelsius")
		public boolean showCelsius = false;
	
		@Config.LangKey("config.schopcraft:client.showSips")
		public boolean showSipsInDurabilityBar = false;
	}
	
	@Config.LangKey("config.schopcraft:mechanics")
	public static class Mechanics {
		
		@Config.LangKey("config.schopcraft:mechanics.enableGhost")
		@Config.RequiresWorldRestart
		public boolean enableGhost = true;
		
		@Config.LangKey("config.schopcraft:mechanics.enableTemperature")
		@Config.RequiresWorldRestart
		public boolean enableTemperature = true;
		
		@Config.LangKey("config.schopcraft:mechanics.enableThirst")
		@Config.RequiresWorldRestart
		public boolean enableThirst = true;
		
		@Config.LangKey("config.schopcraft:mechanics.enableSanity")
		@Config.RequiresWorldRestart
		public boolean enableSanity = true;
		
		@Config.LangKey("config.schopcraft:mechanics.enableWetness")
		@Config.RequiresWorldRestart
		public boolean enableWetness = true;
		
		@Config.LangKey("config.schopcraft:mechanics.temperatureScale")
		@Config.RangeDouble(min = 0.1, max = 3.0)
		public double temperatureScale = 1.0;
		
		@Config.LangKey("config.schopcraft:mechanics.thirstScale")
		@Config.RangeDouble(min = 0.1, max = 3.0)
		public double thirstScale = 1.0;
		
		@Config.LangKey("config.schopcraft:mechanics.sanityScale")
		@Config.RangeDouble(min = 0.1, max = 3.0)
		public double sanityScale = 1.0;
		
		@Config.LangKey("config.schopcraft:mechanics.wetnessScale")
		@Config.RangeDouble(min = 0.1, max = 3.0)
		public double wetnessScale = 1.0;
	}
	
	@Config.LangKey("config.schopcraft:seasons")
	public static class Seasons {
		
		@Config.LangKey("config.schopcraft:seasons.enableSeasons")
		@Config.RequiresWorldRestart
		public boolean aenableSeasons = true;
		
		@Config.LangKey("config.schopcraft:seasons.winterLength")
		@Config.RequiresWorldRestart
		public int winterLength = 14;
		
		@Config.LangKey("config.schopcraft:seasons.springLength")
		@Config.RequiresWorldRestart
		public int springLength = 14;
		
		@Config.LangKey("config.schopcraft:seasons.summerLength")
		@Config.RequiresWorldRestart
		public int summerLength = 14;
		
		@Config.LangKey("config.schopcraft:seasons.autumnLength")
		@Config.RequiresWorldRestart
		public int autumnLength = 14;
	}
	
	// This deals with changed the config values in Forge's GUI in-game.
	// It also deals with syncing some config values from the server to the client, so everything doesn't get messed up.
	@Mod.EventBusSubscriber
	private static class ConfigEvents {
		
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
				IMessage msg = new ConfigPacket.ConfigMessage(SchopConfig.mechanics.enableGhost, SchopConfig.mechanics.enableTemperature, SchopConfig.mechanics.enableThirst, SchopConfig.mechanics.enableSanity, SchopConfig.mechanics.enableWetness, SchopConfig.mechanics.temperatureScale, SchopConfig.mechanics.thirstScale, SchopConfig.mechanics.sanityScale, SchopConfig.mechanics.wetnessScale, SchopConfig.seasons.aenableSeasons, SchopConfig.seasons.winterLength, SchopConfig.seasons.springLength, SchopConfig.seasons.summerLength, SchopConfig.seasons.autumnLength);
				SchopPackets.net.sendTo(msg, (EntityPlayerMP) player); 
			}
		}
	}
}