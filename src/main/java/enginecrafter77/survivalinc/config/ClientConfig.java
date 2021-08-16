package enginecrafter77.survivalinc.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config.LangKey("config.survivalinc.client")
@SideOnly(Side.CLIENT)
public class ClientConfig {	
	@Config.LangKey("config.survivalinc.client.autumnLeafColor")
	@Config.Comment("The color multiplier of leaves during autumn and winter")
	@Config.RangeDouble(min = 0)
	public double[] autumnLeafColor = {1.2, 0.6, 0.8};
	
	@Config.LangKey("config.survivalinc.client.pulsatingGhosts")
	@Config.Comment("True to enable ghosts opacity to pulse. Setting this to false may improve the FPS a little bit")
	public boolean pulsatingGhosts = true;
	
	@Config.LangKey("config.survivalinc.client.heatVignette")
	@Config.Comment("True to vignette-based heat indicator")
	public boolean heatVignette = true;
	
	@Config.LangKey("config.survivalinc.client.logarithmicHeatVignette")
	@Config.Comment("True to enable less linear scaling of heat vignette opacity. False to force linear behavior")
	public boolean logarithmicHeatVignette = true;
	
	@Config.Name("hud")
	@Config.LangKey("config.survivalinc.client.hud")
	@Config.Comment("HUD customization and settings")
	public HUDConfig hud = new HUDConfig();
}