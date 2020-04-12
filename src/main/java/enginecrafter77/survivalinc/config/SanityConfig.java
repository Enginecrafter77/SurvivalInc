package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:sanity")
public class SanityConfig {
	
	@Config.LangKey("config.survivalinc:sanity.enable")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc:sanity.scale")
	@Config.RequiresMcRestart
	@Config.RangeDouble(min = 0.1, max = 3.0)
	public double scale = 1.0;
	
	@Config.LangKey("config.survivalinc:sanity.wetnessAnnoyanceThreshold")
	@Config.RangeDouble(min = 0, max = 100)
	public double wetnessAnnoyanceThreshold = 50;
	
	@Config.LangKey("config.survivalinc:sanity.darkSpookFactorBase")
	@Config.RangeDouble(min = 1E-4, max = 0.1)
	public double darkSpookFactorBase = 0.05;
	
	@Config.LangKey("config.survivalinc:sanity.comfortLightLevel")
	@Config.RangeInt(min = 0, max = 15)
	public int comfortLightLevel = 7;
	
}
