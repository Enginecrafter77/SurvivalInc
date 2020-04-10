package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:seasons")
public class Seasons {

	@Config.LangKey("config.survivalinc:seasons.enableSeasons")
	@Config.RequiresMcRestart
	public boolean aenableSeasons = true;

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
}
