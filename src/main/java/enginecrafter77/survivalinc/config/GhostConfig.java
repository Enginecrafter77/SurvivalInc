package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc:ghost")
public class GhostConfig {
	@Config.LangKey("config.survivalinc:ghost.enable")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc:ghost.enableInteraction")
	public boolean enableInteraction = true;
	
	@Config.LangKey("config.survivalinc:ghost.interactionThreshold")
	@Config.RangeDouble(min = 0D, max = 100D)
	public double interactionThreshold = 40D;
	
	@Config.LangKey("config.survivalinc:ghost.interactionSubclassing")
	public boolean interactionSubclassing = false;
	
	@Config.LangKey("config.survivalinc:ghost.resurrectionBlocksMovement")
	public boolean resurrectionBlocksMovement = true;
	
	@Config.LangKey("config.survivalinc:ghost.resurrectionDuration")
	@Config.RangeInt(min = 60)
	public int resurrectionDuration = 100;
	
	@Config.LangKey("config.survivalinc:ghost.interactionCost")
	@Config.RequiresMcRestart
	public double interactionCost = 10D;
	
	@Config.LangKey("config.survivalinc:ghost.passiveNightRegen")
	@Config.RequiresMcRestart
	public double passiveNightRegen = 0.05D;
	
	@Config.LangKey("config.survivalinc:ghost.sprintingEnergyDrain")
	@Config.RequiresMcRestart
	public double sprintingEnergyDrain = 0.1D;
	
	@Config.LangKey("config.survivalinc:ghost.allowFlying")
	@Config.RequiresMcRestart
	public boolean allowFlying = true;
	
	@Config.LangKey("config.survivalinc:ghost.flyingThreshold")
	@Config.RangeDouble(min = 0, max = 100)
	public double flyingThreshold = 25;
	
	@Config.LangKey("config.survivalinc:ghost.flyingDrain")
	public double flyingDrain = 0.075D;
}
