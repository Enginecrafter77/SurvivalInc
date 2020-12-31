package enginecrafter77.survivalinc.client;

import java.awt.Color;
import java.awt.color.ColorSpace;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * GaugeBar is a simple overlay element
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public class GaugeBar extends SimpleOverlayElement<Float> {
	private static final ResourceLocation bartemplate = new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/statusbar.png");
	private static final int bartemplate_width = 16, bartemplate_height = 32;
	
	/** The base color */
	public final Color color;
	
	/** True to force recalculation of the color */
	private boolean recalculateColor;
	
	/** Individual RGB color components */
	private float[] colorcomponents;
	
	public GaugeBar(Color color)
	{
		super(8, 32);
		this.colorcomponents = new float[4];
		this.recalculateColor = true;
		this.color = color;
	}
	
	@Override
	public void draw(Position2D position, float partialTicks, Float prop)
	{	
		float propheight = this.getHeight() * prop;
		int bar_bottom_dist = Math.round(propheight), bar_top_dist = Math.round((float)this.getHeight() - propheight);
		
		// Recalculate the color if needed
		if(this.recalculateColor)
		{
			this.calculateRGBColor(prop);
			this.recalculateColor = false;
		}
		
		GlStateManager.enableAlpha();
		// Draw the gauge frame
		this.texturer.bindTexture(bartemplate);
		Gui.drawModalRectWithCustomSizedTexture(position.getX(), position.getY(), 0, 0, this.getWidth(), this.getHeight(), GaugeBar.bartemplate_width, GaugeBar.bartemplate_height);
		
		// Draw the gauge infill (colored using OpenGL)
		GlStateManager.pushMatrix();
		GlStateManager.color(this.colorcomponents[0], this.colorcomponents[1], this.colorcomponents[2], this.colorcomponents[3]);
		Gui.drawModalRectWithCustomSizedTexture(position.getX(), position.getY() + bar_top_dist, this.getWidth(), bar_top_dist, this.getWidth(), bar_bottom_dist, GaugeBar.bartemplate_width, GaugeBar.bartemplate_height);
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
