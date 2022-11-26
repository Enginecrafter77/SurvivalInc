package enginecrafter77.survivalinc.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import org.lwjgl.util.ReadablePoint;

/**
 * TranslateRenderFilter is a simple render filter used
 * to move an element by a fixed amount.
 * @author Enginecrafter77
 */
public class TranslateRenderFilter implements ElementRenderFilter, RenderStageFilter {
	
	/** The element position to shift the element by */
	public ReadablePoint offset;
	
	public TranslateRenderFilter(ReadablePoint offset)
	{
		this.offset = offset;
	}

	@Override
	public boolean begin(RenderFrameContext resoultion, ElementType element)
	{
		return this.begin(resoultion, (OverlayElement)null);
	}

	@Override
	public void end(RenderFrameContext resoultion, ElementType element)
	{
		this.end(resoultion, (OverlayElement)null);
	}

	@Override
	public boolean begin(RenderFrameContext context, OverlayElement element)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(offset.getX(), offset.getY(), 0D);
		return true;
	}

	@Override
	public void end(RenderFrameContext context, OverlayElement element)
	{
		GlStateManager.popMatrix();
	}

}
