package enginecrafter77.survivalinc.client;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.Dimension;
import org.lwjgl.util.Point;
import org.lwjgl.util.ReadableDimension;
import org.lwjgl.util.ReadablePoint;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * OverlayElementGroup is a simple container for
 * {@link ScalableOverlayElement}s. This container
 * basically works by spreading the elements along
 * a common axis.
 * @author Enginecrafter77
 */
@Deprecated
@SideOnly(Side.CLIENT)
public class OverlayElementGroup<TYPE> implements OverlayElement<TYPE> {
	
	/** Lists of elements in this group */
	public final List<OverlayElement<? super TYPE>> elements;
	
	/** The axis to spread the elements along */
	public final Axis2D axis;
	
	/** Spacing between each two elements */
	public int spacing;
	
	public OverlayElementGroup(Axis2D axis)
	{
		this.elements = new LinkedList<OverlayElement<? super TYPE>>();
		this.spacing = 2;
		this.axis = axis;
	}
	
	/**
	 * Adds the specified element to the group
	 * @param element The element to be added
	 */
	public void add(OverlayElement<? super TYPE> element)
	{
		this.elements.add(element);
	}
	
	@Override
	public ReadableDimension getSize()
	{
		return new Dimension(this.getSize(Axis2D.HORIZONTAL), this.getSize(Axis2D.VERTICAL));
	}
	
	@Override
	public int getSize(Axis2D axis)
	{
		int size = 0;
		for(OverlayElement<?> element : this.elements)
		{
			int elementsize = element.getSize(axis);
			if(this.axis == axis)
			{
				size += elementsize + this.spacing;
			}
			else if(elementsize > size)
			{
				size = elementsize;
			}
		}
		return size;
	}
	
	@Override
	public void draw(ReadablePoint position, float partialTicks, TYPE arg)
	{
		Point elementpos = new Point(position);
		for(OverlayElement<? super TYPE> element : this.elements)
		{
			element.draw(elementpos, partialTicks, arg);
			Direction2D.getNaturalDirection(this.axis).movePoint(elementpos, element.getSize(this.axis) + this.spacing);
		}
	}
}
