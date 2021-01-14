package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc.general")
public class GeneralConfig {
	@Config.LangKey("config.survivalinc.general.serverSyncDelay")
	@Config.Comment("The amount of ticks between automatic server-client stat data synchronizations")
	@Config.RangeInt(min = 1)
	public int serverSyncDelay = 600;
}
