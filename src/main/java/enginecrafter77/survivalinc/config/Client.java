package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:client")
public class Client {

	@Config.LangKey("config.survivalinc:client.showCelsius")
	public boolean showCelsius = false;

	@Config.LangKey("config.survivalinc:client.showSips")
	public boolean showSipsInDurabilityBar = false;

	@Config.LangKey("config.survivalinc:client.barTransparency")
	@Config.RangeDouble(min = 0, max = 1)
	public double barTransparency = 0;
}