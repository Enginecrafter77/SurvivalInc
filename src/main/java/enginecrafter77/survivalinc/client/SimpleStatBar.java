package enginecrafter77.survivalinc.client;

import java.awt.Color;

import enginecrafter77.survivalinc.stats.StatProvider;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SimpleStatBar extends GaugeBar {	
	protected final DifferentialArrow arrow;
	
	protected final ResourceLocation texture;
	protected int texoffx, texoffy, texwidth, texheight;
	protected int iconheight, spacing;
	
	public SimpleStatBar(StatProvider key, ResourceLocation icon, Color color)
	{
		this(key, icon, 0, 0, color);
	}
	
	public SimpleStatBar(StatProvider provider, ResourceLocation texture, int texture_x, int texture_y, Color color)
	{
		super(provider, color);
		
		this.arrow = new DifferentialArrow(provider, 8, 12);
		this.texture = texture;
		this.texoffx = texture_x;
		this.texoffy = texture_y;
		
		this.texheight = 12;
		this.texwidth = 8;
		this.iconheight = 12;
		this.spacing = 2;
	}
	
	@Override
	public void draw(RenderGameOverlayEvent event)
	{
		if(event.getType() == ElementType.HOTBAR) super.draw(event);
	}
	
	@Override
	public void setAbsolutePosition(int x, int y)
	{
		super.setAbsolutePosition(x, y);
		this.arrow.setAbsolutePosition(x, y);
	}
	
	@Override
	public void setRelativePositionBase(float x, float y)
	{
		super.setRelativePositionBase(x, y);
		this.arrow.setRelativePositionBase(x, y);
	}
	
	@Override
	public void onResolutionChange(ScaledResolution res)
	{
		super.onResolutionChange(res);
		this.arrow.onResolutionChange(res);
	}
	
	@Override
	public int getHeight()
	{
		return this.arrow.getHeight() + super.getHeight() + this.iconheight + 2 * this.spacing;
	}
	
	/**
	 * This method returns the Y position of the gauge bar
	 */
	@Override
	public int getY()
	{
		return super.getY() + this.arrow.getHeight() + this.spacing;
	}
	
	@Override
	public void draw()
	{		
		// Draw the arrow indicating value
		this.arrow.draw();
		// Draw the gauge bar
		super.draw();
		
		GlStateManager.enableAlpha(); // Enable alpha, we will need it
		// Draw the stat icon
		this.texturer.bindTexture(this.texture);
		Gui.drawModalRectWithCustomSizedTexture(this.getX(), this.getY() + super.getHeight() + this.spacing, texoffx, texoffy, this.width, iconheight, texwidth, texheight);
		GlStateManager.disableAlpha(); // Disable alpha, just in case
	}
}