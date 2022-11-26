package enginecrafter77.survivalinc;

import com.google.common.collect.Range;
import enginecrafter77.survivalinc.client.*;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.ghost.GhostConditionRenderFilter;
import enginecrafter77.survivalinc.ghost.GhostEnergyBar;
import enginecrafter77.survivalinc.ghost.GhostProvider;
import enginecrafter77.survivalinc.ghost.RenderGhost;
import enginecrafter77.survivalinc.net.EntityItemUpdateMessage;
import enginecrafter77.survivalinc.net.EntityItemUpdater;
import enginecrafter77.survivalinc.net.StatSyncHandler;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import enginecrafter77.survivalinc.season.LeafColorer;
import enginecrafter77.survivalinc.season.SeasonController;
import enginecrafter77.survivalinc.season.SeasonSyncMessage;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.impl.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.util.Color;
import org.lwjgl.util.Point;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.Rectangle;

import javax.annotation.Nullable;

public class ClientProxy extends CommonProxy {
	/** The HUD instance currently in use, if any */
	@Nullable
	private HUDConstructEvent.RenderHUD hud;
	
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		if(ModConfig.SEASONS.enabled) MinecraftForge.EVENT_BUS.register(LeafColorer.instance);
	}
	
	@Override
	public void registerClientHandlers()
	{
		this.net.registerMessage(StatSyncHandler.class, StatSyncMessage.class, 0, Side.CLIENT);
		this.net.registerMessage(SeasonController::onSyncDelivered, SeasonSyncMessage.class, 1, Side.CLIENT);
		this.net.registerMessage(EntityItemUpdater.class, EntityItemUpdateMessage.class, 2, Side.CLIENT);
	}
	
	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		this.rebuildHUD();
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
		
		// Register Ghost event handler
		if(ModConfig.GHOST.enabled) MinecraftForge.EVENT_BUS.register(new RenderGhost());
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.PostConfigChangedEvent event)
	{
		if(!event.getModID().equals(SurvivalInc.MOD_ID)) return;
		this.rebuildHUD();
	}
	
	// A delegate event handler for RenderHUD#renderOverlayPre
	@SubscribeEvent
	public void renderOverlayPre(RenderGameOverlayEvent.Pre event)
	{
		if(this.hud != null) this.hud.renderOverlayPre(event);
	}
	
	// A delegate event handler for RenderHUD#renderOverlayPost
	@SubscribeEvent
	public void renderOverlayPost(RenderGameOverlayEvent.Post event)
	{
		if(this.hud != null) this.hud.renderOverlayPost(event);
	}
	
	// Fix the chat position so it's always above the last stacked element
	@SubscribeEvent
	public void translateChat(RenderGameOverlayEvent.Chat event)
	{
		int height = Math.max(GuiIngameForge.left_height, GuiIngameForge.right_height) - 1;
		event.setPosY(event.getResolution().getScaledHeight() - height);
	}
	
	@SubscribeEvent
	public void constructHUD(HUDConstructEvent event)
	{
		float origin_x = (float)ModConfig.CLIENT.hud.originX;
		float origin_y = (float)ModConfig.CLIENT.hud.originY;
		
		TextureResource newicons = new TextureResource(new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/staticons.png"), 18, 34);
		
		if(HeatModifier.loaded())
		{
			StatFillBar<SimpleStatRecord> bar = new StatFillBar<SimpleStatRecord>(HeatModifier.instance, Direction2D.UP, newicons.region(new Rectangle(0, 18, 9, 16)));
			bar.addLayer(newicons.region(new Rectangle(9, 18, 9, 16)), SimpleStatRecord::getNormalizedValue);
			bar.setCapacity(1);
			
			event.addElement(bar, new AbsoluteElementPositioner(origin_x, origin_y, ModConfig.CLIENT.hud.heatIconX, ModConfig.CLIENT.hud.heatIconY)).setTrigger(ElementType.EXPERIENCE);
			event.addRenderStageFilter(new TranslateRenderFilter(new Point(0, -10)), ElementType.SUBTITLES);
			
			if(ModConfig.CLIENT.vignette.enable)
			{
				event.addElement(new StatRangeVignette(HeatModifier.instance, Range.lessThan(35F), ClientProxy.parseColor(ModConfig.CLIENT.vignette.coldColor), ModConfig.CLIENT.vignette.maxOpacity, ModConfig.CLIENT.vignette.logarithmicOpacity, true), AbsoluteElementPositioner.ORIGIN).addFilter(new ScaleRenderFilter(Vec2f.MAX));
				event.addElement(new StatRangeVignette(HeatModifier.instance, Range.greaterThan(85F), ClientProxy.parseColor(ModConfig.CLIENT.vignette.hotColor), ModConfig.CLIENT.vignette.maxOpacity, ModConfig.CLIENT.vignette.logarithmicOpacity, false), AbsoluteElementPositioner.ORIGIN).addFilter(new ScaleRenderFilter(Vec2f.MAX));
			}
		}
		if(HydrationModifier.loaded())
		{
			StatFillBar<SimpleStatRecord> bar = new StatFillBar<SimpleStatRecord>(HydrationModifier.instance, ModConfig.CLIENT.hud.hydrationBarDirection, newicons.region(new Rectangle(0, 9, 9, 9)));
			bar.addLayer(newicons.region(new Rectangle(9, 9, 9, 9)), SimpleStatRecord::getNormalizedValue);
			bar.setCapacity(ModConfig.CLIENT.hud.hydrationBarCapacity);
			bar.setSpacing(ModConfig.CLIENT.hud.hydrationBarSpacing);
			
			if(ModConfig.CLIENT.hud.stackHydrationBar)
				event.addElement(bar, ModConfig.CLIENT.hud.hydrationBarStack).setTrigger(ModConfig.CLIENT.hud.hydrationBarRenderTrigger).addFilter(TextureResetFilter.INSTANCE);
			else
				event.addElement(bar, new AbsoluteElementPositioner(origin_x, origin_y, ModConfig.CLIENT.hud.hydrationBarX, ModConfig.CLIENT.hud.hydrationBarY));
			
			if(ModConfig.CLIENT.vignette.enable)
				event.addElement(new StatRangeVignette(HydrationModifier.instance, Range.lessThan(30F), ClientProxy.parseColor(ModConfig.CLIENT.vignette.dehydrationColor), ModConfig.CLIENT.vignette.maxOpacity, ModConfig.CLIENT.vignette.logarithmicOpacity, true), AbsoluteElementPositioner.ORIGIN).addFilter(new ScaleRenderFilter(Vec2f.MAX));
		}
		if(SanityModifier.loaded())
		{
			StatFillBar<SanityRecord> bar = new StatFillBar<SanityRecord>(SanityModifier.instance, ModConfig.CLIENT.hud.sanityBarDirection, newicons.region(new Rectangle(0, 0, 9, 9)));
			bar.addLayer(newicons.region(new Rectangle(9, 0, 9, 9)), SimpleStatRecord::getNormalizedValue);
			bar.setCapacity(ModConfig.CLIENT.hud.sanityBarCapacity);
			bar.setSpacing(ModConfig.CLIENT.hud.sanityBarSpacing);
			
			if(ModConfig.CLIENT.hud.stackSanityBar)
				event.addElement(bar, ModConfig.CLIENT.hud.sanityBarStack).setTrigger(ModConfig.CLIENT.hud.sanityBarRenderTrigger).addFilter(TextureResetFilter.INSTANCE);
			else
				event.addElement(bar, new AbsoluteElementPositioner(origin_x, origin_y, ModConfig.CLIENT.hud.sanityBarX, ModConfig.CLIENT.hud.sanityBarY));
		}
		if(GhostProvider.loaded())
		{
			event.addElement(new GhostEnergyBar(), StackingElementPositioner.LEFT).setTrigger(ElementType.HOTBAR).addFilter(GhostConditionRenderFilter.INSTANCE);
			event.addRenderStageFilter(GhostConditionRenderFilter.INSTANCE, ElementType.HEALTH, ElementType.AIR, ElementType.ARMOR, ElementType.FOOD);
		}
	}
	
	private void rebuildHUD()
	{
		HUDConstructEvent hce = new HUDConstructEvent();
		MinecraftForge.EVENT_BUS.post(hce);
		if(!hce.isCanceled()) this.hud = hce.buildHUD();
	}
	
	/**
	 * Parses color from a HTML color notation. (#RRGGBBAA)
	 * @param hex A HTML notation of color
	 * @return A read-only color object representing the color
	 */
	public static ReadableColor parseColor(String hex)
	{
		if(hex.startsWith("#")) hex = hex.substring(1);
		int bundle = Integer.parseUnsignedInt(hex, 16);
		
		// If the alpha is defined, the whole bundle is shifted to left.
		// This tests for the shift and if the 16 MSB are 0, shift it in place.
		if((bundle & 0xFF000000) == 0L)
		{
			bundle <<= 8; // Shift RGB to RGBA
			bundle |= 0xFF; // Set alpha to 255
		}
		
		return new Color((bundle & 0xFF000000) >> 24, (bundle & 0x00FF0000) >> 16, (bundle & 0x0000FF00) >> 8, bundle & 0x000000FF);
	}
	
	/**
	 * A little nifty hack to reset the texture to default minecraft icons when the rendering is done.
	 * Using this filter is recommended when using render triggers other than {@link ElementType#ALL}.
	 * This filter basically ensures that the vanilla icon pack is re-bound after the rendering is done,
	 * preventing the game from rendering missing textures.
	 * @author Enginecrafter77
	 */
	private static enum TextureResetFilter implements ElementRenderFilter
	{
		INSTANCE;

		@Override
		public boolean begin(RenderFrameContext context, OverlayElement element)
		{
			return true;
		}

		@Override
		public void end(RenderFrameContext context, OverlayElement element)
		{
			Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
		}
	}
}
