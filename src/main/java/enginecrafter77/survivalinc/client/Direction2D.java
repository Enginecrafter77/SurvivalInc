package enginecrafter77.survivalinc.client;

/**
 * A simple enum specifying the possible
 * movement directions in 2D plane.
 * @author Enginecrafter77
 */
public enum Direction2D {
	LEFT(Axis2D.HORIZONTAL, -1),
	RIGHT(Axis2D.HORIZONTAL, 1),
	DOWN(Axis2D.VERTICAL, 1),
	UP(Axis2D.VERTICAL, -1);
	
	public final Axis2D axis;
	
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
	 * Moves the specified {@link ElementPositoner} by the
	 * specified amount of steps in the local direction.
	 * @param subject Origin ElementPositioner, to which the offset is applied
	 * @param steps The number of steps in the local direction to take
	 * @return ElementPositioner offset by X steps in the local directions
	 */
	public ElementPositioner move(ElementPositioner subject, int steps)
	{
		return new ElementPositioner(subject, axis == Axis2D.HORIZONTAL ? this.delta * steps : 0, axis == Axis2D.VERTICAL ? this.delta * steps : 0);
	}
	
	/**
	 * @return True if the direction moves a coordinate closer towards negative infinity, false otherwise.
	 */
	public boolean isReverse()
	{
		return this.delta < 0;
	}
}