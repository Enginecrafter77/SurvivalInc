package net.schoperation.schopcraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.schoperation.schopcraft.SchopCraft;
import net.schoperation.schopcraft.cap.wetness.IWetness;
import net.schoperation.schopcraft.cap.wetness.WetnessProvider;

public class GuiRenderBar extends Gui {
	
	private final ResourceLocation tempBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/tempbar.png");
	private final ResourceLocation thirstBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/thirstbar.png");
	private final ResourceLocation sanityBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/sanitybar.png");
	private final ResourceLocation wetnessBar = new ResourceLocation(SchopCraft.MOD_ID, "textures/gui/wetnessbar.png");
	private final int texturewidth = 100, textureheight = 11, barwidth = 80;
	private static float wetness = 0.00f;
	private static float thirst = 100.00f;
	
	public static void getServerWetness(float value) {	
		wetness = value;	
	}
	public static void getServerThirst(float value) {
		thirst = value;
	}
	
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event) {
		
		if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
			
			// instance of Minecraft. All of this crap is client-side (well of course)
			Minecraft mc = Minecraft.getMinecraft();
			
			// get current screen resolution
			ScaledResolution scaled = new ScaledResolution(mc);
			int screenwidth = scaled.getScaledWidth();
			int screenheight = scaled.getScaledHeight();
			
			// determine width of WETNESS bar.
			double oneWetnessUnit = 0.8; // this is hardcoded in until max and min values are configurable, either by me or you.
			int currentWidthWetness = (int) (oneWetnessUnit * wetness);
			String textWetness = Float.toString(Math.round(wetness)) + "%";
			
			// determine width of THIRST bar.
			double oneThirstUnit = 0.8;
			int currentWidthThirst = (int) (oneThirstUnit * thirst);
			String textThirst = Float.toString(Math.round(thirst)) + "%";
			
			// this is temporary bullcrap to test the bars. they work.
			float oneUnit = (float) (barwidth / mc.player.getMaxHealth());
			int currentWidth = (int) (oneUnit * mc.player.getHealth());
			int playerHealth = (int) mc.player.getHealth();
			String text = Integer.toString(playerHealth) + "%";
			
			//more garbage crap testing biome stuff blah blah blah
			//Biome biome = mc.world.getBiome(mc.player.getPosition());
			//System.out.println(Float.toString(biome.getTemperature()));
			
			// only show bars if the f3 debug screen isn't showing.
			if (!mc.gameSettings.showDebugInfo) {
				
				// top rect is bar, bottom rect is outline/icon
				// TEMPERATURE
				mc.renderEngine.bindTexture(tempBar);
				drawTexturedModalRect(screenwidth-barwidth-2, screenheight-277, 19, 14, currentWidth, textureheight);
				drawTexturedModalRect(screenwidth-texturewidth-1, screenheight-280, 0, 0, texturewidth, textureheight);
				drawCenteredString(mc.fontRenderer, text, screenwidth-texturewidth-16, screenheight-277, Integer.parseInt("FFFFFF", 16));
				
				// THIRST
				mc.renderEngine.bindTexture(thirstBar);
				drawTexturedModalRect(screenwidth-barwidth-2, screenheight-257, 19, 14, currentWidthThirst, textureheight);
				drawTexturedModalRect(screenwidth-texturewidth-1, screenheight-260, 0, 0, texturewidth, textureheight);
				drawCenteredString(mc.fontRenderer, textThirst, screenwidth-texturewidth-16, screenheight-257, Integer.parseInt("FFFFFF", 16));
				
				// SANITY
				mc.renderEngine.bindTexture(sanityBar);
				drawTexturedModalRect(screenwidth-barwidth-2, screenheight-237, 19, 14, currentWidth, textureheight);
				drawTexturedModalRect(screenwidth-texturewidth-1, screenheight-240, 0, 0, texturewidth, textureheight);
				drawCenteredString(mc.fontRenderer, text, screenwidth-texturewidth-16, screenheight-237, Integer.parseInt("FFFFFF", 16));
				
				// WETNESS
				// only show wetness if there is wetness. This is in place so wetness isn't confused with thirst.
				if (wetness > 0) {
					mc.renderEngine.bindTexture(wetnessBar);
					drawTexturedModalRect(screenwidth-barwidth-2, screenheight-217, 19, 14, currentWidthWetness, textureheight);
					drawTexturedModalRect(screenwidth-texturewidth-1, screenheight-220, 0, 0, texturewidth, textureheight);
					drawCenteredString(mc.fontRenderer, textWetness, screenwidth-texturewidth-16, screenheight-217, Integer.parseInt("FFFFFF", 16));
				}
				
				
				
				
				
			}
		}
	}
}
