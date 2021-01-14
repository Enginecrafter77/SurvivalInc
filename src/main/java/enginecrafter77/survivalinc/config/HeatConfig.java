package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc.heat")
public class HeatConfig {
	
	@Config.LangKey("config.survivalinc.heat.enable")
	@Config.Comment("Setting this to true enables heat mechanics")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc.heat.exchangeFactor")
	@Config.Comment("The rate at which the player's heat moves towards the environment's temperature")
	@Config.RangeDouble(min = 0, max = 1)
	public double heatExchangeFactor = 0.002;
	
	@Config.LangKey("config.survivalinc.heat.gaussScaling")
	@Config.Comment({"A gauss curve scaling number.", "Higher values mean sharper curves."})
	@Config.RangeDouble(min = 0)
	public double gaussScaling = 1.5;
	
	@Config.LangKey("config.survivalinc.heat.blockScanRange")
	@Config.Comment("The maximum distance radiant heat from blocks propagates through")
	@Config.RangeInt(min = 0)
	public double blockScanRange = 4;
	
	@Config.LangKey("config.survivalinc.heat.distributionVector")
	@Config.Comment({"States how different armor pieces contribute to the set's conductivity multiplier", "For example, chestplate has higher surface area, so it should have bigger impact on the conductivity than the boots", "The entered vector is normalized before being put to use"})
	@Config.RangeDouble(min = 0, max = Double.MAX_VALUE / 4D)
	public double[] distributionVector = {0.2, 0.35, 0.3, 0.15};
	
	@Config.LangKey("config.survivalinc.heat.caveTemperature")
	@Config.Comment("The uniform undeground temperature")
	@Config.RangeDouble(min = 0)
	public double caveTemperature = 0.7F;
	
	@Config.LangKey("config.survivalinc.heat.tempCoefficient")
	@Config.Comment({"A coefficient to turn the biome's temperature to the one processed by Survival Inc.", "60 is normally a good choice; Schoperation used 78."})
	@Config.RangeDouble(min = 0)
	public double tempCoefficient = 60; // 78 = Schoperation's constant
	
	@Config.LangKey("config.survivalinc.heat.wetnessExchangeMultiplier")
	@Config.Comment({"The heat exchange rate multiplier applied when wetness is at it's maximum", "This option is ignored when wetness is disabled"})
	@Config.RangeDouble(min = 0)
	public double wetnessExchangeMultiplier = 4;
	
	@Config.LangKey("config.survivalinc.heat.fireDuration")
	@Config.Comment({"The duration of fire applied when the player's heat exceeds the threshold", "Setting this to 0 disables the fire, and opts for direct damage instead."})
	@Config.RangeInt(min = 0)
	public int fireDuration = 1;
	
	@Config.LangKey("config.survivalinc.heat.damageAmount")
	@Config.Comment("The damage applied if fireDuration is set to 0.")
	@Config.RangeDouble(min = 0)
	public double damageAmount = 1D;
	
	@Config.LangKey("config.survivalinc.heat.blockHeatMap")
	@Config.Comment({"A map of blocks and their core heat.", "See the wiki for how this value is affecting the radiant heat"})
	@Config.RequiresMcRestart
	public String[] blockHeatMap = {
			"minecraft:lava 400",
			"minecraft:flowing_lava 350",
			"minecraft:magma 300",
			"minecraft:fire 200",
			"minecraft:lit_furnace 100",
			"minecraft:lit_pumpkin 80"
	};
	
	@Config.LangKey("config.survivalinc.heat.armorMaterialConductivity")
	@Config.Comment({"A map of armor types and their heat conductivity.", "This value is split between individual pieces using distributionVector"})
	@Config.RequiresMcRestart
	public String[] armorMaterialConductivity = {
			"leather 0.3",
			"chain 1.1",
			"iron 1.2",
			"gold 1.5",
			"diamond 2.25"
	};
}
