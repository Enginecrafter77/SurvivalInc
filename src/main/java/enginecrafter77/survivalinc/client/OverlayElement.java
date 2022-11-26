package enginecrafter77.survivalinc.client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.Point;
import org.lwjgl.util.ReadableDimension;
import org.lwjgl.util.ReadablePoint;

/**
 * OverlayElement specifies the simplest possible
 * specification of an object which can be drawn
 * on the screen as an overlay.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public interface OverlayElement {
	public static final ReadablePoint POINT_ZERO = new Point(0, 0);
	
	/**
	 * Draws the element on the screen.
	 * @param context The frame render context
	 * @param position The position to draw the element at
	 */
	public void draw(RenderFrameContext context, ReadablePoint position);
	
	public ReadableDimension getSize();
	
	/**
	 * @param axis The axis of the element.
	 * @return The size of the element along the specified axis.
	 */
	public default int getSize(Axis2D axis)
	{
		return axis.getDimensionAxialValue(this.getSize());
	}
}
