package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:client")
public class ClientConfig {
	@Config.LangKey("config.survivalinc:client.barTransparency")
	@Config.RangeDouble(min = 0, max = 1)
	public double barTransparency = 0;
	
	@Config.LangKey("config.survivalinc:client.autumnLeafColor")
	@Config.RangeDouble(min = 0)
	public double[] autumnLeafColor = {1.2, 0.6, 0.8};
	
	@Config.LangKey("config.survivalinc:client.statBarPosition")
	public double[] statBarPosition = {0.5, 1, 95, -64};
	
	@Config.LangKey("config.survivalinc:client.linearArrow")
	public boolean linearArrow = false;
	
	@Config.LangKey("config.survivalinc:client.pulsatingGhosts")
	public boolean pulsatingGhosts = true;
}