package schoperation.schopcraft.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.schopcraft:seasons")
public class Seasons {

    @Config.LangKey("config.schopcraft:seasons.enableSeasons")
    @Config.RequiresWorldRestart
    public boolean aenableSeasons = true;

    @Config.LangKey("config.schopcraft:seasons.enableDayLength")
    @Config.RequiresWorldRestart
    public boolean aenableDayLength = true;

    @Config.LangKey("config.schopcraft:seasons.winterLength")
    @Config.RequiresWorldRestart
    public int winterLength = 14;

    @Config.LangKey("config.schopcraft:seasons.springLength")
    @Config.RequiresWorldRestart
    public int springLength = 14;

    @Config.LangKey("config.schopcraft:seasons.summerLength")
    @Config.RequiresWorldRestart
    public int summerLength = 14;

    @Config.LangKey("config.schopcraft:seasons.autumnLength")
    @Config.RequiresWorldRestart
    public int autumnLength = 14;
}
