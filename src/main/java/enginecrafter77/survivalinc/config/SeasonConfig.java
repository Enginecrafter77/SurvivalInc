package enginecrafter77.survivalinc.config;

import enginecrafter77.survivalinc.season.SnowMeltingController;
import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:seasons")
public class SeasonConfig {

	@Config.LangKey("config.survivalinc:seasons.enable")
	@Config.RequiresMcRestart
	public boolean enabled = true;

	@Config.LangKey("config.survivalinc:seasons.winterLength")
	@Config.RequiresMcRestart
	public int winterLength = 14;

	@Config.LangKey("config.survivalinc:seasons.springLength")
	@Config.RequiresMcRestart
	public int springLength = 14;

	@Config.LangKey("config.survivalinc:seasons.summerLength")
	@Config.RequiresMcRestart
	public int summerLength = 14;

	@Config.LangKey("config.survivalinc:seasons.autumnLength")
	@Config.RequiresMcRestart
	public int autumnLength = 14;
	
	@Config.LangKey("config.survivalinc:seasons.meltController")
	@Config.RequiresMcRestart
	public SnowMeltingController meltController = SnowMeltingController.SIMPLE;
}
