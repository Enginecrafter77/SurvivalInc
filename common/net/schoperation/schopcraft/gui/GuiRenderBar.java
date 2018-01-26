package net.schoperation.schopcraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.schoperation.schopcraft.SchopCraft;
import net.schoperation.schopcraft.config.SchopConfig;
import net.schoperation.schopcraft.gui.StatBar.StatType;

public class GuiRenderBar extends Gui {
	
	// Resource locations of bars.
	private final ResourceLocation temperatureTexture = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/tempbar.png");
	private final ResourceLocation thirstTexture = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/thirstbar.png");
	private final ResourceLocation sanityTexture = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/sanitybar.png");
	private final ResourceLocation wetnessTexture = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/wetnessbar.png");
	private final ResourceLocation ghostTexture = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/ghostenergybar.png");
	
	private final int defaultTextureWidth = 100, defaultTextureHeight = 11, defaultBarWidth = 80;
	private final int ghostTextureWidth = 200, ghostTextureHeight = 9, ghostBarWidth = 187;
	
	// The stat bars themselves.
	private final StatBar temperatureBar = new StatBar(StatType.TEMPERATURE, 100, 11, 80, 19, 14);
	private final StatBar thirstBar = new StatBar(StatType.THIRST, 100, 11, 80, 19, 14);
	private final StatBar sanityBar = new StatBar(StatType.SANITY, 100, 11, 80, 19, 14);
	private final StatBar wetnessBar = new StatBar(StatType.WETNESS, 100, 11, 80, 19, 14);
	private final StatBar ghostBar = new StatBar(StatType.GHOST, 200, 9, 187, 12, 10);
	
	// Stats for rendering
	private static float wetness = 0.00f, maxWetness = 100.00f;
	private static float thirst = 100.00f, maxThirst = 100.00f;
	private static float sanity = 100.00f, maxSanity = 100.00f;
	private static float temperature = 68.00f, maxTemperature = 120.00f, targetTemperature = 68.00f;
	
	private static boolean isGhost = false;
	private static float ghostEnergy = 0.00f, maxGhostEnergy = 100.00f;
	
	// This method gets the correct stats of the player.
	public static void getServerStats(float newTemperature, float newMaxTemperature, float newTargetTemperature, float newThirst, float newMaxThirst, float newSanity, float newMaxSanity, float newWetness, float newMaxWetness, boolean newIsGhost, float newGhostEnergy) {
		
		temperature = newTemperature;
		maxTemperature = newMaxTemperature;
		
		thirst = newThirst;
		maxThirst = newMaxThirst;
		
		sanity = newSanity;
		maxSanity = newMaxSanity;
		
		wetness = newWetness;
		maxWetness = newMaxWetness;
		
		isGhost = newIsGhost;
		ghostEnergy = newGhostEnergy;
	}

	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event) {
		
		if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
			
			// Instance of Minecraft. All of this crap is client-side (well of course)
			Minecraft mc = Minecraft.getMinecraft();
			
			// Get current screen resolution.
			ScaledResolution scaled = event.getResolution();
			int screenWidth = scaled.getScaledWidth();
			int screenHeight = scaled.getScaledHeight();
			
			// Render the stat bars. Go through each of the 4 spots and place a bar if applicable.
			for (int i = 0; i < 4; i++) {
				
				// Position of this bar.
				int x = getX(i);
				int y = getY(i);
				
			}

			// Top rect is bar, bottom rect is outline/icon.
			// TEMPERATURE
			if (SchopConfig.mechanics.enableTemperature) {
				
				mc.renderEngine.bindTexture(temperatureTexture);
				drawTexturedModalRect(screenWidth-defaultBarWidth-2, screenHeight-(screenHeight/2)-20, 19, 14, currentWidthTemperature, defaultTextureHeight);
				drawTexturedModalRect(screenWidth-defaultTextureWidth-1, screenHeight-(screenHeight/2)-23, 0, 0, defaultTextureWidth, defaultTextureHeight);
				drawCenteredString(mc.fontRenderer, textTemperature, screenWidth-defaultTextureWidth-16, screenHeight-(screenHeight/2)-20, Integer.parseInt("FFFFFF", 16));
			}
			
			// THIRST
			if (SchopConfig.mechanics.enableThirst) {
				
				mc.renderEngine.bindTexture(thirstTexture);
				drawTexturedModalRect(screenWidth-defaultBarWidth-2, screenHeight-(screenHeight/2), 19, 14, currentWidthThirst, defaultTextureHeight);
				drawTexturedModalRect(screenWidth-defaultTextureWidth-1, screenHeight-(screenHeight/2)-3, 0, 0, defaultTextureWidth, defaultTextureHeight);
				drawCenteredString(mc.fontRenderer, textThirst, screenWidth-defaultTextureWidth-16, screenHeight-(screenHeight/2), Integer.parseInt("FFFFFF", 16));
			}
			
			// SANITY
			if (SchopConfig.mechanics.enableSanity) {
				
				mc.renderEngine.bindTexture(sanityTexture);
				drawTexturedModalRect(screenWidth-defaultBarWidth-2, screenHeight-(screenHeight/2)+20, 19, 14, currentWidthSanity, defaultTextureHeight);
				drawTexturedModalRect(screenWidth-defaultTextureWidth-1, screenHeight-(screenHeight/2)+17, 0, 0, defaultTextureWidth, defaultTextureHeight);
				drawCenteredString(mc.fontRenderer, textSanity, screenWidth-defaultTextureWidth-16, screenHeight-(screenHeight/2)+20, Integer.parseInt("FFFFFF", 16));
			}
			
			// WETNESS
			// Only show wetness if there is wetness. This is in place so wetness isn't confused with thirst.
			if (wetness > 0 && SchopConfig.mechanics.enableWetness) {
				
				mc.renderEngine.bindTexture(wetnessTexture);
				drawTexturedModalRect(screenWidth-defaultBarWidth-2, screenHeight-(screenHeight/2)+40, 19, 14, currentWidthWetness, defaultTextureHeight);
				drawTexturedModalRect(screenWidth-defaultTextureWidth-1, screenHeight-(screenHeight/2)+37, 0, 0, defaultTextureWidth, defaultTextureHeight);
				drawCenteredString(mc.fontRenderer, textWetness, screenWidth-defaultTextureWidth-16, screenHeight-(screenHeight/2)+40, Integer.parseInt("FFFFFF", 16));
			}
			
			// Ghost energy bar, for when the player is a ghost. Right above their hotbar.
			// Top rect is bar, bottom rect is outline/icon.
			mc.renderEngine.bindTexture(ghostTexture);
			drawTexturedModalRect((screenWidth / 2)-93, screenHeight-50, 12, 10, currentWidthEnergy, ghostTextureHeight);
			drawTexturedModalRect((screenWidth / 2)-105, screenHeight-52, 0, 0, ghostTextureWidth, ghostTextureHeight);
			drawCenteredString(mc.fontRenderer, textEnergy, screenWidth / 2, screenHeight-60, Integer.parseInt("FFFFFF", 16));
		}
	}
	
	// Help determine where to place a stat bar.
	private int getX(int pos) {
		
		return 0;
	}
	
	private int getY(int pos) {
		
		return 0;
	}
}