package enginecrafter77.survivalinc.client;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * OverlayElement specifies the simplest possible
 * specification of an object which can be drawn
 * on the screen as an overlay.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public interface OverlayElement {
	/**
	 * Draws the element on the specified event
	 * @param event The event to draw this element on
	 */
	public abstract void draw(RenderGameOverlayEvent event);
	
	/**
	 * @return The width of the element
	 */
	public int getWidth();
	
	/**
	 * @return The height of the element
	 */
	public int getHeight();
}
