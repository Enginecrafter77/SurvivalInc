package enginecrafter77.survivalinc.client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A simple enum specifying the possible
 * movement directions in 2D plane.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public enum Direction2D {
	LEFT(Axis2D.HORIZONTAL, -1),
	RIGHT(Axis2D.HORIZONTAL, 1),
	DOWN(Axis2D.VERTICAL, 1),
	UP(Axis2D.VERTICAL, -1);
	
	/** The axis on which this direction operates */
	public final Axis2D axis;
	
	/** The difference between pre-move and post-move coordinate of a point on the target axis */
	private final int delta;
	
	private Direction2D(Axis2D axis, int delta)
	{
		this.delta = delta;
		this.axis = axis;
	}
	
	/**
	 * Returns the change in position on the local {@link Axis2D axis} by this movement.
	 * Returned value is either 1 (move towards positive infinity) or -1 (move towards negative infinity).
	 * @return -1 or 1, indicating the change to the coordination of given axis.
	 */
	public int getAxialDelta()
	{
		return this.delta;
	}
	
	/**
	 * Returns true if the direction moves the target coordinate towards positive infinity, false otherwise.
	 * In other words, this method checks whether this direction is the axis' natural direction or not. 
	 * @return True if this direction is normal, false otherwise.
	 */
	public boolean isNormal()
	{
		return this.delta > 0;
	}
	
	/**
	 * @return True if the direction moves a coordinate closer towards negative infinity, false otherwise.
	 * @deprecated Naming and usage don't make sense. Use negated {@link #isNormal()} instead.
	 */
	@Deprecated
	public boolean isReverse()
	{
		return this.delta < 0;
	}
}