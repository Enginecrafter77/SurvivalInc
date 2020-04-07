package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:seasons")
public class Seasons {

	@Config.LangKey("config.survivalinc:seasons.enableSeasons")
	@Config.RequiresWorldRestart
	public boolean aenableSeasons = true;

	@Config.LangKey("config.survivalinc:seasons.enableDayLength")
	@Config.RequiresWorldRestart
	public boolean aenableDayLength = true;

	@Config.LangKey("config.survivalinc:seasons.winterLength")
	@Config.RequiresWorldRestart
	public int winterLength = 14;

	@Config.LangKey("config.survivalinc:seasons.springLength")
	@Config.RequiresWorldRestart
	public int springLength = 14;

	@Config.LangKey("config.survivalinc:seasons.summerLength")
	@Config.RequiresWorldRestart
	public int summerLength = 14;

	@Config.LangKey("config.survivalinc:seasons.autumnLength")
	@Config.RequiresWorldRestart
	public int autumnLength = 14;
}
