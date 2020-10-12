package enginecrafter77.survivalinc.client;

import java.awt.Color;
import java.awt.color.ColorSpace;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatTracker;
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
public class GaugeBar extends ScalableOverlayElement {
	private static final ResourceLocation bartemplate = new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/statusbar.png");
	private static final int bartemplate_width = 16, bartemplate_height = 32;
	
	public final TextureManager texturer;
	
	public final Color color;
	
	private boolean recalculateColor;
	private float[] colorcomponents;
	
	public final StatProvider provider;
	private StatTracker tracker;
	
	public GaugeBar(StatProvider provider, Color color)
	{
		super(GaugeBar.bartemplate_width / 2, GaugeBar.bartemplate_height);
		this.texturer = Minecraft.getMinecraft().renderEngine;
		this.colorcomponents = new float[4];
		this.recalculateColor = true;
		this.provider = provider;
		this.color = color;
		
		// Create a dummy record to see if it's a subclass of SimpleStatRecord
		if(!(provider.createNewRecord() instanceof SimpleStatRecord))
		{
			throw new IllegalArgumentException("Differential Arrow can be used only with providers using SimpleStatRecord records!");
		}
	}
	
	@Override
	public void draw(RenderGameOverlayEvent event)
	{
		if(event.getType() == ElementType.HOTBAR) super.draw(event);
	}
	
	protected float getFillFraction(StatTracker tracker)
	{
		SimpleStatRecord record = (SimpleStatRecord)tracker.getRecord(this.provider);
		return (record.getValue() - record.valuerange.lowerEndpoint()) / (record.valuerange.upperEndpoint() - record.valuerange.lowerEndpoint());
	}
	
	@Override
	public void draw()
	{
		float proportion = this.getFillFraction(this.getTracker());
		
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
	
	public StatTracker getTracker()
	{
		if(this.tracker == null)
			this.tracker = Minecraft.getMinecraft().player.getCapability(StatCapability.target, null);
		return this.tracker;
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
