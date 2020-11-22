package enginecrafter77.survivalinc.client;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * OverlayElementGroup is a simple container for
 * {@link ScalableOverlayElement}s. This container
 * basically works by spreading the elements along
 * a common axis.
 * @author Enginecrafter77
 */
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
	public int getHeight()
	{
		int height = 0;
		for(OverlayElement<?> element : this.elements)
		{
			if(this.axis == Axis2D.VERTICAL)
			{
				height += element.getHeight() + this.spacing;
			}
			else if(element.getHeight() > height)
			{
				height = element.getHeight();
			}
		}
		return height;
	}
	
	@Override
	public int getWidth()
	{
		int width = 0;
		for(OverlayElement<?> element : this.elements)
		{
			if(this.axis == Axis2D.HORIZONTAL)
			{
				width += element.getWidth() + this.spacing;
			}
			else if(element.getWidth() > width)
			{
				width = element.getWidth();
			}
		}
		return width;
	}
	
	@Override
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, TYPE arg)
	{
		for(OverlayElement<? super TYPE> element : this.elements)
		{
			element.draw(resolution, position, partialTicks, arg);
			int offx = this.axis == Axis2D.HORIZONTAL ? element.getWidth() + this.spacing : 0;
			int offy = this.axis == Axis2D.VERTICAL ? element.getHeight() + this.spacing : 0;
			position = new ElementPositioner(position, offx, offy);
		}
	}
	
	@Override
	public Set<ElementType> disableElements(TYPE arg)
	{
		Set<ElementType> set = new HashSet<ElementType>();
		for(OverlayElement<? super TYPE> element : this.elements)
			set.addAll(element.disableElements(arg));
		return set;
	}
}
