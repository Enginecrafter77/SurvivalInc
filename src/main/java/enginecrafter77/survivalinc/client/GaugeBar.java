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
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GaugeBar extends ScalableOverlayElement {
	private static final ResourceLocation bartemplate = new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/statusbar.png");
	private static final int bartemplate_width = 16, bartemplate_height = 32;
	
	public final TextureManager texturer;
	
	/** The base color */
	public final Color color;
	
	/** True to force recalculation of the color */
	private boolean recalculateColor;
	
	/** Individual RGB color components */
	private float[] colorcomponents;
	
	public GaugeBar(Color color)
	{
		super(GaugeBar.bartemplate_width / 2, GaugeBar.bartemplate_height);
		this.texturer = Minecraft.getMinecraft().renderEngine;
		this.colorcomponents = new float[4];
		this.recalculateColor = true;
		this.color = color;
	}
	
	@Override
	public void draw(RenderGameOverlayEvent event)
	{
		if(event.getType() == ElementType.HOTBAR) super.draw(event);
	}
	
	/**
	 * The ratio of the filled to the empty part of the gauge bar.
	 * In other words, it's the fraction of the gauge bar's area
	 * that should be filled.
	 * @return Fraction of the bar's area that should be filled.
	 */
	protected abstract float getFillFraction();
	
	@Override
	public void draw()
	{
		float proportion = this.getFillFraction();
		
		float propheight = this.height * proportion;
		int bar_bottom_dist = Math.round(propheight), bar_top_dist = Math.round((float)this.height - propheight);
		
		// Recalculate the color if needed
		if(this.recalculateColor)
		{
			this.calculateRGBColor(proportion);
			this.recalculateColor = false;
		}
		
		GlStateManager.enableAlpha();
		// Draw the gauge frame
		this.texturer.bindTexture(bartemplate);
		Gui.drawModalRectWithCustomSizedTexture(this.getX(), this.getY(), 0, 0, this.width, this.height, GaugeBar.bartemplate_width, GaugeBar.bartemplate_height);
		
		// Draw the gauge infill (colored using OpenGL)
		GlStateManager.pushMatrix();
		GlStateManager.color(this.colorcomponents[0], this.colorcomponents[1], this.colorcomponents[2], this.colorcomponents[3]);
		Gui.drawModalRectWithCustomSizedTexture(this.getX(), this.getY() + bar_top_dist, this.width, bar_top_dist, this.width, bar_bottom_dist, GaugeBar.bartemplate_width, GaugeBar.bartemplate_height);
		GlStateManager.color(1F, 1F, 1F);
		GlStateManager.popMatrix();
		GlStateManager.disableAlpha();
	}
	
	/**
	 * Forces the gauge bar to recalculate the color the next render tick
	 */
	public void recalculateColor()
	{
		this.recalculateColor = true;
	}
	
	/**
	 * Internal function that splits the color returned by {@link #getColor(float)}
	 * into individual RGB components.
	 * @param fraction The fraction of the bar that is currently filled
	 */
	protected void calculateRGBColor(float fraction)
	{
		this.getColor(fraction).getColorComponents(ColorSpace.getInstance(ColorSpace.CS_sRGB), this.colorcomponents);
		this.colorcomponents[3] = 1F - (float)ModConfig.CLIENT.barTransparency;
	}
	
	/**
	 * Calculates the color of the gauge bar with respect to the currently filled fraction of the gauge bar.
	 * @param fraction The fraction of the bar that is currently filled
	 * @return The color the gauge bar infill should have
	 */
	public Color getColor(float proportion)
	{
		return this.color;
	}
	
}
