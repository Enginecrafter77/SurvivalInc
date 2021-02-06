package enginecrafter77.survivalinc.config;

import enginecrafter77.survivalinc.client.Direction2D;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config.LangKey("config.survivalinc.client.hud")
@SideOnly(Side.CLIENT)
public class HUDConfig {
	
	@Config.LangKey("config.survivalinc.client.hud.moveArmorBar")
	@Config.Comment("Enable moving the Armor bar up to free up space when heat is enabled")
	public boolean moveArmorBar = true;
	
	@Config.LangKey("config.survivalinc.client.hud.moveAirBar")
	@Config.Comment("Enable moving the Air bubble bar up to free up space when hydration is enabled")
	public boolean moveAirBar = true;
	
	@Config.LangKey("config.survivalinc.client.hud.originX")
	@Config.Comment("The X-axis origin of the HUD represented as a fraction from the current screen resolution")
	@Config.RangeDouble(min = 0, max = 1)
	public double originX = 0.5D;
	
	@Config.LangKey("config.survivalinc.client.hud.originY")
	@Config.Comment("The X-axis origin of the HUD represented as a fraction from the current screen resolution")
	@Config.RangeDouble(min = 0, max = 1)
	public double originY = 1D;
	
	@Config.LangKey("config.survivalinc.client.hud.renderTrigger")
	@Config.Comment({"The element type to render the HUD in.", "Only change this if you are absolutely sure about what you are doing."})
	@Config.RequiresMcRestart
	public ElementType renderTrigger = ElementType.ALL;
	
	@Config.LangKey("config.survivalinc.client.hud.heatBarDirection")
	@Config.Comment({"The fill direction of the heat bar.", "For example, setting this to RIGHT means that the bar fills from left to right."})
	public Direction2D heatBarDirection = Direction2D.RIGHT;
	
	@Config.LangKey("config.survivalinc.client.hud.hydrationBarDirection")
	@Config.Comment({"The fill direction of the hydration bar.", "For example, setting this to RIGHT means that the bar fills from left to right."})
	public Direction2D hydrationBarDirection = Direction2D.LEFT;
	
	@Config.LangKey("config.survivalinc.client.hud.heatBarX")
	@Config.Comment("The X coordinate of the top left corner of the heat bar relative to the computed origin point")
	public int heatBarX = -91;
	
	@Config.LangKey("config.survivalinc.client.hud.heatBarY")
	@Config.Comment("The Y coordinate of the top left corner of the heat bar relative to the computed origin point")
	public int heatBarY = -49;
	
	@Config.LangKey("config.survivalinc.client.hud.hydrationBarY")
	@Config.Comment("The X coordinate of the top left corner of the hydration bar relative to the computed origin point")
	public int hydrationBarX = 10;
	
	@Config.LangKey("config.survivalinc.client.hud.hydrationBarX")
	@Config.Comment("The Y coordinate of the top left corner of the hydration bar relative to the computed origin point")
	public int hydrationBarY = -49;
	
	@Config.LangKey("config.survivalinc.client.hud.sanityIconX")
	@Config.Comment("The X coordinate of the top left corner of the sanity icon relative to the computed origin point")
	public int sanityIconX = -8;
	
	@Config.LangKey("config.survivalinc.client.hud.sanityIconY")
	@Config.Comment("The Y coordinate of the top left corner of the sanity icon relative to the computed origin point")
	public int sanityIconY = -51;
	
	@Config.LangKey("config.survivalinc.client.hud.heatBarCapacity")
	@Config.Comment("The number of individual icons in the heat bar")
	@Config.RangeInt(min = 1)
	public int heatBarCapacity = 10;
	
	@Config.LangKey("config.survivalinc.client.hud.heatBarSpacing")
	@Config.Comment({"The spacing between the icons in the heat bar in pixels", "Positive values pull the icons apart, negative values cram the icons together"})
	public int heatBarSpacing = -1;
	
	@Config.LangKey("config.survivalinc.client.hud.hydrationBarCapacity")
	@Config.Comment("The number of individual icons in the hydration bar")
	@Config.RangeInt(min = 1)
	public int hydrationBarCapacity = 10;
	
	@Config.LangKey("config.survivalinc.client.hud.hydrationBarSpacing")
	@Config.Comment({"The spacing between the icons in the hydration bar in pixels", "Positive values pull the icons apart, negative values cram the icons together"})
	public int hydrationBarSpacing = -1;
}
