package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc.heat")
public class HeatConfig {
	@Config.Ignore
	public static final double SCHOPERATION_CONSTANT = 78;
	
	@Config.LangKey("config.survivalinc.heat.enable")
	@Config.Comment("Setting this to true enables heat mechanics")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc.heat.exchangeFactor")
	@Config.Comment("The rate at which the player's heat moves towards the environment's temperature")
	@Config.RangeDouble(min = 0, max = 1)
	public double heatExchangeFactor = 0.002D;
	
	@Config.LangKey("config.survivalinc.heat.gaussScaling")
	@Config.Comment({"A gauss curve scaling number.", "Higher values mean sharper curves."})
	@Config.RangeDouble(min = 0)
	public double gaussScaling = 1.5D;
	
	@Config.LangKey("config.survivalinc.heat.blockScanRange")
	@Config.Comment("The maximum distance radiant heat from blocks propagates through")
	@Config.RangeInt(min = 0)
	public double blockScanRange = 4;
	
	@Config.LangKey("config.survivalinc.heat.caveTemperature")
	@Config.Comment("The temperature at the cave normalization depth. If \"Gradient Cave Temperature\" is disabled, this temperature is uniform for every block below sea level.")
	@Config.RangeDouble(min = 0)
	public double caveTemperature = 0.7D;
	
	@Config.LangKey("config.survivalinc.heat.caveNormalizationDepth")
	@Config.Comment({"The depth below surface at which the ambient temperature is that set in \"Cave Temperature\"", "Has no effect if \"Gradient Cave Temperature\" is disabled."})
	@Config.RangeInt(min = 0)
	public int caveNormalizationDepth = 10;
	
	@Config.LangKey("config.survivalinc.heat.surfaceScanningRadius")
	@Config.Comment({
			"The radius around the surface block to calculate average surface height from.",
			"Higher numbers have larger impact on server performance.",
			"Has no effect if \"Gradient Cave Temperature\" is disabled."})
	@Config.RangeInt(min = 1)
	public int surfaceScanningRadius = 2;
	
	@Config.LangKey("config.survivalinc.heat.gradientCaveTemperature")
	@Config.Comment({"Set to true to enable the cave gradient temperatures.", "Setting this to false may improve server performance."})
	public boolean gradientCaveTemperature = true;
	
	@Config.LangKey("config.survivalinc.heat.tempCoefficient")
	@Config.Comment({
			"A coefficient to turn the biome's temperature to the one processed by Survival Inc.",
			"60 is normally a good choice; Schoperation used to use " + HeatConfig.SCHOPERATION_CONSTANT})
	@Config.RangeDouble(min = 0)
	public double tempCoefficient = 60D;
	
	@Config.LangKey("config.survivalinc.heat.wetnessExchangeMultiplier")
	@Config.Comment({"The heat exchange rate multiplier applied when wetness is at it's maximum", "This option is ignored when wetness is disabled"})
	@Config.RangeDouble(min = 0)
	public double wetnessExchangeMultiplier = 4D;
	
	@Config.LangKey("config.survivalinc.heat.fireDuration")
	@Config.Comment({"The duration of fire applied when the player's heat exceeds the threshold", "Setting this to 0 disables the fire, and opts for direct damage instead."})
	@Config.RangeInt(min = 0)
	public int fireDuration = 1;
	
	@Config.LangKey("config.survivalinc.heat.damageAmount")
	@Config.Comment("The damage applied if fireDuration is set to 0.")
	@Config.RangeDouble(min = 0)
	public double damageAmount = 1D;
	
	@Config.LangKey("config.survivalinc.heat.counteractionEnable")
	@Config.Comment("Set to true to enable body internal heat counteraction, a mechanism which regenerates or dissipates heat based on the distance from optimal temperature.")
	public boolean enableCounteraction = true;
	
	@Config.LangKey("config.survivalinc.heat.positiveCAAmplitude")
	@Config.Comment({"The maximum counteraction against too cold temperatures", "In other words, how much the body can correct it's temperature when it's too cold"})
	@Config.RangeDouble(min = 0)
	public double positiveCAAmplitude = 0.035D;
	
	@Config.LangKey("config.survivalinc.heat.negativeCAAmplitude")
	@Config.Comment({"The maximum counteraction against too hot temperatures", "In other words, how much the body can correct it's temperature when it's too hot"})
	@Config.RangeDouble(min = 0)
	public double negativeCAAmplitude = 0.01D;
	
	@Config.LangKey("config.survivalinc.heat.counteractionCoverage")
	@Config.Comment({"The range along which the counteraction scales.", "If the temperature gets x-ths of temperature range away from optimal, the amplitude remains constant (i.e. maxed out)"})
	@Config.RangeDouble(min = 0, max = 1)
	public double counteractionCoverage = 0.2D;
	
	@Config.LangKey("config.survivalinc.heat.counteractionExponent")
	@Config.Comment({
			"The exponent to the counteraction scaling function.",
			"Needless to say, exponent 1 makes the function scale lineraly.",
			"Likewise, exponents <1 make it scale like root functions.",
			"Exponent 0 makes the amplitude always maxed out."})
	@Config.RangeDouble(min = 0)
	public double counteractionExponent = 2D;
	
	@Config.LangKey("config.survivalinc.heat.daytimeDifference")
	@Config.Comment("The difference between the base environment temperature and the one during daytime. (and nighttime conversely)")
	public double daytimeDifference = 10D;
	
	@Config.LangKey("config.survivalinc.heat.colderNights")
	@Config.Comment("Setting this to true causes the \"Daytime Difference\" value to be subtracted during night.")
	public boolean colderNights = false;
	
	@Config.LangKey("config.survivalinc.heat.sunlightBonus")
	@Config.Comment("The environmental heat bonus received by standing in sunlight during daytime")
	public double sunlightBonus = 15D;
	
	@Config.LangKey("config.survivalinc.heat.blockHeatMap")
	@Config.Comment({"A map of blocks and their core heat.", "See the wiki for how this value is affecting the radiant heat"})
	@Config.RequiresMcRestart
	public String[] blockHeatMap = {"minecraft:lava 400", "minecraft:flowing_lava 350", "minecraft:magma 300", "minecraft:fire 200", "minecraft:lit_furnace 100", "minecraft:lit_pumpkin 80"};
}
