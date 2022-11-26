package enginecrafter77.survivalinc.client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * ElementRenderFilter specifies a simple interface
 * to execute some code before and after an element
 * is drawn on screen. This interface was designed
 * with OpenGL operations in mind. Just like {@link OverlayElement},
 * this interface accepts an optional argument provided
 * by the caller.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public interface ElementRenderFilter {
	/**
	 * Phase 1 of the filter; runs before any content is
	 * drawn on the screen. Useful for pushing matrices.
	 * @param context
	 * @param element
	 * @return True if the element should be drawn, false otherwise.
	 */
	public boolean begin(RenderFrameContext context, OverlayElement element);
	
	/**
	 * Phase 2 of the filter; runs after the element has
	 * been drawn on the screen. Useful for popping matrices.
	 * Please note that the element might not be drawn on the
	 * screen if the {@link #begin(RenderFrameContext, OverlayElement)}
	 * method returned false. But even in that case, it is
	 * guaranteed that this method will be run to avoid unmatched
	 * matrices on the stack.
	 * @param context
	 * @param element
	 */
	public void end(RenderFrameContext context, OverlayElement element);
}
