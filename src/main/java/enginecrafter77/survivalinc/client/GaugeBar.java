package enginecrafter77.survivalinc.client;

import java.awt.Color;
import java.awt.color.ColorSpace;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GaugeBar extends Gui {
	private static final ResourceLocation bartemplate = new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/statusbar.png");
	private static final int bartemplate_width = 16, bartemplate_height = 32;
	
	public final TextureManager texturer;
	
	protected int width, height;
	
	public final Color color;
	
	private boolean recalculateColor;
	private float[] colorcomponents;
	
	public GaugeBar(int width, int height, Color color)
	{
		this.texturer = Minecraft.getMinecraft().renderEngine;
		this.colorcomponents = new float[4];
		this.recalculateColor = true;
		this.height = height;
		this.width = width;
		this.color = color;
	}
	
	public void draw(int x, int y, float proportion)
	{
		float propheight = GaugeBar.bartemplate_height * proportion;
		int bar_bottom_dist = Math.round(propheight), bar_top_dist = Math.round((float)bartemplate_height - propheight);
		
		// Recalculate the color if needed
		if(this.recalculateColor)
		{
			this.calculateRGBColor(proportion);
			this.recalculateColor = false;
		}
		
		// Enable drawing with alpha
		GlStateManager.enableAlpha();
		
		// Draw the gauge frame
		this.texturer.bindTexture(bartemplate);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, GaugeBar.bartemplate_width, GaugeBar.bartemplate_height);
		
		// Draw the gauge infill (colored using OpenGL)
		GlStateManager.pushMatrix();
		GlStateManager.color(this.colorcomponents[0], this.colorcomponents[1], this.colorcomponents[2], this.colorcomponents[3]);
		Gui.drawModalRectWithCustomSizedTexture(x, y + bar_top_dist, GaugeBar.bartemplate_width / 2, bar_top_dist, width, bar_bottom_dist, GaugeBar.bartemplate_width, GaugeBar.bartemplate_height);
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.popMatrix();
		// Disable previously enabled alpha
		GlStateManager.disableAlpha();
	}
	
	public void recalculateColor()
	{
		this.recalculateColor = true;
	}
	
	protected void calculateRGBColor(float proportion)
	{
		this.getColor(proportion).getColorComponents(ColorSpace.getInstance(ColorSpace.CS_sRGB), this.colorcomponents);
		this.colorcomponents[3] = 1F - (float)ModConfig.CLIENT.barTransparency;
	}
	
	public Color getColor(float proportion)
	{
		return this.color;
	}
	
}
