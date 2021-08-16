package enginecrafter77.survivalinc;

import java.util.function.Predicate;

import org.lwjgl.util.Point;
import org.lwjgl.util.Rectangle;

import enginecrafter77.survivalinc.client.AbsoluteElementPositioner;
import enginecrafter77.survivalinc.client.Direction2D;
import enginecrafter77.survivalinc.client.ElementRenderFilter;
import enginecrafter77.survivalinc.client.HUDConstructEvent;
import enginecrafter77.survivalinc.client.HideRenderFilter;
import enginecrafter77.survivalinc.client.StatFillBar;
import enginecrafter77.survivalinc.client.TextureResource;
import enginecrafter77.survivalinc.client.TranslateRenderFilter;
import enginecrafter77.survivalinc.config.ModConfig;
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
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.impl.HeatModifier;
import enginecrafter77.survivalinc.stats.impl.HydrationModifier;
import enginecrafter77.survivalinc.stats.impl.SanityModifier;
import enginecrafter77.survivalinc.stats.impl.SanityRecord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
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

public class ClientProxy extends CommonProxy {
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
		
		HUDConstructEvent hce = new HUDConstructEvent();
		MinecraftForge.EVENT_BUS.post(hce);
		if(!hce.isCanceled()) this.hud = hce.buildHUD();
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
		
		HUDConstructEvent hce = new HUDConstructEvent();
		MinecraftForge.EVENT_BUS.post(hce);
		if(!hce.isCanceled()) this.hud = hce.buildHUD();
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
		
		if(ModConfig.HEAT.enabled)
		{
			StatFillBar<SimpleStatRecord> bar = new StatFillBar<SimpleStatRecord>(HeatModifier.instance, Direction2D.UP, newicons.region(new Rectangle(0, 18, 9, 16)));
			bar.addLayer(newicons.region(new Rectangle(9, 18, 9, 16)), SimpleStatRecord::getNormalizedValue);
			bar.setCapacity(1);
			
			event.addElement(bar, new AbsoluteElementPositioner(origin_x, origin_y, ModConfig.CLIENT.hud.heatIconX, ModConfig.CLIENT.hud.heatIconY)).setTrigger(ElementType.EXPERIENCE);
			event.addRenderStageFilter(new TranslateRenderFilter(new Point(0, -10)), ElementType.SUBTITLES);
		}
		if(ModConfig.HYDRATION.enabled)
		{
			StatFillBar<SimpleStatRecord> bar = new StatFillBar<SimpleStatRecord>(HydrationModifier.instance, ModConfig.CLIENT.hud.hydrationBarDirection, newicons.region(new Rectangle(0, 9, 9, 9)));
			bar.addLayer(newicons.region(new Rectangle(9, 9, 9, 9)), SimpleStatRecord::getNormalizedValue);
			bar.setCapacity(ModConfig.CLIENT.hud.hydrationBarCapacity);
			bar.setSpacing(ModConfig.CLIENT.hud.hydrationBarSpacing);
			
			if(ModConfig.CLIENT.hud.stackHydrationBar)
				event.addElement(bar, ModConfig.CLIENT.hud.hydrationBarStack).setTrigger(ModConfig.CLIENT.hud.hydrationBarRenderTrigger).addFilter(TextureResetFilter.INSTANCE);
			else
				event.addElement(bar, new AbsoluteElementPositioner(origin_x, origin_y, ModConfig.CLIENT.hud.hydrationBarX, ModConfig.CLIENT.hud.hydrationBarY));
		}
		if(ModConfig.SANITY.enabled)
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
		if(ModConfig.GHOST.enabled)
		{
			Predicate<StatTracker> isGhostActive = (StatTracker tracker) -> tracker.getRecord(GhostProvider.instance).isActive();
			event.addElement(new GhostEnergyBar(), new AbsoluteElementPositioner(0.5F, 1F, -91, -39));
			event.addRenderStageFilter(new HideRenderFilter<StatTracker>(isGhostActive), ElementType.HEALTH, ElementType.AIR, ElementType.ARMOR, ElementType.FOOD);
		}
	}
	
	/**
	 * A little nifty hack to reset the texture to default minecraft icons when the rendering is done.
	 * Using this filter is recommended when using render triggers other than {@link ElementType#ALL}.
	 * This filter basically ensures that the vanilla icon pack is re-bound after the rendering is done,
	 * preventing the game from rendering missing textures.
	 * @author Enginecrafter77
	 */
	private static enum TextureResetFilter implements ElementRenderFilter<Object>
	{
		INSTANCE;
		
		@Override
		public boolean begin(ScaledResolution resoultion, Object arg)
		{
			return true;
		}

		@Override
		public void end(ScaledResolution resoultion, Object arg)
		{
			Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
		}
	}
}