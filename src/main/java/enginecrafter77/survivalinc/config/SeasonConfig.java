package enginecrafter77.survivalinc.config;

import enginecrafter77.survivalinc.season.melting.MeltingController;
import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc.seasons")
public class SeasonConfig {

	@Config.LangKey("config.survivalinc.seasons.enable")
	@Config.Comment("Setting this to true enables the sesons")
	@Config.RequiresMcRestart
	public final boolean enabled = true;
	
	@Config.LangKey("config.survivalinc.seasons.startingSeason")
	@Config.Comment("The season the player starts in")
	@Config.RequiresWorldRestart
	public final String startingSeason = "survivalinc:spring";
	
	@Config.LangKey("config.survivalinc.seasons.startingDay")
	@Config.Comment("The day in the target season the player starts in")
	@Config.RangeInt(min = 0)
	@Config.RequiresWorldRestart
	public final int startingDay = 6;
	
	@Config.LangKey("config.survivalinc.seasons.durations")
	@Config.Comment({"The lengths of individual seasons.", "ORDER: Winter, Spring, Summer, Autumn"})
	@Config.RangeInt(min = 1)
	public final int[] durations = {14, 14, 14, 14};
	
	@Config.LangKey("config.survivalinc.seasons.temperatures")
	@Config.Comment({"The temperatures of individual seasons.", "ORDER: Winter, Spring, Summer, Autumn"})
	@Config.RequiresWorldRestart
	public final double[] temperatures = {-1F, 0.1F, 0.5F, -0.1F};
	
	@Config.LangKey("config.survivalinc.seasons.meltController")
	@Config.Comment({"The melting controller that is responsible for melting and freezing the meltable blocks", "See the wiki for what each MeltingController offers."})
	@Config.RequiresMcRestart
	public final MeltingController meltController = MeltingController.FANCY;
}
