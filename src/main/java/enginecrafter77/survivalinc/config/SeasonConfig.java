package enginecrafter77.survivalinc.config;

import enginecrafter77.survivalinc.season.melting.MeltingController;
import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:seasons")
public class SeasonConfig {

	@Config.LangKey("config.survivalinc:seasons.enable")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc:seasons.durations")
	public int[] durations = {14, 14, 14, 14};
	
	@Config.LangKey("config.survivalinc:seasons.temperatures")
	@Config.RequiresWorldRestart
	public double[] temperatures = {-1F, 0.1F, 0.5F, -0.1F};
	
	@Config.LangKey("config.survivalinc:seasons.meltController")
	@Config.RequiresMcRestart
	public MeltingController meltController = MeltingController.FANCY;
}
