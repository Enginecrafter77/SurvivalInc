package enginecrafter77.survivalinc;

import enginecrafter77.survivalinc.client.*;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.ghost.RenderGhost;
import enginecrafter77.survivalinc.net.*;
import enginecrafter77.survivalinc.season.LeafSeasonalTintApplicator;
import enginecrafter77.survivalinc.season.SeasonSyncMessage;
import enginecrafter77.survivalinc.season.SeasonSyncRequest;
import enginecrafter77.survivalinc.stats.impl.HydrationModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

import javax.annotation.Nullable;

public class ClientProxy implements SurvivalIncProxy {
	public static final TextureResource STAT_ICONS = new TextureResource(new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/staticons.png"), 18, 34);

	/**
	 * A little nifty hack to reset the texture to default minecraft icons when the rendering is done.
	 * Using this filter is recommended when using render triggers other than {@link ElementType#ALL}.
	 * This filter basically ensures that the vanilla icon pack is re-bound after the rendering is done,
	 * preventing the game from rendering missing textures.
	 */
	public static final ElementRenderFilter TEXTURE_RESET_FILTER = new ElementRenderFilter() {
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
	};

	/** The HUD instance currently in use, if any */
	@Nullable
	private HUDConstructEvent.RenderHUD hud;

	@Override
	public void registerRendering()
	{
		if(ModConfig.SEASONS.enabled) MinecraftForge.EVENT_BUS.register(LeafSeasonalTintApplicator.INSTANCE);

		if(ModConfig.GHOST.enabled) MinecraftForge.EVENT_BUS.register(new RenderGhost());
	}
	
	@Override
	public void registerNetworkHandlers(SimpleNetworkWrapper net)
	{
		net.registerMessage(StatSyncHandler.class, StatSyncMessage.class, 0, Side.CLIENT);
		net.registerMessage(SurvivalInc.seasonController::onSyncDelivered, SeasonSyncMessage.class, 1, Side.CLIENT);
		net.registerMessage(EntityItemUpdater.class, EntityItemUpdateMessage.class, 2, Side.CLIENT);
		net.registerMessage(HydrationModifier::validateMessage, WaterDrinkMessage.class, 3, Side.SERVER);
		net.registerMessage(StatSyncRequestHandler.class, StatSyncRequestMessage.class, 4, Side.SERVER);
		net.registerMessage(SurvivalInc.seasonController::onSyncRequest, SeasonSyncRequest.class, 5, Side.SERVER);
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.PostConfigChangedEvent event)
	{
		if(!event.getModID().equals(SurvivalInc.MOD_ID)) return;
		this.createHUD();
	}

	@Nullable
	@Override
	public Object getAuxiliaryEventHandler()
	{
		return this;
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
	
	// Fix the chat position so that it's always above the last stacked element
	@SubscribeEvent
	public void translateChat(RenderGameOverlayEvent.Chat event)
	{
		int height = Math.max(GuiIngameForge.left_height, GuiIngameForge.right_height) - 1;
		event.setPosY(event.getResolution().getScaledHeight() - height);
	}

	@Override
	public void createHUD()
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
}
