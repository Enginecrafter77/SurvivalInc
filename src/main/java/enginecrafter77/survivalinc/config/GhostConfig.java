package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:ghost")
public class GhostConfig {
	@Config.LangKey("config.survivalinc:ghost.enable")
	@Config.RequiresMcRestart
	public boolean enabled = false;
}
