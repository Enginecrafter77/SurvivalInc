package enginecrafter77.survivalinc.config;

import enginecrafter77.survivalinc.CommonProxy;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.net.ConfigPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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

		@SubscribeEvent
		public static void onLogin(PlayerLoggedInEvent event)
		{
			// Player instance
			EntityPlayer player = event.player;

			if (player instanceof EntityPlayerMP)
			{
				// Send server config values to the client. This will not affect
				// the client's config file; it's temporary stuff.
				IMessage msg = new ConfigPacket.ConfigMessage(ModConfig.MECHANICS.enableGhost,
						ModConfig.MECHANICS.enableTemperature, ModConfig.MECHANICS.enableThirst,
						ModConfig.MECHANICS.enableSanity, ModConfig.MECHANICS.enableWetness,
						ModConfig.MECHANICS.temperatureScale, ModConfig.MECHANICS.thirstScale,
						ModConfig.MECHANICS.sanityScale, ModConfig.MECHANICS.wetnessScale,
						ModConfig.SEASONS.aenableSeasons, ModConfig.SEASONS.aenableDayLength,
						ModConfig.SEASONS.winterLength, ModConfig.SEASONS.springLength,
						ModConfig.SEASONS.summerLength, ModConfig.SEASONS.autumnLength);
				CommonProxy.net.sendTo(msg, (EntityPlayerMP) player);
			}
		}
	}
}