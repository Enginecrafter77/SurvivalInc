package enginecrafter77.survivalinc.client;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public interface RenderStageFilter {
	/**
	 * Phase 1 of the filter; runs before any content is
	 * drawn on the screen. Useful for pushing matrices.
	 * @param resoultion The game resolution
	 * @param arg The optional argument
	 * @return True if the element should be drawn, false otherwise.
	 */
	public boolean begin(ScaledResolution resoultion, ElementType element);
	
	/**
	 * Phase 2 of the filter; runs after the element has
	 * been drawn on the screen. Useful for popping matrices.
	 * Please note that the element might not be drawn on the
	 * screen if the {@link #begin(ScaledResolution, Object)}
	 * method returned false. But even in that case, it is
	 * guaranteed that this method will be run to avoid unmatched
	 * matrices on the stack.
	 * @param resoultion The game resolution
	 * @param arg The optinal argument
	 */
	public void end(ScaledResolution resoultion, ElementType element);	
}
