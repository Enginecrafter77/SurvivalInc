package enginecrafter77.survivalinc.client;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

/**
 * TranslateRenderFilter is a simple render filter used
 * to move an element by a fixed amount.
 * @author Enginecrafter77
 */
public class TranslateRenderFilter implements ElementRenderFilter<Object> {
	
	/** The element position to shift the element by */
	public Position2D offset;
	
	public TranslateRenderFilter(Position2D offset)
	{
		this.offset = offset;
	}

	@Override
	public boolean begin(ScaledResolution resolution, Object arg)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(offset.getX(), offset.getY(), 0D);
		return true;
	}

	@Override
	public void end(ScaledResolution resoultion, Object arg)
	{
		GlStateManager.popMatrix();
	}

}
