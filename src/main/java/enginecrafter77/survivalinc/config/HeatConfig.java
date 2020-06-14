package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:heat")
public class HeatConfig {
	
	@Config.LangKey("config.survivalinc:heat.enable")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc:heat.exchangeFactor")
	@Config.RangeDouble(min = 0, max = 1)
	public double heatExchangeFactor = 0.002;
	
	@Config.LangKey("config.survivalinc:heat.gaussScaling")
	@Config.RangeDouble(min = 0)
	public double gaussScaling = 1.5;
	
	@Config.LangKey("config.survivalinc:heat.blockScanRange")
	@Config.RangeInt(min = 0)
	public double blockScanRange = 4;
	
	@Config.LangKey("config.survivalinc:heat.distributionVector")
	@Config.RangeDouble(min = 0, max = Double.MAX_VALUE / 4D)
	public double[] distributionVector = {0.2, 0.35, 0.3, 0.15};
	
	@Config.LangKey("config.survivalinc:heat.caveTemperature")
	@Config.RangeDouble(min = 0)
	public double caveTemperature = 0.7F;
	
	@Config.LangKey("config.survivalinc:heat.tempCoefficient")
	@Config.RangeDouble(min = 0)
	public double tempCoefficient = 60; // 78 = Schoperation's constant
	
	@Config.LangKey("config.survivalinc:heat.wetnessExchangeMultiplier")
	@Config.RangeDouble(min = 0)
	public double wetnessExchangeMultiplier = 4;
	
	@Config.LangKey("config.survivalinc:heat.blockHeatMap")
	@Config.RequiresMcRestart
	public String[] blockHeatMap = {
			"minecraft:lava 400",
			"minecraft:flowing_lava 350",
			"minecraft:magma 300",
			"minecraft:fire 200",
			"minecraft:lit_furnace 100",
			"minecraft:lit_pumpkin 80"
	};
	
	@Config.LangKey("config.survivalinc:heat.armorMaterialConductivity")
	@Config.RequiresMcRestart
	public String[] armorMaterialConductivity = {
			"leather 0.3",
			"chain 1.1",
			"iron 1.2",
			"gold 1.5",
			"diamond 2.25"
	};
}
