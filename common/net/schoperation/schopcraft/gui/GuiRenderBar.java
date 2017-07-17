package net.schoperation.schopcraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.schoperation.schopcraft.SchopCraft;

public class GuiRenderBar extends Gui {
	
	// resource locations of bars.
	private final ResourceLocation tempBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/tempbar.png");
	private final ResourceLocation thirstBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/thirstbar.png");
	private final ResourceLocation sanityBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/sanitybar.png");
	private final ResourceLocation wetnessBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/wetnessbar.png");
	private final int textureWidth = 100, textureHeight = 11, barWidth = 80;
	
	// stats for rendering
	private static float wetness = 0.00f;
	private static float maxWetness = 100.00f;
	private static float thirst = 100.00f;
	private static float maxThirst = 100.00f;
	private static float sanity = 100.00f;
	private static float maxSanity = 100.00f;
	private static float temperature = 68.0f;
	private static float maxTemperature = 120.0f;
	private static float targetTemperature = 68.0f;
	
	// these methods are to get the correct stats of the player.
	public static void getServerThirst(float newThirst, float newMaxThirst) {
		
		thirst = newThirst;
		maxThirst = newMaxThirst;
	}
	
	public static void getServerSanity(float newSanity, float newMaxSanity) {
		
		sanity = newSanity;
		maxSanity = newMaxSanity;
	}
	
	public static void getServerWetness(float newWetness, float newMaxWetness) {
		
		wetness = newWetness;
		maxWetness = newMaxWetness;
	}
	
	public static void getServerTemperature(float newTemperature, float newMaxTemperature, float newTargetTemperature) {
		
		temperature = newTemperature;
		maxTemperature = newMaxTemperature;
		targetTemperature = newTargetTemperature;
	}
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event) {
		
		if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
			
			// instance of Minecraft. All of this crap is client-side (well of course)
			Minecraft mc = Minecraft.getMinecraft();
			
			// get current screen resolution.
			ScaledResolution scaled = new ScaledResolution(mc);
			int screenWidth = scaled.getScaledWidth();
			int screenHeight = scaled.getScaledHeight();
			
			// determine width of WETNESS bar.
			double oneWetnessUnit = (double) barWidth / maxWetness; // default 0.8
			int currentWidthWetness = (int) (oneWetnessUnit * wetness);
			double roundedWetness = (double) (Math.round(wetness * 10)) / 10;
			String textWetness = Double.toString(roundedWetness) + "%";
			
			// determine width of THIRST bar.
			double oneThirstUnit = (double) barWidth / maxThirst; // default 0.8
			int currentWidthThirst = (int) (oneThirstUnit * thirst);
			double roundedThirst = (double) (Math.round(thirst * 10)) / 10;
			String textThirst = Double.toString(roundedThirst) + "%";
			
			// determine width of SANITY bar.
			double oneSanityUnit = (double) barWidth / maxSanity; // default 0.8, could change
			int currentWidthSanity = (int) (oneSanityUnit * sanity);
			double roundedSanity = (double) (Math.round(sanity * 10)) / 10;
			String textSanity = Double.toString(roundedSanity);
			
			// determine width of TEMPERATURE bar.
			double oneTemperatureUnit = (double) barWidth / maxTemperature; // default 0.66
			int currentWidthTemperature = (int) (oneTemperatureUnit * temperature);
			double roundedTemperature = (double) (Math.round(temperature * 10)) / 10;
			String textTemperature = Double.toString(roundedTemperature) + "°F";
			
			// only show bars if the f3 debug screen isn't showing.
			if (!mc.gameSettings.showDebugInfo) {
				
				// top rect is bar, bottom rect is outline/icon
				// TEMPERATURE
				mc.renderEngine.bindTexture(tempBar);
				drawTexturedModalRect(screenWidth-barWidth-2, screenHeight-(screenHeight/2)-20, 19, 14, currentWidthTemperature, textureHeight);
				drawTexturedModalRect(screenWidth-textureWidth-1, screenHeight-(screenHeight/2)-23, 0, 0, textureWidth, textureHeight);
				drawCenteredString(mc.fontRenderer, textTemperature, screenWidth-textureWidth-16, screenHeight-(screenHeight/2)-20, Integer.parseInt("FFFFFF", 16));
				
				// THIRST
				mc.renderEngine.bindTexture(thirstBar);
				drawTexturedModalRect(screenWidth-barWidth-2, screenHeight-(screenHeight/2), 19, 14, currentWidthThirst, textureHeight);
				drawTexturedModalRect(screenWidth-textureWidth-1, screenHeight-(screenHeight/2)-3, 0, 0, textureWidth, textureHeight);
				drawCenteredString(mc.fontRenderer, textThirst, screenWidth-textureWidth-16, screenHeight-(screenHeight/2), Integer.parseInt("FFFFFF", 16));
				
				// SANITY
				mc.renderEngine.bindTexture(sanityBar);
				drawTexturedModalRect(screenWidth-barWidth-2, screenHeight-(screenHeight/2)+20, 19, 14, currentWidthSanity, textureHeight);
				drawTexturedModalRect(screenWidth-textureWidth-1, screenHeight-(screenHeight/2)+17, 0, 0, textureWidth, textureHeight);
				drawCenteredString(mc.fontRenderer, textSanity, screenWidth-textureWidth-16, screenHeight-(screenHeight/2)+20, Integer.parseInt("FFFFFF", 16));
				
				// WETNESS
				// only show wetness if there is wetness. This is in place so wetness isn't confused with thirst.
				if (wetness > 0) {
					
					mc.renderEngine.bindTexture(wetnessBar);
					drawTexturedModalRect(screenWidth-barWidth-2, screenHeight-(screenHeight/2)+40, 19, 14, currentWidthWetness, textureHeight);
					drawTexturedModalRect(screenWidth-textureWidth-1, screenHeight-(screenHeight/2)+37, 0, 0, textureWidth, textureHeight);
					drawCenteredString(mc.fontRenderer, textWetness, screenWidth-textureWidth-16, screenHeight-(screenHeight/2)+40, Integer.parseInt("FFFFFF", 16));
				}
			}
		}
	}
}