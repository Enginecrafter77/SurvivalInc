package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc.wetness")
public class WetnessConfig {
	
	@Config.LangKey("config.survivalinc.wetness.enable")
	@Config.Comment("Setting this to true enables wetness")
	@Config.RequiresMcRestart
	public final boolean enabled = true;
	
	@Config.LangKey("config.survivalinc.wetness.drainingFactor")
	@Config.Comment({"The fraction of the current wetness drained each tick", "Please note that the value drained each tick is always a FRACTION of what the value was in previous tick. This makes a nice logarithmic draining cruve"})
	@Config.RangeDouble(min = 0)
	public final double drainingFactor = 64;
	
	@Config.LangKey("config.survivalinc.wetness.minimalWalkSpeed")
	@Config.Comment("The minimal walking speed, generally caused by maximum value of wetness")
	@Config.RangeDouble(min = 0, max = 1)
	public final double minimalWalkSpeed = 0.25;
	
	@Config.LangKey("config.survivalinc.wetness.towelCapacity")
	@Config.Comment("The capacity of a towel expressed as a fraction of player's maximum wetness")
	@Config.RangeDouble(min = 0)
	public final double towelCapacity = 0.4;
	
	@Config.LangKey("config.survivalinc.wetness.towelDryRate")
	@Config.Comment("The rate in units/tick at which a towel dries on top of a furnace")
	@Config.RangeDouble(min = 0)
	public final double towelDryRate = 0.025;
	
	@Config.LangKey("config.survivalinc.wetness.fullySubmergedRate")
	@Config.Comment({"The rate in units/tick at which wetness rises when fully submerged in water.", "The clothing soak-up is meant to be instant, so it makes sense for this value to be arbitrarily high."})
	@Config.RangeDouble(min = 0)
	public final double fullySubmergedRate = 5;
	
	@Config.LangKey("config.survivalinc.wetness.partiallySubmergedRate")
	@Config.Comment("The rate in units/tick at which wetness rises when partially submerged in water.")
	@Config.RangeDouble(min = 0)
	public final double partiallySubmergedRate = 1.25;
	
	@Config.LangKey("config.survivalinc.wetness.partiallySubmergedCap")
	@Config.Comment({"The maximum fraction of wetness absorbed when partially submerged.", "Setting this value to 0 disables partial submersion, while 1 disables full submersion."})
	@Config.RangeDouble(min = 0, max = 1)
	@Config.SlidingOption
	public final double partiallySubmergedCap = 0.4;
	
}
