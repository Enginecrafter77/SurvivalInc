package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc.hydration")
public class HydrationConfig {
	@Config.LangKey("config.survivalinc.hydration.enable")
	@Config.Comment("Setting this to true enables hydration mechanics")
	@Config.RequiresMcRestart
	public final boolean enabled = true;
	
	@Config.LangKey("config.survivalinc.hydration.startValue")
	@Config.Comment("The initial value of hydration.")
	@Config.RangeDouble(min = 0, max = 100)
	@Config.RequiresWorldRestart
	public final double startValue = 80D;
	
	@Config.LangKey("config.survivalinc.hydration.passiveDrain")
	@Config.Comment("The rate in units/tick at which hydration is passively drained")
	@Config.RangeDouble(min = 0)
	public final double passiveDrain = 0.001F;
	
	@Config.LangKey("config.survivalinc.hydration.sweatingThreshold")
	@Config.Comment("The amount of heat after which the sweating effect is applied")
	@Config.RangeDouble(min = 0, max = 1)
	public final double sweatingThreshold = 0.75;
	
	@Config.LangKey("config.survivalinc.hydration.sweatingMultiplier")
	@Config.Comment("The multiplier to the passive drain inflicted by sweating effect")
	@Config.RangeDouble(min = 0)
	public final double sweatingMultiplier = 4;
	
	@Config.LangKey("config.survivalinc.hydration.sipValue")
	@Config.Comment("The amount of hydration replenished by single sip from canteen or water body")
	@Config.RangeInt(min = 0)
	public final int sipValue = 40;

	@Config.LangKey("config.survivalinc.hydration.sipVolume")
	@Config.Comment("The amount of water consumed by single sips (in millibuckets)")
	@Config.RangeInt(min = 0)
	public final int sipVolume = 64;
	
	@Config.LangKey("config.survivalinc.hydration.canteenCapacity")
	@Config.Comment("The capacity of canteen in millibuckets (for balancing reasons)")
	@Config.RangeInt(min = 1)
	public final int canteenCapacity = 1000;
}
