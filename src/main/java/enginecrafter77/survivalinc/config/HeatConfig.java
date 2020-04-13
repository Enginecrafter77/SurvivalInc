package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:heat")
public class HeatConfig {
	
	@Config.LangKey("config.survivalinc:heat.enable")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc:heat.scale")
	@Config.RequiresMcRestart
	@Config.RangeDouble(min = 0.1, max = 3.0)
	public double scale = 1.0;
	
	@Config.LangKey("config.survivalinc:heat.exchangeFactor")
	@Config.RangeDouble(min = 0, max = 1)
	public double heatExchangeFactor = 0.0003;
	
	@Config.LangKey("config.survivalinc:heat.gaussScaling")
	@Config.RangeDouble(min = 0)
	public double gaussScaling = 1.5;
	
	@Config.LangKey("config.survivalinc:heat.blockScanRange")
	@Config.RangeInt(min = 0)
	public double blockScanRange = 4;
	
}
