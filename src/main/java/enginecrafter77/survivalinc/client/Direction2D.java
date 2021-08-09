package enginecrafter77.survivalinc.client;

import org.lwjgl.util.ReadablePoint;
import org.lwjgl.util.WritablePoint;

import com.google.common.collect.ImmutableMap;

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
	
	/** The natural direction for axes */
	private static final ImmutableMap<Axis2D, Direction2D> naturals = ImmutableMap.of(Axis2D.HORIZONTAL, RIGHT, Axis2D.VERTICAL, DOWN);
	private static final ImmutableMap<Direction2D, Direction2D> opposites = ImmutableMap.of(LEFT, RIGHT, RIGHT, LEFT, DOWN, UP, UP, DOWN);
	
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
	public boolean isNatural()
	{
		return this.delta > 0;
	}
	
	/**
	 * @return True if the direction moves a coordinate closer towards negative infinity, false otherwise.
	 * @deprecated Naming and usage don't make sense. Use negated {@link #isNatural()} instead.
	 */
	@Deprecated
	public boolean isReverse()
	{
		return this.delta < 0;
	}
	
	/**
	 * @return The opposite direction to the local one.
	 */
	public Direction2D opposite()
	{
		return Direction2D.opposites.get(this);
	}
	
	public <POINT extends ReadablePoint & WritablePoint> void movePoint(POINT point, int steps)
	{
		switch(this.axis)
		{
		case HORIZONTAL:
			point.setX(point.getX() + this.getAxialDelta() * steps);
			break;
		case VERTICAL:
			point.setY(point.getY() + this.getAxialDelta() * steps);
			break;
		default:
			throw new IllegalStateException("Axis " + this.axis + " doesn't exist!");
		}
	}
	
	/**
	 * Returns the direction describing natural movement along the axis.
	 * This means that a point on Cartesian plane will have the coordinate
	 * on target axis increased by the returned direction. In other words,
	 * the direction returned by this method will be the direction that
	 * causes the coordinate to increase on given axis.
	 * @param axis The axis to match the direction for
	 * @return The direction natural to the axis
	 */
	public static Direction2D getNaturalDirection(Axis2D axis)
	{
		return Direction2D.naturals.get(axis);
	}
}