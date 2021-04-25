package enginecrafter77.survivalinc.client;

import java.util.EnumMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Position2D describes a single point in 2D Cartesian plane.
 * The class is intended to be used in 2D graphics, so the
 * position is stored as integer.
 * @author Enginecrafter77
 */
public class Position2D {
	public static final Position2D ZERO = new Position2D(0, 0);
	
	/** The position map */
	private final ImmutableMap<Axis2D, Integer> position;
	
	/**
	 * Constructs a Position2D object using the Cartesian coordinates X (horizontal) and Y (vertical).
	 * @param x The horizontal coordinate
	 * @param y The vertical coordinate
	 */
	public Position2D(int x, int y)
	{
		this.position = ImmutableMap.of(Axis2D.HORIZONTAL, x, Axis2D.VERTICAL, y);
	}
	
	/**
	 * Constructs a Position2D object using a pre-filled position map mapping
	 * {@link Axis2D axes} to their respective positions.
	 * @param map
	 */
	public Position2D(EnumMap<Axis2D, Integer> map)
	{
		this.position = Maps.immutableEnumMap(map);
	}
	
	/**
	 * Returns the internal mapping of axes to the coordinates of the point on given axis.
	 * This method returns direct reference to internal map, so it may be modified if the implementation
	 * allow to do so.
	 * @return The internal mapping of axes to the coordinates of the point on given axis.
	 */
	protected Map<Axis2D, Integer> getPositionMap()
	{
		return this.position;
	}
	
	/**
	 * Offsets the point by the given XY coordinates.
	 * @param x The X offset
	 * @param y The Y offset
	 * @return A new Position2D instance offset by the given position
	 */
	public Position2D offset(int x, int y)
	{
		Map<Axis2D, Integer> position = this.getPositionMap();
		x += position.get(Axis2D.HORIZONTAL);
		y += position.get(Axis2D.VERTICAL);
		return new Position2D(x, y);
	}
	
	/**
	 * Moves the point in the given direction <i>steps</i> times.
	 * @param direction The direction on 2D plane of the move
	 * @param steps The length of the move, i.e. how many times to apply atomic step indicated by the direction.
	 * @return A new Position2D instance moved in the given direction.
	 */
	public Position2D move(Direction2D direction, int steps)
	{
		EnumMap<Axis2D, Integer> map = new EnumMap<Axis2D, Integer>(this.getPositionMap());
		int pos = map.get(direction.axis);
		pos += direction.getAxialDelta() * steps;
		map.put(direction.axis, pos);
		return new Position2D(map);
	}
	
	/**
	 * @param axis The axis
	 * @return A position on the given axis
	 */
	public int getPositionOn(Axis2D axis)
	{
		return this.getPositionMap().get(axis);
	}
	
	/**
	 * @return The position on the {@link Axis2D#HORIZONTAL horizontal} axis
	 */
	public final int getX()
	{
		return this.getPositionOn(Axis2D.HORIZONTAL);
	}
	
	/**
	 * @return The position on the {@link Axis2D#VERTICAL vertical} axis
	 */
	public final int getY()
	{
		return this.getPositionOn(Axis2D.VERTICAL);
	}
	
	/**
	 * Describes a point on 2D Cartesian plane that can be moved without
	 * creating new instance.
	 * @author Enginecrafter77
	 */
	public static class MutablePosition extends Position2D
	{
		private final EnumMap<Axis2D, Integer> mposition;
		
		public MutablePosition(Position2D source)
		{
			super(0, 0);
			this.mposition = new EnumMap<Axis2D, Integer>(source.position);
		}
		
		/**
		 * Sets the coordinate on the given axis
		 * @param axis The axis
		 * @param value The coordinate to set for the given axis
		 */
		public void set(Axis2D axis, int value)
		{
			this.mposition.put(axis, value);
		}
		
		/**
		 * Sets the X coordinate
		 * @param x The X coordinate
		 */
		public void setX(int x)
		{
			this.set(Axis2D.HORIZONTAL, x);
		}
		
		/**
		 * Sets the Y coordinate
		 * @param y The Y coordinate
		 */
		public void setY(int y)
		{
			this.set(Axis2D.VERTICAL, y);
		}
		
		/**
		 * Creates a new immutable Position2D based on this object.
		 * @return A new immutable instance of Position2D with the same coordinates as the local object.
		 */
		public Position2D toImmutable()
		{
			return new Position2D(this.mposition);
		}
		
		@Override
		protected Map<Axis2D, Integer> getPositionMap()
		{
			return this.mposition;
		}
		
		@Override
		public int getPositionOn(Axis2D axis)
		{
			return this.getPositionMap().get(axis);
		}
		
		/**
		 * Offsets the point by the given XY coordinates.
		 * @param x The X offset
		 * @param y The Y offset
		 * @return The local Position2D object
		 */
		@Override
		public Position2D offset(int x, int y)
		{
			Map<Axis2D, Integer> position = this.getPositionMap();
			x += position.get(Axis2D.HORIZONTAL);
			y += position.get(Axis2D.VERTICAL);
			position.put(Axis2D.HORIZONTAL, x);
			position.put(Axis2D.HORIZONTAL, y);
			return this;
		}
		
		/**
		 * Moves the point in the given direction <i>steps</i> times.
		 * @param direction The direction on 2D plane of the move
		 * @param steps The length of the move, i.e. how many times to apply atomic step indicated by the direction.
		 * @return The local Position2D object
		 */
		@Override
		public Position2D move(Direction2D direction, int steps)
		{
			Map<Axis2D, Integer> position = this.getPositionMap();
			int value = position.get(direction.axis);
			value += direction.getAxialDelta() * steps;
			position.put(direction.axis, value);
			return this;
		}
	}
}
