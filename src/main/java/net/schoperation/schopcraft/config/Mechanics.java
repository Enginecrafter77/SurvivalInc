package net.schoperation.schopcraft.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.schopcraft:mechanics")
public class Mechanics {

    @Config.LangKey("config.schopcraft:mechanics.enableGhost")
    @Config.RequiresWorldRestart
    public boolean enableGhost = true;

    @Config.LangKey("config.schopcraft:mechanics.enableTemperature")
    @Config.RequiresWorldRestart
    public boolean enableTemperature = true;

    @Config.LangKey("config.schopcraft:mechanics.enableThirst")
    @Config.RequiresWorldRestart
    public boolean enableThirst = true;

    @Config.LangKey("config.schopcraft:mechanics.enableSanity")
    @Config.RequiresWorldRestart
    public boolean enableSanity = true;

    @Config.LangKey("config.schopcraft:mechanics.enableWetness")
    @Config.RequiresWorldRestart
    public boolean enableWetness = true;

    @Config.LangKey("config.schopcraft:mechanics.temperatureScale")
    @Config.RequiresWorldRestart
    @Config.RangeDouble(min = 0.1, max = 3.0)
    public double temperatureScale = 1.0;

    @Config.LangKey("config.schopcraft:mechanics.thirstScale")
    @Config.RequiresWorldRestart
    @Config.RangeDouble(min = 0.1, max = 3.0)
    public double thirstScale = 1.0;

    @Config.LangKey("config.schopcraft:mechanics.sanityScale")
    @Config.RequiresWorldRestart
    @Config.RangeDouble(min = 0.1, max = 3.0)
    public double sanityScale = 1.0;

    @Config.LangKey("config.schopcraft:mechanics.wetnessScale")
    @Config.RequiresWorldRestart
    @Config.RangeDouble(min = 0.1, max = 3.0)
    public double wetnessScale = 1.0;
}
