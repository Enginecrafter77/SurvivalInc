package enginecrafter77.survivalinc.client;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * OverlayElement specifies the simplest possible
 * specification of an object which can be drawn
 * on the screen as an overlay.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public interface OverlayElement<RENDER_ARGUMENT> {
	/**
	 * Draws the element on the screen.
	 * @param resolution The resolution to draw in
	 * @param position The desired position of the element
	 * @param partialTicks Fraction of time between one tick and another
	 * @param arg The render argument
	 */
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, RENDER_ARGUMENT arg);
	
	/**
	 * @param axis The axis of the element.
	 * @return The size of the element along the specified axis.
	 */
	public int getSize(Axis2D axis);
}
