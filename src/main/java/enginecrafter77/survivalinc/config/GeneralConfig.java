package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc.general")
public class GeneralConfig {
	@Config.LangKey("config.survivalinc.general.serverSyncDelay")
	@Config.Comment("The amount of ticks between automatic server-client stat data synchronizations")
	@Config.RangeInt(min = 1)
	public final int serverSyncDelay = 600;
	
	@Config.LangKey("config.survivalinc.general.verifyDrinkRequests")
	@Config.Comment({
		"Set this to true to verify water drink requests by doing independent server-side raytracing.",
		"Setting this to false allows hacked clients to replenish their hydration idenfinitely",
		"On the other hand, this may cause some issues with mods implementing draconic policies on raytracing, such as ValkyrienSkies",
		"If you are unsure, leave this to true. If you're experiencing crashes, try turning this off"
	})
	public final boolean verifyClientDrinkingRequests = true;
}
