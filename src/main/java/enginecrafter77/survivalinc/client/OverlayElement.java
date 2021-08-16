package enginecrafter77.survivalinc.client;

import java.util.Optional;

import org.lwjgl.util.Point;
import org.lwjgl.util.ReadableDimension;
import org.lwjgl.util.ReadablePoint;

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
public interface OverlayElement {
	public static final ReadablePoint POINT_ZERO = new Point(0, 0);
	
	/**
	 * Draws the element on the screen.
	 * @param resolution The resolution to draw in
	 * @param position The desired position of the element
	 * @param partialTicks Fraction of time between one tick and another
	 * @param arg The render argument
	 */
	public void draw(ReadablePoint position, float partialTicks, Object... arguments);
	
	public ReadableDimension getSize();
	
	/**
	 * @param axis The axis of the element.
	 * @return The size of the element along the specified axis.
	 */
	public default int getSize(Axis2D axis)
	{
		return axis.getDimensionAxialValue(this.getSize());
	}
	
	/**
	 * @deprecated Use {@link #draw(Position2D, float, Object)} instead.
	 * @param resolution The screen resolution
	 * @param position The element positioner
	 * @param partialTicks Fraction of time between one tick and another
	 * @param arg The render argument
	 */
	@Deprecated
	public default void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, Object... arguments)
	{
		this.draw(position.getPositionFor(resolution, this), partialTicks, arguments);
	}
	
	public static <TYPE> Optional<TYPE> getArgument(Object[] arguments, int index, Class<TYPE> type)
	{
		if(index >= arguments.length) return Optional.empty();
		
		if(!type.isInstance(arguments[index])) throw new IllegalArgumentException(String.format("Argument #%d (%s) is of invalid type (%s)!", index, type.getName(), arguments[index].getClass().getName()));
		return Optional.of(type.cast(arguments[index]));
	}
}
