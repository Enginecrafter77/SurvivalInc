package enginecrafter77.survivalinc.client;

import enginecrafter77.survivalinc.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class StatBar {

	/*
	 * Defines a statistic bar to be displayed on the GUI, the HUD, whatever you
	 * wanna call it.
	 */

	// Type
	public enum StatType {
		TEMPERATURE, THIRST, SANITY, WETNESS, GHOST
	}

	public final StatType type;

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

	// Values it holds (actual and max)
	private float value = 0f;
	private float maxValue = 100f;
	private boolean isGhost = false;

	public StatBar(StatType type, int fullWidth, int fullHeight, int defaultBarWidth, int movingTextureX,
			int movingTextureY, ResourceLocation texture)
	{

		this.type = type;
		this.fullWidth = fullWidth;
		this.fullHeight = fullHeight;
		this.defaultBarWidth = defaultBarWidth;
		this.movingTextureX = movingTextureX;
		this.movingTextureY = movingTextureY;
		this.texture = texture;
	}

	public void setValue(float value)
	{

		this.value = value;
	}

	public void setMaxValue(float value)
	{

		this.maxValue = value;
	}

	public void setGhost(boolean bool)
	{

		this.isGhost = bool;
	}

	public int getFullWidth()
	{

		return this.fullWidth;
	}

	public int getFullHeight()
	{

		return this.fullHeight;
	}

	public int getMovingTextureX()
	{

		return this.movingTextureX;
	}

	public int getMovingTextureY()
	{

		return this.movingTextureY;
	}

	public int getFullBarWidth()
	{

		return this.defaultBarWidth;
	}

	public ResourceLocation getTexture()
	{

		return this.texture;
	}

	// Should this bar even be displayed?
	public boolean shouldBeDisplayed()
	{

		// Minecraft instance. Figure out if f3 debug mode is on.
		Minecraft mc = Minecraft.getMinecraft();

		boolean isDebugEnabled = mc.gameSettings.showDebugInfo;

		// Don't display most crap if debug mode is enabled.
		if (!isDebugEnabled && !isGhost)
		{
			if(type == StatType.TEMPERATURE && ModConfig.MECHANICS.enableTemperature)
			{
				return true;
			}
			else if (type == StatType.THIRST && ModConfig.MECHANICS.enableThirst)
			{
				return true;
			}
			else if (type == StatType.SANITY && ModConfig.MECHANICS.enableSanity)
			{
				return true;
			}
			else if (type == StatType.WETNESS && value > 0 && ModConfig.MECHANICS.enableWetness)
			{
				return true;
			}
			else
			{
				return false;
			}
		}

		else if (isGhost && type == StatType.GHOST)
		{

			return true;
		}

		else
		{

			return false;
		}
	}

	// Determine the width of the bar.
	public int getMovingWidth()
	{

		// One "unit". The width of the bar PER one thirst, one temperature, one
		// sanity, etc.
		double singleUnit = (double) defaultBarWidth / maxValue;

		// Multiplication of singleUnit to get the width
		int width = (int) (singleUnit * value);

		return width;
	}

	// Determine text to be displayed.
	public String getTextToDisplay()
	{

		// Round the actual value
		double roundedValue = (double) (Math.round(value * 10)) / 10;

		String text;

		// String
		// Is this temperature? Then we might have to convert.
		if (type == StatType.TEMPERATURE)
		{

			if (ModConfig.CLIENT.showCelsius)
			{

				float tempInCelsius = (value - 32) / 1.8f;
				roundedValue = (double) (Math.round(tempInCelsius * 10)) / 10;
				text = Double.toString(roundedValue) + "°C";
			}

			else
			{

				text = Double.toString(roundedValue) + "°F";
			}
		}

		else if (type == StatType.THIRST || type == StatType.WETNESS)
		{

			text = Double.toString(roundedValue) + "%";
		}

		else if (type == StatType.GHOST)
		{

			text = Double.toString(roundedValue) + " GPU";
		}

		else
		{

			text = Double.toString(roundedValue);
		}

		return text;
	}
}