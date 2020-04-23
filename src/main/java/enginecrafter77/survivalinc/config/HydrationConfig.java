package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:hydration")
public class HydrationConfig {
	
	@Config.LangKey("config.survivalinc:hydration.enable")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
}
