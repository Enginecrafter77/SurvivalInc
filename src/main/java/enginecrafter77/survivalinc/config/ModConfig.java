package enginecrafter77.survivalinc.config;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = SurvivalInc.MOD_ID, name = SurvivalInc.MOD_ID, category = "")
public class ModConfig {

	// The values in the config file. Add on more as needed.
	// Sub-categories
	@Config.Name("client")
	public static final Client CLIENT = new Client();

	@Config.Name("mechanics")
	public static final Mechanics MECHANICS = new Mechanics();

	@Config.Name("seasons")
	public static final Seasons SEASONS = new Seasons();

	// This deals with changed the config values in Forge's GUI in-game.
	// It also deals with syncing some config values from the server to the
	// client, so everything doesn't get messed up.
	@Mod.EventBusSubscriber
	private static class ConfigEvents {
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event)
		{
			// Make sure it's the right mod.
			if (event.getModID().equals(SurvivalInc.MOD_ID))
			{
				ConfigManager.sync(SurvivalInc.MOD_ID, Config.Type.INSTANCE);
			}
		}
	}
}