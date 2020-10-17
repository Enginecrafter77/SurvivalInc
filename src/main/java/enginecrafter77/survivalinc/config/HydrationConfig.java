package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:hydration")
public class HydrationConfig {
	@Config.LangKey("config.survivalinc:hydration.enable")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc:hydration.startValue")
	@Config.RangeDouble(min = 0, max = 100)
	@Config.RequiresWorldRestart
	public double startValue = 80D;
	
	@Config.LangKey("config.survivalinc:hydration.passiveDrain")
	@Config.RangeDouble(min = 0)
	public double passiveDrain = 0.001F;
	
	@Config.LangKey("config.survivalinc:hydration.sweatingThreshold")
	@Config.RangeDouble(min = 0, max = 1)
	public double sweatingThreshold = 0.75;
	
	@Config.LangKey("config.survivalinc:hydration.sweatingMultiplier")
	@Config.RangeDouble(min = 0)
	public double sweatingMultiplier = 4;
	
	@Config.LangKey("config.survivalinc:hydration.drinkAmount")
	@Config.RangeDouble(min = 0)
	public double drinkAmount = 5;
}
