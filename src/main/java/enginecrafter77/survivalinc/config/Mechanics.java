package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:mechanics")
public class Mechanics {

	@Config.LangKey("config.survivalinc:mechanics.enableGhost")
	@Config.RequiresMcRestart
	public boolean enableGhost = true;

	@Config.LangKey("config.survivalinc:mechanics.enableTemperature")
	@Config.RequiresMcRestart
	public boolean enableTemperature = true;

	@Config.LangKey("config.survivalinc:mechanics.enableThirst")
	@Config.RequiresMcRestart
	public boolean enableThirst = true;

	@Config.LangKey("config.survivalinc:mechanics.enableSanity")
	@Config.RequiresMcRestart
	public boolean enableSanity = true;

	@Config.LangKey("config.survivalinc:mechanics.enableWetness")
	@Config.RequiresMcRestart
	public boolean enableWetness = true;

	@Config.LangKey("config.survivalinc:mechanics.temperatureScale")
	@Config.RequiresMcRestart
	@Config.RangeDouble(min = 0.1, max = 3.0)
	public double temperatureScale = 1.0;

	@Config.LangKey("config.survivalinc:mechanics.thirstScale")
	@Config.RequiresMcRestart
	@Config.RangeDouble(min = 0.1, max = 3.0)
	public double thirstScale = 1.0;

	@Config.LangKey("config.survivalinc:mechanics.sanityScale")
	@Config.RequiresMcRestart
	@Config.RangeDouble(min = 0.1, max = 3.0)
	public double sanityScale = 1.0;

	@Config.LangKey("config.survivalinc:mechanics.wetnessScale")
	@Config.RequiresMcRestart
	@Config.RangeDouble(min = 0.1, max = 3.0)
	public double wetnessScale = 1.0;
	
	@Config.LangKey("config.survivalinc:mechanics.minAnnoyingWetness")
	@Config.RangeDouble(min = 0, max = 100)
	public double minAnnoyingWetness = 50;
	
	@Config.LangKey("config.survivalinc:mechanics.darkSpookFactorBase")
	@Config.RangeDouble(min = 0.0001, max = 0.1)
	public double darkSpookFactorBase = 0.05;
	
	@Config.LangKey("config.survivalinc:mechanics.comfortLightLevel")
	@Config.RangeInt(min = 0, max = 15)
	public int comfortLightLevel = 7;
	
	@Config.LangKey("config.survivalinc:mechanics.heatExchangeFactor")
	@Config.RangeDouble(min = 0, max = 1)
	public double heatExchangeFactor = 0.0003;
	
	@Config.LangKey("config.survivalinc:mechanics.wetnessSlowdownThreshold")
	@Config.RangeDouble(min = 0, max = 100)
	public double wetnessSlowdownThreshold = 75;
	
	@Config.LangKey("config.survivalinc:mechanics.minimalWalkSpeed")
	@Config.RangeDouble(min = 0, max = 1)
	public double minimalWalkSpeed = 0.25;
}
