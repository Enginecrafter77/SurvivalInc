package enginecrafter77.survivalinc.client;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.SchopConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderHUD extends Gui {

	// The stat bars themselves.
	private static final StatBar TEMPERATURE_BAR = new StatBar(StatBar.StatType.TEMPERATURE, 100, 11, 80, 19, 14,
			new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/tempbar.png"));
	private static final StatBar THIRST_BAR = new StatBar(StatBar.StatType.THIRST, 100, 11, 80, 19, 14,
			new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/thirstbar.png"));
	private static final StatBar SANITY_BAR = new StatBar(StatBar.StatType.SANITY, 100, 11, 80, 19, 14,
			new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/sanitybar.png"));
	private static final StatBar WETNESS_BAR = new StatBar(StatBar.StatType.WETNESS, 100, 11, 80, 19, 14,
			new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/wetnessbar.png"));
	private static final StatBar GHOST_BAR = new StatBar(StatBar.StatType.GHOST, 200, 9, 187, 12, 10,
			new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/ghostenergybar.png"));

	// List of the main bars for easy iteration
	private static final StatBar[] MAIN_BARS = { TEMPERATURE_BAR, THIRST_BAR, SANITY_BAR, WETNESS_BAR };

	// Nothing
	private final BarBounces joke = new BarBounces();

	// This method gets the correct stats of the player.
	public static void retrieveStats(float newTemperature, float newMaxTemperature, float newThirst, float newMaxThirst,
			float newSanity, float newMaxSanity, float newWetness, float newMaxWetness, boolean newIsGhost,
			float newGhostEnergy)
	{

		TEMPERATURE_BAR.setValue(newTemperature);
		TEMPERATURE_BAR.setMaxValue(newMaxTemperature);
		TEMPERATURE_BAR.setGhost(newIsGhost);

		THIRST_BAR.setValue(newThirst);
		THIRST_BAR.setMaxValue(newMaxThirst);
		THIRST_BAR.setGhost(newIsGhost);

		SANITY_BAR.setValue(newSanity);
		SANITY_BAR.setMaxValue(newMaxSanity);
		SANITY_BAR.setGhost(newIsGhost);

		WETNESS_BAR.setValue(newWetness);
		WETNESS_BAR.setMaxValue(newMaxWetness);
		WETNESS_BAR.setGhost(newIsGhost);

		GHOST_BAR.setValue(newGhostEnergy);
		GHOST_BAR.setMaxValue(100f);
		GHOST_BAR.setGhost(newIsGhost);
	}

	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event)
	{
		if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT)
		{
			Minecraft mc = Minecraft.getMinecraft();

			// Get current screen resolution.
			ScaledResolution scaled = event.getResolution();
			int screenWidth = scaled.getScaledWidth();
			int screenHeight = scaled.getScaledHeight();

			// Variables used to render the bars.
			int x;
			int y;
			int i = 0;
			ResourceLocation texture;
			int fullWidth;
			int fullHeight;
			int movingTextureX;
			int movingTextureY;
			int fullBarWidth;
			int movingWidth;
			String text;
			
			// The loop that renders the main stat bars.
			for(StatBar bar : MAIN_BARS)
			{
				// RIGHTMOST position of this bar. This is so we can account for
				// the bar widths correctly.
				x = getX(screenWidth, i);
				y = getY(screenHeight, i);
				i++;

				// Should this bar be displayed?
				if (bar.shouldBeDisplayed())
				{

					// Get the stuff
					texture = bar.getTexture();
					fullWidth = bar.getFullWidth();
					fullHeight = bar.getFullHeight();
					movingTextureX = bar.getMovingTextureX();
					movingTextureY = bar.getMovingTextureY();
					fullBarWidth = bar.getFullBarWidth();
					movingWidth = bar.getMovingWidth();
					text = bar.getTextToDisplay();

					// Actual rendering. First one is the moving bar. Second one
					// is the whole bar.
					mc.renderEngine.bindTexture(texture);
					drawTexturedModalRect(x - fullBarWidth - 1, y + 3, movingTextureX, movingTextureY, movingWidth,
							fullHeight);
					drawTexturedModalRect(x - fullWidth, y, 0, 0, fullWidth, fullHeight);
					drawCenteredString(mc.fontRenderer, text, x - fullWidth - 15, y + 2,
							Integer.parseInt("FFFFFF", 16));
				}

				else
				{

					;
				}
			}

			// Render ghost bar normally. It's an oddity.
			if (GHOST_BAR.shouldBeDisplayed())
			{

				mc.renderEngine.bindTexture(GHOST_BAR.getTexture());
				drawTexturedModalRect((screenWidth / 2) - 93, screenHeight - 50, GHOST_BAR.getMovingTextureX(),
						GHOST_BAR.getMovingTextureY(), GHOST_BAR.getMovingWidth(), GHOST_BAR.getFullHeight());
				drawTexturedModalRect((screenWidth / 2) - 105, screenHeight - 52, 0, 0, GHOST_BAR.getFullWidth(),
						GHOST_BAR.getFullHeight());
				drawCenteredString(mc.fontRenderer, GHOST_BAR.getTextToDisplay(), screenWidth / 2, screenHeight - 60,
						Integer.parseInt("FFFFFF", 16));
			}
		}
	}

	// Help determine where to place a stat bar.
	// It's more of a base position, and will be modified for whatever texture
	// it's for.

	// This'll either be right by 0 or right by the rightmost edge of the
	// screen.
	// So pos doesn't actually matter.
	private int getX(int screenWidth, int pos)
	{

		// Figure out where the user specified to put the bars (in config)
		// From there, figure out where exactly to put the single bar, according
		// to the config value.
		// On the left of the screen
		if (SchopConfig.CLIENT.barPositions.equals("top left") || SchopConfig.CLIENT.barPositions.equals("middle left")
				|| SchopConfig.CLIENT.barPositions.equals("bottom left"))
		{

			return 135;
		}

		// Right of the screen
		else if (SchopConfig.CLIENT.barPositions.equals("top right")
				|| SchopConfig.CLIENT.barPositions.equals("middle right")
				|| SchopConfig.CLIENT.barPositions.equals("bottom right"))
		{

			return screenWidth - 2;
		}

		// For the smartasses... :)
		else if (SchopConfig.CLIENT.barPositions
				.equals("Include the space. If you mess up, it'll default to middle right."))
		{

			return joke.getJokeX(screenWidth, pos);
		}

		// "middle right" by default.
		else
		{

			return screenWidth - 2;
		}
	}

	// The stat bars are 20 pixels apart, vertically.
	private int getY(int screenHeight, int pos)
	{

		// Top of the screen
		if (SchopConfig.CLIENT.barPositions.equals("top left") || SchopConfig.CLIENT.barPositions.equals("top right"))
		{

			// Is this the 1st bar? 2nd bar? etc.
			return 10 + (20 * pos);
		}

		// Middle of the screen
		else if (SchopConfig.CLIENT.barPositions.equals("middle left")
				|| SchopConfig.CLIENT.barPositions.equals("middle right"))
		{

			return (screenHeight / 2) - 30 + (20 * pos);
		}

		// Bottom of the screen
		else if (SchopConfig.CLIENT.barPositions.equals("bottom left")
				|| SchopConfig.CLIENT.barPositions.equals("bottom right"))
		{

			return screenHeight - 80 + (20 * pos);
		}

		// For the smartasses... :)
		else if (SchopConfig.CLIENT.barPositions
				.equals("Include the space. If you mess up, it'll default to middle right."))
		{

			return joke.getJokeY(screenHeight, pos);
		}

		// Middle by default
		else
		{

			return (screenHeight / 2) - 30 + (20 * pos);
		}
	}
}