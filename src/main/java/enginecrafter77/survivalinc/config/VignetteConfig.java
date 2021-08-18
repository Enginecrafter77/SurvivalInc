package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config.LangKey("config.survivalinc.vignette")
@SideOnly(Side.CLIENT)
public class VignetteConfig {
	@Config.LangKey("config.survivalinc.client.vignette.enable")
	@Config.Comment("Whether to use vignette-based stat value indicator")
	public boolean enable = true;
	
	@Config.LangKey("config.survivalinc.client.vignette.logarithmicOpacity")
	@Config.Comment("True to enable less linear scaling of heat vignette opacity. False to force linear behavior")
	public boolean logarithmicOpacity = true;
	
	@Config.LangKey("config.survivalinc.client.vignette.maxOpacity")
	@Config.Comment("The maximum opacity the vignette can achieve.")
	public float maxOpacity = 0.5F;
	
	@Config.LangKey("config.survivalinc.client.vignette.hotColor")
	@Config.Comment("The color (HTML notation) of vignette displayed when the player is overheating")
	public String hotColor = "#f2541f";
	
	@Config.LangKey("config.survivalinc.client.vignette.coldColor")
	@Config.Comment("The color (HTML notation) of vignette displayed when the player is freezing")
	public String coldColor = "#2eecf0";
	
	@Config.LangKey("config.survivalinc.client.vignette.dehydrationColor")
	@Config.Comment("The color (HTML notation) of vignette displayed when the player is dehydrated")
	public String dehydrationColor = "#9e9e9e";
}
