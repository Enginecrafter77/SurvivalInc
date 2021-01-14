package enginecrafter77.survivalinc.client;

import java.util.EnumMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class Position2D {
	public static final Position2D ZERO = new Position2D(0, 0);
	
	private final ImmutableMap<Axis2D, Integer> position;
	
	public Position2D(int x, int y)
	{
		this.position = ImmutableMap.of(Axis2D.HORIZONTAL, x, Axis2D.VERTICAL, y);
	}
	
	public Position2D(EnumMap<Axis2D, Integer> map)
	{
		this.position = Maps.immutableEnumMap(map);
	}
	
	protected Map<Axis2D, Integer> getPositionMap()
	{
		return this.position;
	}
	
	public Position2D move(int x, int y)
	{
		Map<Axis2D, Integer> position = this.getPositionMap();
		x += position.get(Axis2D.HORIZONTAL);
		y += position.get(Axis2D.VERTICAL);
		return new Position2D(x, y);
	}
	
	public Position2D move(Direction2D direction, int steps)
	{
		EnumMap<Axis2D, Integer> map = new EnumMap<Axis2D, Integer>(this.getPositionMap());
		int pos = map.get(direction.axis);
		pos += direction.getAxialDelta() * steps;
		map.put(direction.axis, pos);
		return new Position2D(map);
	}
	
	public int getPositionOn(Axis2D axis)
	{
		return this.getPositionMap().get(axis);
	}
	
	public final int getX()
	{
		return this.getPositionOn(Axis2D.HORIZONTAL);
	}
	
	public final int getY()
	{
		return this.getPositionOn(Axis2D.VERTICAL);
	}
	
	public static class MutablePosition extends Position2D
	{
		private final EnumMap<Axis2D, Integer> mposition;
		
		public MutablePosition(Position2D source)
		{
			super(0, 0);
			this.mposition = new EnumMap<Axis2D, Integer>(source.position);
		}
		
		public void set(Axis2D axis, int value)
		{
			this.mposition.put(axis, value);
		}
		
		public void setX(int x)
		{
			this.set(Axis2D.HORIZONTAL, x);
		}
		
		public void setY(int y)
		{
			this.set(Axis2D.VERTICAL, y);
		}
		
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
		
		@Override
		public Position2D move(int x, int y)
		{
			Map<Axis2D, Integer> position = this.getPositionMap();
			x += position.get(Axis2D.HORIZONTAL);
			y += position.get(Axis2D.VERTICAL);
			position.put(Axis2D.HORIZONTAL, x);
			position.put(Axis2D.HORIZONTAL, y);
			return this;
		}
		
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
