package enginecrafter77.survivalinc.client;

import org.lwjgl.util.ReadableDimension;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec2f;

public class ScaleRenderFilter implements ElementRenderFilter {

	private final Vec2f scale;
	
	public ScaleRenderFilter(Vec2f scale)
	{
		this.scale = scale == Vec2f.MAX ? null : scale;
	}
	
	@Override
	public boolean begin(ScaledResolution resoultion, OverlayElement element)
	{
		Vec2f scale = this.scale;
		if(scale == null) scale = ScaleRenderFilter.getRatio(Axis2D.getResolutionDimensions(resoultion), element.getSize());
		GlStateManager.pushMatrix();
		GlStateManager.scale(scale.x, scale.y, 0F);
		return true;
	}

	@Override
	public void end(ScaledResolution resoultion, OverlayElement element)
	{
		GlStateManager.popMatrix();
	}

	public static Vec2f getRatio(ReadableDimension first, ReadableDimension second)
	{
		return new Vec2f((float)first.getWidth() / (float)second.getWidth(), (float)first.getHeight() / (float)second.getHeight());
	}
	
}
