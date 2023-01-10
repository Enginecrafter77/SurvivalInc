package enginecrafter77.survivalinc.config;

import enginecrafter77.survivalinc.client.Direction2D;
import enginecrafter77.survivalinc.client.StackingElementLayoutFunction;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config.LangKey("config.survivalinc.client.hud")
@SideOnly(Side.CLIENT)
public class HUDConfig {
	
	@Config.LangKey("config.survivalinc.client.hud.originX")
	@Config.Comment("The X-axis origin of the HUD represented as a fraction from the current screen resolution")
	@Config.RangeDouble(min = 0, max = 1)
	public double originX = 0.5D;
	
	@Config.LangKey("config.survivalinc.client.hud.originY")
	@Config.Comment("The X-axis origin of the HUD represented as a fraction from the current screen resolution")
	@Config.RangeDouble(min = 0, max = 1)
	public double originY = 1D;
	
	@Config.LangKey("config.survivalinc.client.hud.stackSanityBar")
	@Config.Comment({"Set to true to enable sanity bar stacking.", "The stacking basically means that the bar is dynamically integrated into the vanilla HUD.", "Setting this to false enables absolute positioning using sanityBarX and sanityBarY"})
	public boolean stackSanityBar = true;
	
	@Config.LangKey("config.survivalinc.client.hud.stackHydrationBar")
	@Config.Comment({"Set to true to enable hydration bar stacking.", "The stacking basically means that the bar is dynamically integrated into the vanilla HUD.", "Setting this to false enables absolute positioning using hydrationBarX and hydrationBarY"})
	public boolean stackHydrationBar = true;
	
	@Config.LangKey("config.survivalinc.client.hud.sanityBarStack")
	@Config.Comment("The side where the sanity bar will be stacked in")
	public StackingElementLayoutFunction sanityBarStack = StackingElementLayoutFunction.LEFT;
	
	@Config.LangKey("config.survivalinc.client.hud.hydrationBarStack")
	@Config.Comment("The side where the hydration bar will be stacked in")
	public StackingElementLayoutFunction hydrationBarStack = StackingElementLayoutFunction.RIGHT;
	
	@Config.LangKey("config.survivalinc.client.hud.sanityBarRenderTrigger")
	@Config.Comment({"The element to render the sanity bar after.", "This value is only relevant when sanity bar stacking is enabled, and indicates the element the sanity bar follows."})
	public ElementType sanityBarRenderTrigger = ElementType.HEALTH;
	
	@Config.LangKey("config.survivalinc.client.hud.hydrationBarRenderTrigger")
	@Config.Comment({"The element to render the hydration bar after.", "This value is only relevant when hydration bar stacking is enabled, and indicates the element the hydration bar follows."})
	public ElementType hydrationBarRenderTrigger = ElementType.FOOD;
	
	@Config.LangKey("config.survivalinc.client.hud.sanityBarDirection")
	@Config.Comment({"The fill direction of the sanity bar.", "For example, setting this to RIGHT means that the bar fills from left to right."})
	public Direction2D sanityBarDirection = Direction2D.RIGHT;
	
	@Config.LangKey("config.survivalinc.client.hud.hydrationBarDirection")
	@Config.Comment({"The fill direction of the hydration bar.", "For example, setting this to RIGHT means that the bar fills from left to right."})
	public Direction2D hydrationBarDirection = Direction2D.LEFT;
	
	@Config.LangKey("config.survivalinc.client.hud.sanityBarX")
	@Config.Comment("The X coordinate of the top left corner of the sanity bar relative to the computed origin point")
	public int sanityBarX = -91;
	
	@Config.LangKey("config.survivalinc.client.hud.sanityBarY")
	@Config.Comment("The Y coordinate of the top left corner of the sanity bar relative to the computed origin point")
	public int sanityBarY = -49;
	
	@Config.LangKey("config.survivalinc.client.hud.hydrationBarY")
	@Config.Comment("The X coordinate of the top left corner of the hydration bar relative to the computed origin point")
	public int hydrationBarX = 10;
	
	@Config.LangKey("config.survivalinc.client.hud.hydrationBarX")
	@Config.Comment("The Y coordinate of the top left corner of the hydration bar relative to the computed origin point")
	public int hydrationBarY = -49;
	
	@Config.LangKey("config.survivalinc.client.hud.heatIconX")
	@Config.Comment("The X coordinate of the top left corner of the thermometer icon relative to the computed origin point")
	public int heatIconX = -4;
	
	@Config.LangKey("config.survivalinc.client.hud.heatIconY")
	@Config.Comment("The Y coordinate of the top left corner of the thermometer icon relative to the computed origin point")
	public int heatIconY = -51;
	
	@Config.LangKey("config.survivalinc.client.hud.sanityBarCapacity")
	@Config.Comment("The number of individual icons in the sanity bar")
	@Config.RangeInt(min = 1)
	public int sanityBarCapacity = 10;
	
	@Config.LangKey("config.survivalinc.client.hud.sanityBarSpacing")
	@Config.Comment({"The spacing between the icons in the sanity bar in pixels", "Positive values pull the icons apart, negative values squash the icons together"})
	public int sanityBarSpacing = -1;
	
	@Config.LangKey("config.survivalinc.client.hud.hydrationBarCapacity")
	@Config.Comment("The number of individual icons in the hydration bar")
	@Config.RangeInt(min = 1)
	public int hydrationBarCapacity = 10;
	
	@Config.LangKey("config.survivalinc.client.hud.hydrationBarSpacing")
	@Config.Comment({"The spacing between the icons in the hydration bar in pixels", "Positive values pull the icons apart, negative values cram the icons together"})
	public int hydrationBarSpacing = -1;

	@Config.LangKey("config.survivalinc.client.hud.ghostEnergyRenderTrigger")
	@Config.Comment({"The element to render the ghost energy bar after.", "May help with compatibility with some mods."})
	public ElementType ghostEnergyRenderTrigger = ElementType.HEALTH;
}
