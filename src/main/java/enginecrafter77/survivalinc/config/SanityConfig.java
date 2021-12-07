package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc.sanity")
public class SanityConfig {
	
	@Config.LangKey("config.survivalinc.sanity.enable")
	@Config.Comment("Setting this to true enables sanity mechanics")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc.sanity.startValue")
	@Config.Comment("The starting value of sanity")
	@Config.RangeDouble(min = 0, max = 100)
	@Config.RequiresWorldRestart
	public double startValue = 100D;
	
	@Config.LangKey("config.survivalinc.sanity.wetnessAnnoyanceThreshold")
	@Config.Comment("The fraction of wetness after which it starts to take a toll on sanity")
	@Config.RangeDouble(min = 0, max = 1)
	public double wetnessAnnoyanceThreshold = 0.35D;
	
	@Config.LangKey("config.survivalinc.sanity.maxWetnessAnnoyance")
	@Config.Comment("The maximum rate at which sanity is drained due to wetness")
	@Config.RangeDouble(min = 0, max = 100)
	public double maxWetnessAnnoyance = 0.02D;
	
	@Config.LangKey("config.survivalinc.sanity.darkSpookFactorBase")
	@Config.Comment({"The rate in units/tick at which being in light level of 0 drains your sanity", "This value is linearly interpolated to the other light levels up to the comfortable light level"})
	@Config.RangeDouble(min = 1E-4, max = 0.1)
	public double darkSpookFactorBase = 0.05;
	
	@Config.LangKey("config.survivalinc.sanity.comfortLightLevel")
	@Config.Comment("The minimum light level without any sanity impact")
	@Config.RangeInt(min = 0, max = 15)
	public int comfortLightLevel = 4;
	
	@Config.LangKey("config.survivalinc.sanity.nighttimeDrain")
	@Config.Comment("The rate at which sanity is passively drained during night")
	@Config.RangeDouble(min = 1E-3)
	public double nighttimeDrain = 0.0075;
	
	@Config.LangKey("config.survivalinc.sanity.friendlyMobBonus")
	@Config.Comment("The rate at which sanity is replenished by standing near friendly mobs")
	@Config.RangeDouble(min = 1E-3)
	public double friendlyMobBonus = 0.006;
	
	@Config.LangKey("config.survivalinc.sanity.hostileMobModifier")
	@Config.Comment("The rate at which sanity is drained by standing near hostile mobs")
	@Config.RangeDouble(min = 1E-3)
	public double hostileMobModifier = 0.003;
	
	@Config.LangKey("config.survivalinc.sanity.tamedMobMultiplier")
	@Config.Comment("The multiplier to the friendlyMobBonus if the entity also happens to be tamed")
	@Config.RangeDouble(min = 0)
	public double tamedMobMultiplier = 4;
	
	@Config.LangKey("config.survivalinc.sanity.animalTameBoost")
	@Config.Comment("The sanity bonus received when the player tames an animal")
	@Config.RangeDouble(min = 0)
	public double animalTameBoost = 20;
	
	@Config.LangKey("config.survivalinc.sanity.sleepRestoration")
	@Config.Comment("The fraction of maximum sanity restored by sleeping")
	@Config.RangeDouble(min = 0, max = 1)
	public double sleepResoration = 0.3;
	
	@Config.LangKey("config.survivalinc.sanity.hallucinationThreshold")
	@Config.Comment("A fraction of maximum sanity below which hallucinations start to appear")
	@Config.RangeDouble(min = 0, max = 1)
	public double hallucinationThreshold = 0.4;
	
	@Config.LangKey("config.survivalinc.sanity.staticBuzzIntensity")
	@Config.Comment({
			"The intensity of the sanity screen distortion shader and the accompanying slenderman-like sound effect",
			"When this setting is set to 0, an alternative vanilla-compatible nausea effect is used instead",
			"WARNING: The shader has been reported to have very nauseating effects on photosensitive people",
			"People suffering from extreme photosensitivity are strongly advised to turn this setting off."})
	@Config.RangeDouble(min = 0, max = 1)
	@Config.RequiresMcRestart
	public double staticBuzzIntensity = 1;
	
	@Config.LangKey("config.survivalinc.sanity.sleepDeprivationMin")
	@Config.Comment("The longest time (in ticks) the player can stay awake without suffering from sleep deprivation")
	@Config.RangeInt(min = 1)
	public int sleepDeprivationMin = 24000;
	
	@Config.LangKey("config.survivalinc.sanity.sleepDeprivationMax")
	@Config.Comment("The time (in ticks) at which sleep deprivation sanity drain rate reaches it's peak")
	@Config.RangeInt(min = 2)
	public int sleepDeprivationMax = 120000;
	
	@Config.LangKey("config.survivalinc.sanity.sleepDeprivationDebuff")
	@Config.Comment("The rate at which sanity is drained after the player stays awake for <sleepDeprivationMax> ticks.")
	@Config.RangeDouble(min = 0)
	public double sleepDeprivationDebuff = 0.04;
}
