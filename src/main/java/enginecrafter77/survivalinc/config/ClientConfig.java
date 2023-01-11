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
	public final double[] autumnLeafColor = {1.2, 0.6, 0.8};
	
	@Config.LangKey("config.survivalinc.client.pulsatingGhosts")
	@Config.Comment("True to enable ghosts opacity to pulse. Setting this to false may improve the FPS a little bit")
	public final boolean pulsatingGhosts = true;
	
	@Config.Name("hud")
	@Config.LangKey("config.survivalinc.client.hud")
	@Config.Comment("HUD customization and settings")
	public final HUDConfig hud = new HUDConfig();
	
	@Config.Name("vignette")
	@Config.LangKey("config.survivalinc.client.vignette")
	@Config.Comment("Vignette customization and settings")
	public final VignetteConfig vignette = new VignetteConfig();
}
