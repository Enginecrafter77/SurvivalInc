package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:hydration")
public class HydrationConfig {
	
	@Config.LangKey("config.survivalinc:hydration.enable")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc:hydration.scale")
	@Config.RequiresMcRestart
	@Config.RangeDouble(min = 0.1, max = 3.0)
	public double scale = 1.0;
	
}
