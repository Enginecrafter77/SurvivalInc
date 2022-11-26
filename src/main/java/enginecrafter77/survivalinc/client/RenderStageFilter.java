package enginecrafter77.survivalinc.client;

import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public interface RenderStageFilter {
	/**
	 * Phase 1 of the filter; runs before any content is
	 * drawn on the screen. Useful for pushing matrices.
	 * @param resoultion The game resolution
	 * @return True if the element should be drawn, false otherwise.
	 */
	public boolean begin(RenderFrameContext resoultion, ElementType element);
	
	/**
	 * Phase 2 of the filter; runs after the element has
	 * been drawn on the screen. Useful for popping matrices.
	 * Please note that the element might not be drawn on the
	 * screen if the {@link #begin(RenderFrameContext, ElementType)}
	 * method returned false. But even in that case, it is
	 * guaranteed that this method will be run to avoid unmatched
	 * matrices on the stack.
	 * @param resoultion The game resolution
	 */
	public void end(RenderFrameContext resoultion, ElementType element);
}
