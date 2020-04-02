package schoperation.schopcraft.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.schopcraft:mechanics")
public class Mechanics {

	@Config.LangKey("config.schopcraft:mechanics.enableGhost")
	@Config.RequiresWorldRestart
	public boolean enableGhost = true;

	@Config.LangKey("config.schopcraft:mechanics.enableTemperature")
	@Config.RequiresWorldRestart
	public boolean enableTemperature = true;

	@Config.LangKey("config.schopcraft:mechanics.enableThirst")
	@Config.RequiresWorldRestart
	public boolean enableThirst = true;

	@Config.LangKey("config.schopcraft:mechanics.enableSanity")
	@Config.RequiresWorldRestart
	public boolean enableSanity = true;

	@Config.LangKey("config.schopcraft:mechanics.enableWetness")
	@Config.RequiresWorldRestart
	public boolean enableWetness = true;

	@Config.LangKey("config.schopcraft:mechanics.temperatureScale")
	@Config.RequiresWorldRestart
	@Config.RangeDouble(min = 0.1, max = 3.0)
	public double temperatureScale = 1.0;

	@Config.LangKey("config.schopcraft:mechanics.thirstScale")
	@Config.RequiresWorldRestart
	@Config.RangeDouble(min = 0.1, max = 3.0)
	public double thirstScale = 1.0;

	@Config.LangKey("config.schopcraft:mechanics.sanityScale")
	@Config.RequiresWorldRestart
	@Config.RangeDouble(min = 0.1, max = 3.0)
	public double sanityScale = 1.0;

	@Config.LangKey("config.schopcraft:mechanics.wetnessScale")
	@Config.RequiresWorldRestart
	@Config.RangeDouble(min = 0.1, max = 3.0)
	public double wetnessScale = 1.0;
	
	@Config.LangKey("config.schopcraft:mechanics.minAnnoyingWetness")
	@Config.RequiresWorldRestart
	@Config.RangeDouble(min = 0, max = 100)
	public double minAnnoyingWetness = 50;
	
	@Config.LangKey("config.schopcraft:mechanics.darkSpookFactorBase")
	@Config.RequiresWorldRestart
	@Config.RangeDouble(min = 0.0001, max = 0.1)
	public double darkSpookFactorBase = 0.05;
	
	@Config.LangKey("config.schopcraft:mechanics.comfortLightLevel")
	@Config.RequiresWorldRestart
	@Config.RangeInt(min = 0, max = 15)
	public int comfortLightLevel = 8;
}
