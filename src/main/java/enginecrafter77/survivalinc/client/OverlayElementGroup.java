package enginecrafter77.survivalinc.client;

import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class OverlayElementGroup extends ScalableOverlayElement {
	
	public final List<ScalableOverlayElement> elements;
	public final Axis axis;
	public int spacing;
	
	public OverlayElementGroup(Axis axis)
	{
		super(0, 0);
		this.elements = new LinkedList<ScalableOverlayElement>();
		this.spacing = 2;
		this.axis = axis;
	}
	
	public void add(ScalableOverlayElement element)
	{
		this.elements.add(element);
	}
	
	@Override
	public void onResolutionChange(ScaledResolution res)
	{
		super.onResolutionChange(res);
		
		int xoff = 0, yoff = 0;
		for(ScalableOverlayElement element : this.elements)
		{
			element.setRelativePositionBase(this.mulX, this.mulY);
			element.setAbsolutePosition(this.offX + xoff, this.offY + yoff);
			switch(this.axis)
			{
			case HORIZONTAL:
				xoff += element.getWidth() + this.spacing;
				break;
			case VERTICAL:
				yoff += element.getHeight() + this.spacing;
				break;
			default:
				throw new IllegalStateException("Illegal value of axis variable");
			}
			
			element.onResolutionChange(res);
		}
	}
	
	@Override
	public int getHeight()
	{
		int height = 0;
		for(ScalableOverlayElement element : this.elements)
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
		for(ScalableOverlayElement element : this.elements)
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
	public void draw(RenderGameOverlayEvent event)
	{
		super.draw(event);
		
		for(ScalableOverlayElement element : this.elements)
		{
			element.draw(event);
		}
	}
	
	@Override
	public void draw() {} // Do nothing
	
	public static enum Axis {HORIZONTAL, VERTICAL}

}
