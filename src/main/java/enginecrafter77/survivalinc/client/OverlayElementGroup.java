package enginecrafter77.survivalinc.client;

import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.gui.ScaledResolution;
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
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, TYPE arg)
	{
		for(OverlayElement<? super TYPE> element : this.elements)
		{
			element.draw(resolution, position, partialTicks, arg);
			int offx = this.axis == Axis2D.HORIZONTAL ? element.getSize(Axis2D.HORIZONTAL) + this.spacing : 0;
			int offy = this.axis == Axis2D.VERTICAL ? element.getSize(Axis2D.VERTICAL) + this.spacing : 0;
			position = new ElementPositioner(position, offx, offy);
		}
	}
}
