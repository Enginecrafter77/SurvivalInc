package enginecrafter77.survivalinc.client;

import java.util.EnumMap;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class DifferentialArrow implements StatRender
{
	public static final ResourceLocation arrowtexture = new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/arrow.png");
	
	public final EnumMap<Axis, Integer> position;
	public final TextureManager texturer;
	public final StatProvider provider;
	
	public int width, height;
	
	public DifferentialArrow(StatProvider provider, int width, int height)
	{
		this.texturer = Minecraft.getMinecraft().getTextureManager();
		this.position = new EnumMap<Axis, Integer>(Axis.class);
		this.provider = provider;
		this.height = height;
		this.width = width;
	}
	
	@Override
	public void draw(ScaledResolution resolution, StatTracker tracker)
	{
		// Draw the arrow
		this.texturer.bindTexture(arrowtexture);
		float value = this.getArrowValue(tracker);
		boolean inverse = value < 0F;
		value = Math.abs(value);
		
		GlStateManager.pushMatrix(); // Create new object by pushing matrix
		// Offset this object into the desired position + centering offset
		GlStateManager.translate(this.position.get(Axis.HORIZONTAL) + (this.width / 2), this.position.get(Axis.VERTICAL) + (this.height / 2), 0F);
		GlStateManager.pushMatrix(); // Create new object by pushing matrix
		GlStateManager.scale(value, value, 1F); // Scale the arrow
		if(inverse) GlStateManager.rotate(180F, 0F, 0F, 1F); // Rotate the arrow
		Gui.drawModalRectWithCustomSizedTexture(-this.width / 2, -this.height / 2, 0, 0, this.width, this.height, 8, 12); // Draw the arrow (center at origin)
		GlStateManager.popMatrix(); // Render the scaled and rotated arrow
		GlStateManager.popMatrix(); // Render the offset arrow in place
	}

	@Override
	public int getDimension(Axis axis)
	{
		switch(axis)
		{
		case HORIZONTAL:
			return this.width;
		case VERTICAL:
			return this.height;
		default:
			return 0;
		}
	}

	@Override
	public void setPosition(Axis axis, int value)
	{
		this.position.put(axis, value);
	}
	
	public float getArrowValue(StatTracker tracker)
	{
		float scale = 10F * tracker.getLastChange(provider);
		float dist = Math.abs(scale);
		if(dist > 1F) scale /= dist; // Always results in 1 or -1
		if(dist < 0.3F && dist != 0) scale = scale > 0F ? 0.3F : -0.3F;
		return scale;
	}
	
}