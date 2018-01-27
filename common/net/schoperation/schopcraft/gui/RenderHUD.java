package net.schoperation.schopcraft.gui;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.schoperation.schopcraft.SchopCraft;
import net.schoperation.schopcraft.config.SchopConfig;
import net.schoperation.schopcraft.gui.StatBar.StatType;

public class RenderHUD extends Gui {
	
	// The stat bars themselves.
	private final StatBar temperatureBar = new StatBar(StatType.TEMPERATURE, 100, 11, 80, 19, 14, new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/tempbar.png"));
	private final StatBar thirstBar = new StatBar(StatType.THIRST, 100, 11, 80, 19, 14, new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/thirstbar.png"));
	private final StatBar sanityBar = new StatBar(StatType.SANITY, 100, 11, 80, 19, 14, new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/sanitybar.png"));
	private final StatBar wetnessBar = new StatBar(StatType.WETNESS, 100, 11, 80, 19, 14, new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/wetnessbar.png"));
	private final StatBar ghostBar = new StatBar(StatType.GHOST, 200, 9, 187, 12, 10, new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/ghostenergybar.png"));
	
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
				
				// RIGHTMOST position of this bar. This is so we can account for the bar widths correctly.
				int x = getX(screenWidth, i);
				int y = getY(screenHeight, i);
				
				// Now, let's go through each of the four bars and try to place them.
				// First, iterate through the types and get some stuff.
				ResourceLocation texture;
				int fullWidth;
				int fullHeight;
				int movingTextureX;			
				int movingTextureY;
				int fullBarWidth;
				int movingWidth;
				String text;
				
				// Temperature
				if (temperatureBar.shouldBeDisplayed(isGhost, temperature) && !temperatureBar.isAlreadyRendered()) {
					
					// Get the stuff
					texture = temperatureBar.getTexture();
					fullWidth = temperatureBar.getFullWidth();
					fullHeight = temperatureBar.getFullHeight();
					movingTextureX = temperatureBar.getMovingTextureX();
					movingTextureY = temperatureBar.getMovingTextureY();
					fullBarWidth = temperatureBar.getFullBarWidth();
					movingWidth = temperatureBar.getMovingWidth(temperature, maxTemperature);
					text = temperatureBar.getTextToDisplay(temperature);
					temperatureBar.setAlreadyRendered();
				}
				
				// Thirst
				else if (thirstBar.shouldBeDisplayed(isGhost, thirst) && !thirstBar.isAlreadyRendered()) {
					
					texture = thirstBar.getTexture();
					fullWidth = thirstBar.getFullWidth();
					fullHeight = thirstBar.getFullHeight();
					movingTextureX = thirstBar.getMovingTextureX();
					movingTextureY = thirstBar.getMovingTextureY();
					fullBarWidth = thirstBar.getFullBarWidth();
					movingWidth = thirstBar.getMovingWidth(thirst, maxThirst);
					text = thirstBar.getTextToDisplay(thirst);
					thirstBar.setAlreadyRendered();
				}
				
				// Sanity
				else if (sanityBar.shouldBeDisplayed(isGhost, sanity) && !sanityBar.isAlreadyRendered()) {
					
					texture = sanityBar.getTexture();
					fullWidth = sanityBar.getFullWidth();
					fullHeight = sanityBar.getFullHeight();
					movingTextureX = sanityBar.getMovingTextureX();
					movingTextureY = sanityBar.getMovingTextureY();
					fullBarWidth = sanityBar.getFullBarWidth();
					movingWidth = sanityBar.getMovingWidth(sanity, maxSanity);
					text = sanityBar.getTextToDisplay(sanity);
					sanityBar.setAlreadyRendered();
				}
				
				// Wetness
				else if (wetnessBar.shouldBeDisplayed(isGhost, wetness) && !wetnessBar.isAlreadyRendered()) {
					
					texture = wetnessBar.getTexture();
					fullWidth = wetnessBar.getFullWidth();
					fullHeight = wetnessBar.getFullHeight();
					movingTextureX = wetnessBar.getMovingTextureX();
					movingTextureY = wetnessBar.getMovingTextureY();
					fullBarWidth = wetnessBar.getFullBarWidth();
					movingWidth = wetnessBar.getMovingWidth(wetness, maxWetness);
					text = wetnessBar.getTextToDisplay(wetness);
					wetnessBar.setAlreadyRendered();
				}
				
				else {
					
					break;
				}
				
				// Actual rendering. First one is the moving bar. Second one is the whole bar.
				mc.renderEngine.bindTexture(texture);
				drawTexturedModalRect(x - fullBarWidth - 1, y + 3, movingTextureX, movingTextureY, movingWidth, fullHeight);
				drawTexturedModalRect(x - fullWidth, y, 0, 0, fullWidth, fullHeight);
				drawCenteredString(mc.fontRenderer, text, x - fullWidth - 15, y + 2, Integer.parseInt("FFFFFF", 16));
			}
			
			// Reset isAlreadyRendered so we can properly render them continuously.
			temperatureBar.unsetAlreadyRendered();
			thirstBar.unsetAlreadyRendered();
			sanityBar.unsetAlreadyRendered();
			wetnessBar.unsetAlreadyRendered();
			
			// Render ghost bar normally. It's an oddity.
			if (ghostBar.shouldBeDisplayed(isGhost, ghostEnergy)) {
				
				mc.renderEngine.bindTexture(ghostBar.getTexture());
				drawTexturedModalRect((screenWidth / 2)-93, screenHeight-50, ghostBar.getMovingTextureX(), ghostBar.getMovingTextureY(), ghostBar.getMovingWidth(ghostEnergy, 100), ghostBar.getFullHeight());
				drawTexturedModalRect((screenWidth / 2)-105, screenHeight-52, 0, 0, ghostBar.getFullWidth(), ghostBar.getFullHeight());
				drawCenteredString(mc.fontRenderer, ghostBar.getTextToDisplay(ghostEnergy), screenWidth / 2, screenHeight-60, Integer.parseInt("FFFFFF", 16));
			}
		}
	}
	
	// Help determine where to place a stat bar.
	// It's more of a base position, and will be modified for whatever texture it's for.
	
	// Nothing... important...
	// These variables and the following two methods are a joke. For a joke. Look cool.
	private final Random rand = new Random();
	private int jokeDirX1 = 1, jokeDirY1 = 1, jokeDirX2 = 1, jokeDirY2 = 1, jokeDirX3 = 1, jokeDirY3 = 1, jokeDirX4 = 1, jokeDirY4 = 1;
	private int jokeX1 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth());
	private int jokeY1 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
	private int jokeX2 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth());
	private int jokeY2 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
	private int jokeX3 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth());
	private int jokeY3 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
	private int jokeX4 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth());
	private int jokeY4 = rand.nextInt(new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
	
	private int getJokeX(int screenWidth, int pos) {
		
		if (pos == 0) {
			
			jokeX1 += jokeDirX1;
			
			if (jokeX1 > screenWidth || jokeX1 < 100) {
				
				jokeDirX1 *= -1;
			}
			
			return jokeX1;
		}
		
		else if (pos == 1) {
			
			jokeX2 += jokeDirX2;
			
			if (jokeX2 > screenWidth || jokeX2 < 100) {
				
				jokeDirX2 *= -1;
			}
			
			return jokeX2;
		}
		
		else if (pos == 2) {
			
			jokeX3 += jokeDirX3;
			
			if (jokeX3 > screenWidth || jokeX3 < 100) {
				
				jokeDirX3 *= -1;
			}
			
			return jokeX3;
		}
		
		else {
			
			jokeX4 += jokeDirX4;
			
			if (jokeX4 > screenWidth || jokeX4 < 100) {
				
				jokeDirX4 *= -1;
			}
			
			return jokeX4;
		}
	}
	
	private int getJokeY(int screenHeight, int pos) {
		
		if (pos == 1) {
			
			jokeY1 += jokeDirY1;
			
			if (jokeY1 > screenHeight || jokeY1 < 0) {
				
				jokeDirY1 *= -1;
			}
			
			return jokeY1;
		}
		
		else if (pos == 2) {
			
			jokeY2 += jokeDirY2;
			
			if (jokeY2 > screenHeight || jokeY2 < 0) {
				
				jokeDirY2 *= -1;
			}
			
			return jokeY2;
		}
		
		else if (pos == 3) {
			
			jokeY3 += jokeDirY3;
			
			if (jokeY3 > screenHeight || jokeY3 < 0) {
				
				jokeDirY3 *= -1;
			}
			
			return jokeY3;
		}
		
		else {
			
			jokeY4 += jokeDirY4;
			
			if (jokeY4 > screenHeight || jokeY4 < 0) {
				
				jokeDirY4 *= -1;
			}
			
			return jokeY4;
		}
	}
			
	// This'll either be right by 0 or right by the rightmost edge of the screen.
	// So pos doesn't actually matter.
	private int getX(int screenWidth, int pos) {
		
		// Figure out where the user specified to put the bars (in config)
		// From there, figure out where exactly to put the single bar, according to the config value.
		// On the left of the screen
		if (SchopConfig.client.barPositions.equals("top left") || SchopConfig.client.barPositions.equals("middle left") || SchopConfig.client.barPositions.equals("bottom left")) {
			
			return 135;
		}
		
		// Right of the screen
		else if (SchopConfig.client.barPositions.equals("top right") || SchopConfig.client.barPositions.equals("middle right") || SchopConfig.client.barPositions.equals("bottom right")) {
			
			return screenWidth - 2;
		}
		
		// For the smartasses... :)
		else if (SchopConfig.client.barPositions.equals("Include the space. If you mess up, it'll default to middle right.")) {
			
			return getJokeX(screenWidth, pos);
		}
		
		// "middle right" by default.
		else {
			
			return screenWidth - 2;
		}
	}
	
	// The stat bars are 20 pixels apart, vertically.
	private int getY(int screenHeight, int pos) {
		
		// Top of the screen
		if (SchopConfig.client.barPositions.equals("top left") || SchopConfig.client.barPositions.equals("top right")) {
			
			// Is this the 1st bar? 2nd bar? etc.
			return 10 + (20 * pos);
		}
		
		// Middle of the screen
		else if (SchopConfig.client.barPositions.equals("middle left") || SchopConfig.client.barPositions.equals("middle right")) {
			
			return (screenHeight / 2) - 30 + (20 * pos);
		}
		
		// Bottom of the screen
		else if (SchopConfig.client.barPositions.equals("bottom left") || SchopConfig.client.barPositions.equals("bottom right")) {
			
			return screenHeight - 80 + (20 * pos);
		}
		
		// For the smartasses... :)
		else if (SchopConfig.client.barPositions.equals("Include the space. If you mess up, it'll default to middle right.")) {
			
			return getJokeY(screenHeight, pos);
		}
		
		// Middle by default
		else {
			
			return (screenHeight / 2) - 30 + (20 * pos);
		}
	}
}