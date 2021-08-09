package enginecrafter77.survivalinc.client;

import java.util.EnumMap;

import org.lwjgl.util.ReadableDimension;
import org.lwjgl.util.WritableDimension;

public final class AxisMappedDimension implements ReadableDimension, WritableDimension {
	public final EnumMap<Axis2D, Integer> dimensions;
	
	public AxisMappedDimension(int width, int height)
	{
		this();
		this.setSize(width, height);
	}
	
	public AxisMappedDimension(ReadableDimension dimension)
	{
		this();
		this.setSize(dimension);
	}
	
	private AxisMappedDimension()
	{
		this.dimensions = new EnumMap<Axis2D, Integer>(Axis2D.class);
	}
	
	public int getSizeOn(Axis2D axis)
	{
		return this.dimensions.get(axis);
	}
	
	public void setSizeOn(Axis2D axis, int size)
	{
		this.dimensions.put(axis, size);
	}
	
	@Override
	public int getWidth()
	{
		return this.getSizeOn(Axis2D.HORIZONTAL);
	}

	@Override
	public int getHeight()
	{
		return this.getSizeOn(Axis2D.VERTICAL);
	}

	@Override
	public void getSize(WritableDimension dest)
	{
		dest.setSize(this);
	}

	@Override
	public void setSize(int w, int h)
	{
		this.setWidth(w);
		this.setHeight(h);
	}

	@Override
	public void setSize(ReadableDimension d)
	{
		this.setSize(d.getWidth(), d.getHeight());
	}

	@Override
	public void setHeight(int height)
	{
		this.dimensions.put(Axis2D.VERTICAL, height);
	}

	@Override
	public void setWidth(int width)
	{
		this.dimensions.put(Axis2D.HORIZONTAL, width);
	}
}