package enginecrafter77.survivalinc.client;

import java.util.HashSet;
import java.util.Iterator;
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
	public final Axis axis;
	
	/** Spacing between each two elements */
	public int spacing;
	
	public OverlayElementGroup(Axis axis)
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
			if(this.axis == Axis.VERTICAL)
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
			if(this.axis == Axis.HORIZONTAL)
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
		PositioningIterator pos = new PositioningIterator(resolution, position, partialTicks, arg);
		while(pos.hasNext()) pos.next();
	}
	
	@Override
	public Set<ElementType> disableElements(TYPE arg)
	{
		Set<ElementType> set = new HashSet<ElementType>();
		for(OverlayElement<? super TYPE> element : this.elements)
			set.addAll(element.disableElements(arg));
		return set;
	}
	
	/**
	 * A simple enum specifying the axis plane.
	 * @author Enginecrafter77
	 */
	public static enum Axis {HORIZONTAL, VERTICAL}
	
	public class PositioningIterator extends ElementPositioner implements Iterator<OverlayElement<? super TYPE>>
	{		
		public final ScaledResolution resolution;
		public final float partialTicks;
		public final TYPE arg;
		
		public int index;
		
		public PositioningIterator(ScaledResolution resolution, ElementPositioner position, float partialTicks, TYPE arg)
		{
			this.partialTicks = partialTicks;
			this.resolution = resolution;
			this.index = 0;
			this.arg = arg;
			
			this.setPositionOffset(position.offX, position.offY);
			this.setPositionOrigin(position.mulX, position.mulY);
		}

		@Override
		public boolean hasNext()
		{
			return this.index < elements.size();
		}

		@Override
		public OverlayElement<? super TYPE> next()
		{
			OverlayElement<? super TYPE> element = elements.get(this.index++);
			element.draw(this.resolution, this, this.partialTicks, this.arg);
			this.offX += axis == Axis.HORIZONTAL ? element.getWidth() + spacing : 0;
			this.offY += axis == Axis.VERTICAL ? element.getHeight() + spacing : 0;
			return element;
		}
	}
}
