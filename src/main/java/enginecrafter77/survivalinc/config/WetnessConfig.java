package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc.wetness")
public class WetnessConfig {
	
	@Config.LangKey("config.survivalinc.wetness.enable")
	@Config.Comment("Setting this to true enables wetness")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc.wetness.drainingFactor")
	@Config.Comment({"The fraction of the current wetness drained each tick", "Please note that the value drained each tick is always a FRACTION of what the value was in previous tick. This makes a nice logarithmic draining cruve"})
	@Config.RangeDouble(min = 0)
	public double drainingFactor = 64;
	
	@Config.LangKey("config.survivalinc.wetness.minimalWalkSpeed")
	@Config.Comment("The minimal walking speed, generally caused by maximum value of wetness")
	@Config.RangeDouble(min = 0, max = 1)
	public double minimalWalkSpeed = 0.25;
	
	@Config.LangKey("config.survivalinc.wetness.towelCapacity")
	@Config.Comment("The capacity of a towel expressed as a percentage of player's maximum wetness")
	@Config.RangeDouble(min = 0)
	public double towelCapacity = 40;
	
	@Config.LangKey("config.survivalinc.wetness.towelDryRate")
	@Config.Comment("The rate in units/tick at which a towel dries on top of a furnace")
	@Config.RangeDouble(min = 0)
	public double towelDryRate = 0.025;
	
}
