package enginecrafter77.survivalinc.client;

/**
 * A simple enum specifying the axis plane.
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
	
	public int getAxialDelta()
	{
		return this.delta;
	}
	
	public ElementPositioner move(ElementPositioner subject, int steps)
	{
		return new ElementPositioner(subject, axis == Axis2D.HORIZONTAL ? this.delta * steps : 0, axis == Axis2D.VERTICAL ? this.delta * steps : 0);
	}
	
	public boolean requiresOffseting()
	{
		return this.delta < 0;
	}
}