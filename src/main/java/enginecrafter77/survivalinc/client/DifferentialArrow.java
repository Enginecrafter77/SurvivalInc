package enginecrafter77.survivalinc.client;

import java.util.EnumMap;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
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
	
	protected float amplitude, min_scale, max_scale;
	
	public DifferentialArrow(StatProvider provider, int width, int height)
	{
		this.texturer = Minecraft.getMinecraft().getTextureManager();
		this.position = new EnumMap<Axis, Integer>(Axis.class);
		this.provider = provider;
		this.height = height;
		this.width = width;
		
		this.amplitude = 10F;
		this.min_scale = 0.3F;
		this.max_scale = 1F;
		
		// Create a dummy record to see if it's a subclass of SimpleStatRecord
		if(!(provider.createNewRecord() instanceof SimpleStatRecord))
		{
			throw new IllegalArgumentException("Differential Arrow can be used only with providers using SimpleStatRecord records!");
		}
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
		SimpleStatRecord record = (SimpleStatRecord)tracker.getRecord(provider);
		float scale = record.getLastChange() * this.amplitude;
		float dist = Math.abs(scale);
		if(dist > this.max_scale) scale /= dist; // Always results in 1 or -1
		if(dist < this.min_scale && dist != 0) scale = scale > 0 ? this.min_scale : -this.min_scale;
		return scale;
	}
	
}