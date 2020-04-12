package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:wetness")
public class WetnessConfig {
	
	@Config.LangKey("config.survivalinc:wetness.enable")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc:wetness.scale")
	@Config.RequiresMcRestart
	@Config.RangeDouble(min = 0.1, max = 3.0)
	public double scale = 1.0;
	
	@Config.LangKey("config.survivalinc:wetness.minimalWalkSpeed")
	@Config.RangeDouble(min = 0, max = 1)
	public double minimalWalkSpeed = 0.25;
	
	@Config.LangKey("config.survivalinc:wetness.slowdownThreshold")
	@Config.RangeDouble(min = 0, max = 100)
	public double slowdownThreshold = 75;
	
}
