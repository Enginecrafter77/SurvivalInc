package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:mechanics")
public class Mechanics {

	@Config.LangKey("config.survivalinc:mechanics.enableGhost")
	@Config.RequiresWorldRestart
	public boolean enableGhost = true;

	@Config.LangKey("config.survivalinc:mechanics.enableTemperature")
	@Config.RequiresWorldRestart
	public boolean enableTemperature = true;

	@Config.LangKey("config.survivalinc:mechanics.enableThirst")
	@Config.RequiresWorldRestart
	public boolean enableThirst = true;

	@Config.LangKey("config.survivalinc:mechanics.enableSanity")
	@Config.RequiresWorldRestart
	public boolean enableSanity = true;

	@Config.LangKey("config.survivalinc:mechanics.enableWetness")
	@Config.RequiresWorldRestart
	public boolean enableWetness = true;

	@Config.LangKey("config.survivalinc:mechanics.temperatureScale")
	@Config.RequiresWorldRestart
	@Config.RangeDouble(min = 0.1, max = 3.0)
	public double temperatureScale = 1.0;

	@Config.LangKey("config.survivalinc:mechanics.thirstScale")
	@Config.RequiresWorldRestart
	@Config.RangeDouble(min = 0.1, max = 3.0)
	public double thirstScale = 1.0;

	@Config.LangKey("config.survivalinc:mechanics.sanityScale")
	@Config.RequiresWorldRestart
	@Config.RangeDouble(min = 0.1, max = 3.0)
	public double sanityScale = 1.0;

	@Config.LangKey("config.survivalinc:mechanics.wetnessScale")
	@Config.RequiresWorldRestart
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
}
