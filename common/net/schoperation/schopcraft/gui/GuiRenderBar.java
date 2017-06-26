package net.schoperation.schopcraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.schoperation.schopcraft.SchopCraft;

public class GuiRenderBar extends Gui {
	
	private final ResourceLocation tempBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/tempbar.png");
	private final ResourceLocation thirstBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/thirstbar.png");
	private final ResourceLocation sanityBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/sanitybar.png");
	private final ResourceLocation wetnessBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/wetnessbar.png");
	private final int texturewidth = 100, textureheight = 11, barwidth = 80;
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event) {
		
		if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
			
			Minecraft mc = Minecraft.getMinecraft();
			
			// get current screen resolution
			ScaledResolution scaled = new ScaledResolution(mc);
			int screenwidth = scaled.getScaledWidth();
			int screenheight = scaled.getScaledHeight();
			
			// this is temporary bullcrap to test the bars. they work.
			float oneUnit = (float) (barwidth / mc.player.getMaxHealth());
			int currentWidth = (int) (oneUnit * mc.player.getHealth());
			int playerHealth = (int) mc.player.getHealth();
			String text = Integer.toString(playerHealth) + "%";
			
			//more garbage
			//Biome biome = mc.world.getBiome(mc.player.getPosition());
			//System.out.println(Float.toString(biome.getTemperature()));
			
			// only show bars if the f3 debug screen isn't showing.
			if (!mc.gameSettings.showDebugInfo) {
				
				// top rect is bar, bottom rect is outline/icon
				mc.renderEngine.bindTexture(tempBar);
				drawTexturedModalRect(screenwidth-barwidth-2, screenheight-277, 19, 14, currentWidth, textureheight);
				drawTexturedModalRect(screenwidth-texturewidth-1, screenheight-280, 0, 0, texturewidth, textureheight);
				
				mc.renderEngine.bindTexture(thirstBar);
				drawTexturedModalRect(screenwidth-barwidth-2, screenheight-257, 19, 14, currentWidth, textureheight);
				drawTexturedModalRect(screenwidth-texturewidth-1, screenheight-260, 0, 0, texturewidth, textureheight);
				
				mc.renderEngine.bindTexture(sanityBar);
				drawTexturedModalRect(screenwidth-barwidth-2, screenheight-237, 19, 14, currentWidth, textureheight);
				drawTexturedModalRect(screenwidth-texturewidth-1, screenheight-240, 0, 0, texturewidth, textureheight);
				
				mc.renderEngine.bindTexture(wetnessBar);
				drawTexturedModalRect(screenwidth-barwidth-2, screenheight-217, 19, 14, currentWidth, textureheight);
				drawTexturedModalRect(screenwidth-texturewidth-1, screenheight-220, 0, 0, texturewidth, textureheight);
				
				drawCenteredString(mc.fontRenderer, text, screenwidth-texturewidth-16, screenheight-277, Integer.parseInt("FFFFFF", 16));
				drawCenteredString(mc.fontRenderer, text, screenwidth-texturewidth-16, screenheight-257, Integer.parseInt("FFFFFF", 16));
				drawCenteredString(mc.fontRenderer, text, screenwidth-texturewidth-16, screenheight-237, Integer.parseInt("FFFFFF", 16));
				drawCenteredString(mc.fontRenderer, text, screenwidth-texturewidth-16, screenheight-217, Integer.parseInt("FFFFFF", 16));
			}
		}
	}
}
