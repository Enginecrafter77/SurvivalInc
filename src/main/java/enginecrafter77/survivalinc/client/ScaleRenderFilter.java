package enginecrafter77.survivalinc.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.util.ReadableDimension;

public class ScaleRenderFilter implements ElementRenderFilter {

	private final Vec2f scale;
	
	public ScaleRenderFilter(Vec2f scale)
	{
		this.scale = scale == Vec2f.MAX ? null : scale;
	}
	
	@Override
	public boolean begin(RenderFrameContext context, OverlayElement element)
	{
		Vec2f scale = this.scale;
		if(scale == null) scale = ScaleRenderFilter.getRatio(Axis2D.getResolutionDimensions(context.getResolution()), element.getSize());
		GlStateManager.pushMatrix();
		GlStateManager.scale(scale.x, scale.y, 0F);
		return true;
	}

	@Override
	public void end(RenderFrameContext resoultion, OverlayElement element)
	{
		GlStateManager.popMatrix();
	}

	public static Vec2f getRatio(ReadableDimension first, ReadableDimension second)
	{
		return new Vec2f((float)first.getWidth() / (float)second.getWidth(), (float)first.getHeight() / (float)second.getHeight());
	}
	
}
