package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:client")
public class ClientConfig {
	@Config.LangKey("config.survivalinc:client.barTransparency")
	@Config.RangeDouble(min = 0, max = 1)
	public double barTransparency = 0;
}