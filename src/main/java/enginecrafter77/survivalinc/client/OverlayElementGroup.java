package enginecrafter77.survivalinc.client;

import java.util.LinkedList;
import java.util.List;
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
	
	private final Direction2D[] naturals = {Direction2D.RIGHT, Direction2D.DOWN}; 
	
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
	public void draw(Position2D position, float partialTicks, TYPE arg)
	{
		Position2D.MutablePosition elementpos = new Position2D.MutablePosition(position);
		for(OverlayElement<? super TYPE> element : this.elements)
		{
			element.draw(elementpos, partialTicks, arg);
			elementpos.move(this.naturals[this.axis.ordinal()], element.getSize(this.axis) + this.spacing);
		}
	}
}
