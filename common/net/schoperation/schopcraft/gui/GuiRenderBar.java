package net.schoperation.schopcraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.schoperation.schopcraft.SchopCraft;
import net.schoperation.schopcraft.config.ModConfig;

public class GuiRenderBar extends Gui {
	
	// Resource locations of bars.
	private final ResourceLocation tempBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/tempbar.png");
	private final ResourceLocation thirstBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/thirstbar.png");
	private final ResourceLocation sanityBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/sanitybar.png");
	private final ResourceLocation wetnessBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/wetnessbar.png");
	private final ResourceLocation ghostEnergyBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/ghostenergybar.png");
	private final int defaultTextureWidth = 100, defaultTextureHeight = 11, defaultBarWidth = 80;
	private final int ghostTextureWidth = 200, ghostTextureHeight = 9, ghostBarWidth = 187;
	
	// Stats for rendering
	private static float wetness = 0.00f, maxWetness = 100.00f;
	private static float thirst = 100.00f, maxThirst = 100.00f;
	private static float sanity = 100.00f, maxSanity = 100.00f;
	private static float temperature = 68.00f, maxTemperature = 120.00f, targetTemperature = 68.00f;
	
	private static boolean isGhost = false;
	private static float ghostEnergy = 0.00f, maxGhostEnergy = 100.00f;
	
	// This method gets the correct stats of the player. The min parameters will be kept here just in case they're needed in the future.
	public static void getServerStats(float newTemperature, float newMaxTemperature, float newMinTemperature, float newTargetTemperature, float newThirst, float newMaxThirst, float newMinThirst, float newSanity, float newMaxSanity, float newMinSanity, float newWetness, float newMaxWetness, float newMinWetness, boolean newIsGhost, float newGhostEnergy) {
		
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
			ScaledResolution scaled = new ScaledResolution(mc);
			int screenWidth = scaled.getScaledWidth();
			int screenHeight = scaled.getScaledHeight();
			
			// Determine width of WETNESS bar.
			double oneWetnessUnit = (double) defaultBarWidth / maxWetness; // default 0.8
			int currentWidthWetness = (int) (oneWetnessUnit * wetness);
			
			// Show wetness value.
			double roundedWetness = (double) (Math.round(wetness * 10)) / 10;
			String textWetness = Double.toString(roundedWetness) + "%";
			
			// Determine width of THIRST bar.
			double oneThirstUnit = (double) defaultBarWidth / maxThirst; // default 0.8
			int currentWidthThirst = (int) (oneThirstUnit * thirst);
			
			// Show thirst value.
			double roundedThirst = (double) (Math.round(thirst * 10)) / 10;
			String textThirst = Double.toString(roundedThirst) + "%";
			
			// Determine width of SANITY bar.
			double oneSanityUnit = (double) defaultBarWidth / maxSanity; // default 0.8, could change
			int currentWidthSanity = (int) (oneSanityUnit * sanity);
			
			// Show sanity value.
			double roundedSanity = (double) (Math.round(sanity * 10)) / 10;
			String textSanity = Double.toString(roundedSanity);
			
			// Determine width of GHOST ENERGY bar.
			double oneEnergyUnit = (double) ghostBarWidth / maxGhostEnergy;
			int currentWidthEnergy = (int) (oneEnergyUnit * ghostEnergy);
			
			// Show ghost energy value.
			double roundedEnergy = (double) (Math.round(ghostEnergy * 10)) / 10;
			String textEnergy = Double.toString(roundedEnergy) + " GPU";
			
			// Determine width of TEMPERATURE bar.
			// This is also determined whether the user wants Celsius or Fahrenheit.
			// Fields for showing temp (due to config file).
			int currentWidthTemperature;
			String textTemperature;
			
			if (ModConfig.showCelsius) {
				
				// NEW temperature values to show (the actual will never change).
				double temperatureCelsius = (double) ((temperature - 32) / 1.8);
				double maxTemperatureCelsius = (double) ((maxTemperature - 32) / 1.8);
				
				// Determine width.
				double oneTemperatureUnit = (double) defaultBarWidth / maxTemperatureCelsius; // default ~0.61
				currentWidthTemperature = (int) (oneTemperatureUnit * temperatureCelsius);
				
				// Show temperature value.
				double roundedTemperature = (double) (Math.round(temperatureCelsius * 10)) / 10;
				textTemperature = Double.toString(roundedTemperature) + "°C";
			}
			else {
				
				// Use current temperature values (as they are set in fahrenheit in the mod itself).
				double oneTemperatureUnit = (double) defaultBarWidth / maxTemperature; // default 0.66 repeating
				currentWidthTemperature = (int) (oneTemperatureUnit * temperature);
				
				// Show temperature value.
				double roundedTemperature = (double) (Math.round(temperature * 10)) / 10;
				textTemperature = Double.toString(roundedTemperature) + "°F";
			}
			
			// Only show the main bars if the F3 debug screen is now showing, and if the player is not a ghost.
			if (!mc.gameSettings.showDebugInfo && !isGhost) {
				
				// Top rect is bar, bottom rect is outline/icon.
				// TEMPERATURE
				mc.renderEngine.bindTexture(tempBar);
				drawTexturedModalRect(screenWidth-defaultBarWidth-2, screenHeight-(screenHeight/2)-20, 19, 14, currentWidthTemperature, defaultTextureHeight);
				drawTexturedModalRect(screenWidth-defaultTextureWidth-1, screenHeight-(screenHeight/2)-23, 0, 0, defaultTextureWidth, defaultTextureHeight);
				drawCenteredString(mc.fontRenderer, textTemperature, screenWidth-defaultTextureWidth-16, screenHeight-(screenHeight/2)-20, Integer.parseInt("FFFFFF", 16));
				
				// THIRST
				mc.renderEngine.bindTexture(thirstBar);
				drawTexturedModalRect(screenWidth-defaultBarWidth-2, screenHeight-(screenHeight/2), 19, 14, currentWidthThirst, defaultTextureHeight);
				drawTexturedModalRect(screenWidth-defaultTextureWidth-1, screenHeight-(screenHeight/2)-3, 0, 0, defaultTextureWidth, defaultTextureHeight);
				drawCenteredString(mc.fontRenderer, textThirst, screenWidth-defaultTextureWidth-16, screenHeight-(screenHeight/2), Integer.parseInt("FFFFFF", 16));
				
				// SANITY
				mc.renderEngine.bindTexture(sanityBar);
				drawTexturedModalRect(screenWidth-defaultBarWidth-2, screenHeight-(screenHeight/2)+20, 19, 14, currentWidthSanity, defaultTextureHeight);
				drawTexturedModalRect(screenWidth-defaultTextureWidth-1, screenHeight-(screenHeight/2)+17, 0, 0, defaultTextureWidth, defaultTextureHeight);
				drawCenteredString(mc.fontRenderer, textSanity, screenWidth-defaultTextureWidth-16, screenHeight-(screenHeight/2)+20, Integer.parseInt("FFFFFF", 16));
				
				// WETNESS
				// Only show wetness if there is wetness. This is in place so wetness isn't confused with thirst.
				if (wetness > 0) {
					
					mc.renderEngine.bindTexture(wetnessBar);
					drawTexturedModalRect(screenWidth-defaultBarWidth-2, screenHeight-(screenHeight/2)+40, 19, 14, currentWidthWetness, defaultTextureHeight);
					drawTexturedModalRect(screenWidth-defaultTextureWidth-1, screenHeight-(screenHeight/2)+37, 0, 0, defaultTextureWidth, defaultTextureHeight);
					drawCenteredString(mc.fontRenderer, textWetness, screenWidth-defaultTextureWidth-16, screenHeight-(screenHeight/2)+40, Integer.parseInt("FFFFFF", 16));
				}
			}
			
			// Ghost energy bar, for when the player is a ghost. Right above their hotbar.
			else if (isGhost) {
				
				// Top rect is bar, bottom rect is outline/icon.
				mc.renderEngine.bindTexture(ghostEnergyBar);
				drawTexturedModalRect((screenWidth / 2)-93, screenHeight-50, 12, 10, currentWidthEnergy, ghostTextureHeight);
				drawTexturedModalRect((screenWidth / 2)-105, screenHeight-52, 0, 0, ghostTextureWidth, ghostTextureHeight);
				drawCenteredString(mc.fontRenderer, textEnergy, screenWidth / 2, screenHeight-60, Integer.parseInt("FFFFFF", 16));
			}
		}
	}
}