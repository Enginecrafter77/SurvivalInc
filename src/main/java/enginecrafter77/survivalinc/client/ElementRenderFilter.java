package enginecrafter77.survivalinc.client;

import net.minecraft.client.gui.ScaledResolution;
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
 * @param <ARGUMENT> The argument
 */
@SideOnly(Side.CLIENT)
public interface ElementRenderFilter<ARGUMENT> {
	/**
	 * Phase 1 of the filter; runs before any content is
	 * drawn on the screen. Useful for pushing matrices.
	 * @param resoultion The game resolution
	 * @param arg The optional argument
	 * @return True if the element should be drawn, false otherwise.
	 */
	public boolean begin(ScaledResolution resoultion, ARGUMENT arg);
	
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
	public void end(ScaledResolution resoultion, ARGUMENT arg);
}
