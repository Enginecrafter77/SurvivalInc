package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:sanity")
public class SanityConfig {
	
	@Config.LangKey("config.survivalinc:sanity.enable")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc:sanity.wetnessAnnoyanceThreshold")
	@Config.RangeDouble(min = 0, max = 100)
	public double wetnessAnnoyanceThreshold = 50;
	
	@Config.LangKey("config.survivalinc:sanity.darkSpookFactorBase")
	@Config.RangeDouble(min = 1E-4, max = 0.1)
	public double darkSpookFactorBase = 0.05;
	
	@Config.LangKey("config.survivalinc:sanity.comfortLightLevel")
	@Config.RangeInt(min = 0, max = 15)
	public int comfortLightLevel = 7;
	
	@Config.LangKey("config.survivalinc:sanity.nighttimeDrain")
	@Config.RangeDouble(min = 1E-3)
	public double nighttimeDrain = 0.0075;
	
	@Config.LangKey("config.survivalinc:sanity.friendlyMobBonus")
	@Config.RangeDouble(min = 1E-3)
	public double friendlyMobBonus = 0.006;
	
	@Config.LangKey("config.survivalinc:sanity.hostileMobModifier")
	@Config.RangeDouble(min = 1E-3)
	public double hostileMobModifier = 0.003;
	
	@Config.LangKey("config.survivalinc:sanity.tamedMobMultiplier")
	@Config.RangeDouble(min = 0)
	public double tamedMobMultiplier = 4;
	
	@Config.LangKey("config.survivalinc:sanity.animalTameBoost")
	@Config.RangeDouble(min = 0)
	public double animalTameBoost = 4;
	
	@Config.LangKey("config.survivalinc:sanity.sleepRestoration")
	@Config.RangeDouble(min = 0, max = 1)
	public double sleepResoration = 0.3;
	
	@Config.LangKey("config.survivalinc:sanity.hallucinationThreshold")
	@Config.RangeDouble(min = 0, max = 1)
	public double hallucinationThreshold = 0.4;
}
