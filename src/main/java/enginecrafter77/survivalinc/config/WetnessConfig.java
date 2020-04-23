package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:wetness")
public class WetnessConfig {
	
	@Config.LangKey("config.survivalinc:wetness.enable")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc:wetness.passiveDryRate")
	@Config.RangeDouble(min = 0)
	public double passiveDryRate = 0.005;
	
	@Config.LangKey("config.survivalinc:wetness.sunlightMultiplier")
	@Config.RangeDouble(min = 0)
	public double sunlightMultiplier = 2;
	
	@Config.LangKey("config.survivalinc:wetness.minimalWalkSpeed")
	@Config.RangeDouble(min = 0, max = 1)
	public double minimalWalkSpeed = 0.25;
	
	@Config.LangKey("config.survivalinc:wetness.slowdownThreshold")
	@Config.RangeDouble(min = 0, max = 100)
	public double slowdownThreshold = 60;
	
	@Config.LangKey("config.survivalinc:wetness.towelCapacity")
	@Config.RangeDouble(min = 0)
	public double towelCapacity = 40;
	
	@Config.LangKey("config.survivalinc:wetness.towelDryRate")
	@Config.RangeDouble(min = 0)
	public double towelDryRate = 0.025;
	
}
