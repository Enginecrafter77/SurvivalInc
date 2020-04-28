package enginecrafter77.survivalinc.config;

import enginecrafter77.survivalinc.season.SnowMeltingController;
import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:seasons")
public class SeasonConfig {

	@Config.LangKey("config.survivalinc:seasons.enable")
	@Config.RequiresMcRestart
	public boolean enabled = true;

	@Config.LangKey("config.survivalinc:seasons.durations")
	@Config.RequiresMcRestart
	public int[] durations = {14, 14, 14, 14};
	
	@Config.LangKey("config.survivalinc:seasons.lengths")
	@Config.RequiresMcRestart
	public double[] temperatures = {-0.25F, 0.1F, 0.3F, -0.05F};
	
	@Config.LangKey("config.survivalinc:seasons.meltController")
	@Config.RequiresMcRestart
	public SnowMeltingController meltController = SnowMeltingController.SIMPLE;
}
