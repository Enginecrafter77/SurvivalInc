package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc.hydration")
public class HydrationConfig {
	@Config.LangKey("config.survivalinc.hydration.enable")
	@Config.Comment("Setting this to true enables hydration mechanics")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc.hydration.startValue")
	@Config.Comment("The initial value of hydration.")
	@Config.RangeDouble(min = 0, max = 100)
	@Config.RequiresWorldRestart
	public double startValue = 80D;
	
	@Config.LangKey("config.survivalinc.hydration.passiveDrain")
	@Config.Comment("The rate in units/tick at which hydration is passively drained")
	@Config.RangeDouble(min = 0)
	public double passiveDrain = 0.001F;
	
	@Config.LangKey("config.survivalinc.hydration.sweatingThreshold")
	@Config.Comment("The amount of heat after which the sweating effect is applied")
	@Config.RangeDouble(min = 0, max = 1)
	public double sweatingThreshold = 0.75;
	
	@Config.LangKey("config.survivalinc.hydration.sweatingMultiplier")
	@Config.Comment("The multiplier to the passive drain inflicted by sweating effect")
	@Config.RangeDouble(min = 0)
	public double sweatingMultiplier = 4;
	
	@Config.LangKey("config.survivalinc.hydration.sipVolume")
	@Config.Comment("The amount of hydration replenished by single sip from canteen or water body")
	@Config.RangeDouble(min = 0)
	public double sipVolume = 5;
	
	@Config.LangKey("config.survivalinc.hydration.canteenCapacity")
	@Config.Comment("The amount of water indicated by number of sips the canteen can store")
	@Config.RangeInt(min = 1)
	public int canteenCapacity = 32;
	
	@Config.LangKey("config.survivalinc.hydration.foodMap")
	@Config.Comment("A map of foods and their associated effects on hydration after consuming them")
	@Config.RequiresMcRestart
	public String[] foodHydrationMap = {};
}
