package net.schoperation.schopcraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.schoperation.schopcraft.config.SchopConfig;

public class StatBar {
	
	/*
	 * Defines a statistic bar to be displayed on the GUI, the HUD, whatever you wanna call it.
	 */
	
	// Type
	public enum StatType { TEMPERATURE, THIRST, SANITY, WETNESS, GHOST; }
	private StatType type;
	
	// Texture
	private ResourceLocation texture;
	
	// Full width and height
	private int fullWidth;
	private int fullHeight;
	
	// Full width of the bar that actually MOVES.
	private int defaultBarWidth;
	
	// Starting position of the moving bar in the texture file.
	private int movingTextureX;
	private int movingTextureY;
	
	// Is it already being rendered?
	private boolean isAlreadyRendered = false;
	
	public StatBar(StatType type, int fullWidth, int fullHeight, int defaultBarWidth, int movingTextureX, int movingTextureY, ResourceLocation texture) {
		
		this.type = type;
		this.fullWidth = fullWidth;
		this.fullHeight = fullHeight;
		this.defaultBarWidth = defaultBarWidth;
		this.movingTextureX = movingTextureX;
		this.movingTextureY = movingTextureY;
		this.texture = texture;
	}
	
	public int getFullWidth() {
		
		return this.fullWidth;
	}
	
	public int getFullHeight() {
		
		return this.fullHeight;
	}
	
	public int getMovingTextureX() {
		
		return this.movingTextureX;
	}
	
	public int getMovingTextureY() {
		
		return this.movingTextureY;
	}
	
	public int getFullBarWidth() {
		
		return this.defaultBarWidth;
	}
	
	public ResourceLocation getTexture() {
		
		return this.texture;
	}
	
	public void setAlreadyRendered() {
		
		this.isAlreadyRendered = true;
	}
	
	public void unsetAlreadyRendered() {
		
		this.isAlreadyRendered = false;
	}
	
	public boolean isAlreadyRendered() {
		
		return this.isAlreadyRendered;
	}
	
	// Should this bar even be displayed?
	public boolean shouldBeDisplayed(boolean isGhost, float value) {
		
		// Minecraft instance. Figure out if f3 debug mode is on.
		Minecraft mc = Minecraft.getMinecraft();
		
		boolean isDebugEnabled = mc.gameSettings.showDebugInfo;
		
		// Don't display most crap if debug mode is enabled.
		if (!isDebugEnabled && !isGhost) {
			
			if (type == StatType.TEMPERATURE && SchopConfig.mechanics.enableTemperature) {
			
				return true;
			}
			
			else if (type == StatType.THIRST && SchopConfig.mechanics.enableThirst) {
				
				return true;
			}
			
			else if (type == StatType.SANITY && SchopConfig.mechanics.enableSanity) {
				
				return true;
			}
			
			else if (type == StatType.WETNESS && value > 0 && SchopConfig.mechanics.enableWetness) {
				
				return true;
			}
			
			else {
				
				return false;
			}
		}
		
		else if (isGhost) {
			
			if (type == StatType.GHOST && SchopConfig.mechanics.enableGhost) {
			
				return true;
			}
			
			else {
				
				return false;
			}
		}
		
		else {
			
			return false;
		}
	}
	
	// Determine the width of the bar.
	public int getMovingWidth(float value, float maxValue) {

		// One "unit". The width of the bar PER one thirst, one temperature, one sanity, etc.
		double singleUnit = (double) defaultBarWidth / maxValue;
		
		// Multiplication of singleUnit to get the width
		int width = (int) (singleUnit * value);
		
		return width;
	}
	
	// Determine text to be displayed.
	public String getTextToDisplay(float value) {
		
		// Round the actual value
		double roundedValue = (double) (Math.round(value * 10)) / 10;
		
		String text;
		
		// String
		// Is this temperature? Then we might have to convert.
		if (type == StatType.TEMPERATURE) {
			
			if (SchopConfig.client.showCelsius) {
				
				float tempInCelsius = (value - 32) / 1.8f;
				roundedValue = (double) (Math.round(tempInCelsius * 10)) / 10;
				text = Double.toString(roundedValue) + "°C";
			}
			
			else {
				
				text = Double.toString(roundedValue) + "°F";
			}
		}
		
		else if (type == StatType.THIRST || type == StatType.WETNESS) {
			
			text = Double.toString(roundedValue) + "%";
		}
		
		else if (type == StatType.GHOST) {
			
			text = Double.toString(roundedValue) + " GPU";
		}
		
		else {
			
			text = Double.toString(roundedValue);
		}
		
		return text;
	}
}