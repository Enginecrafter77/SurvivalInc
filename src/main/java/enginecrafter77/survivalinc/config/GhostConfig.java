package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;

@Config.LangKey("config.survivalinc.ghost")
public class GhostConfig {
	@Config.LangKey("config.survivalinc.ghost.enable")
	@Config.Comment("Set to true to enable ghosts")
	@Config.RequiresMcRestart
	public boolean enabled = true;
	
	@Config.LangKey("config.survivalinc.ghost.enableInteraction")
	@Config.Comment("Setting this to false disables any ghost interaction with the world whatsoever")
	public boolean enableInteraction = true;
	
	@Config.LangKey("config.survivalinc.ghost.interactionThreshold")
	@Config.Comment("The minimum amount of ghost energy the ghost needs to have to be able to interact with the world")
	@Config.RangeDouble(min = 0D, max = 100D)
	public double interactionThreshold = 40D;
	
	@Config.LangKey("config.survivalinc.ghost.interactionSubclassing")
	@Config.Comment({"Setting this to true enables subclass-checking the blocks in addition to direct checking.", "This option serves mainly for resolving compatibility issues, and may have negligible impact on performance"})
	public boolean interactionSubclassing = false;
	
	@Config.LangKey("config.survivalinc.ghost.resurrectionBlocksMovement")
	@Config.Comment("Setting this to true makes ghosts stay still while they're being resurrected")
	public boolean resurrectionBlocksMovement = true;
	
	@Config.LangKey("config.survivalinc.ghost.resurrectionDuration")
	@Config.Comment("The time in ticks it takes for ghosts to resurrect")
	@Config.RangeInt(min = 60)
	public int resurrectionDuration = 200;
	
	@Config.LangKey("config.survivalinc.ghost.interactionCost")
	@Config.Comment({"The base cost of all interactions.", "Interaction processors use this as a base value to compute the interaction cost based on complexity of the interaction"})
	@Config.RequiresMcRestart
	public double interactionCost = 10D;
	
	@Config.LangKey("config.survivalinc.ghost.passiveNightRegen")
	@Config.Comment("The rate at which ghost energy replenishes during night")
	@Config.RequiresMcRestart
	public double passiveNightRegen = 0.05D;
	
	@Config.LangKey("config.survivalinc.ghost.sprintingEnergyDrain")
	@Config.Comment("The rate at which ghost energy is drained when sprinting/using boost")
	@Config.RequiresMcRestart
	public double sprintingEnergyDrain = 0.1D;
	
	@Config.LangKey("config.survivalinc.ghost.allowFlying")
	@Config.Comment("Setting this to true allows ghosts with enough energy to fly")
	@Config.RequiresMcRestart
	public boolean allowFlying = true;
	
	@Config.LangKey("config.survivalinc.ghost.flyingThreshold")
	@Config.Comment({"The minimum amount of energy to enable flying.", "Below this threshold, ghosts will be unable to start flying", "Ghosts that are already flying will drop to the ground"})
	@Config.RangeDouble(min = 0, max = 100)
	public double flyingThreshold = 25;
	
	@Config.LangKey("config.survivalinc.ghost.flyingDrain")
	@Config.Comment({"The rate at which ghost energy is drained when flying", "Normally, this should be a little more than the night regeneration rate."})
	public double flyingDrain = 0.075D;
}
