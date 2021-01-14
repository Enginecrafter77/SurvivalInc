package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:client")
public class ClientConfig {	
	@Config.LangKey("config.survivalinc:client.autumnLeafColor")
	@Config.RangeDouble(min = 0)
	public double[] autumnLeafColor = {1.2, 0.6, 0.8};
	
	@Config.LangKey("config.survivalinc:client.pulsatingGhosts")
	public boolean pulsatingGhosts = true;
	
	@Config.Name("hud")
	public HUDConfig hud = new HUDConfig();
}