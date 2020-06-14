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
	public int comfortLightLevel = 4;
	
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
	public double animalTameBoost = 20;
	
	@Config.LangKey("config.survivalinc:sanity.sleepRestoration")
	@Config.RangeDouble(min = 0, max = 1)
	public double sleepResoration = 0.3;
	
	@Config.LangKey("config.survivalinc:sanity.hallucinationThreshold")
	@Config.RangeDouble(min = 0, max = 1)
	public double hallucinationThreshold = 0.4;
	
	@Config.LangKey("config.survivalinc:sanity.foodMap")
	@Config.RequiresMcRestart
	public String[] foodSanityMap = {
			"minecraft:chicken -5",
			"minecraft:beef -5",
			"minecraft:rabbit -5",
			"minecraft:mutton -5",
			"minecraft:porkchop -5",
			"minecraft:fish -5",
			"minecraft:rotten_flesh -10",
			"minecraft:spider_eye -15",
			"minecraft:cooked_chicken 2",
			"minecraft:cooked_beef 2",
			"minecraft:cooked_rabbit 2",
			"minecraft:cooked_mutton 2",
			"minecraft:cooked_porkchop 2",
			"minecraft:cooked_fish 2",
			"minecraft:pumpkin_pie 10",
			"minecraft:rabbit_stew 6",
			"minecraft:mushroom_stew 6",
			"minecraft:beetroot_soup 5.75",
			"minecraft:cookie 7.5"
	};
}
