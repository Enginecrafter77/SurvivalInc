package enginecrafter77.survivalinc.client;

import org.lwjgl.util.Point;
import org.lwjgl.util.ReadableDimension;
import org.lwjgl.util.ReadablePoint;
import org.lwjgl.util.Rectangle;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * SymbolFillBar is a simple element that allows for
 * symbols to be drawn stacked next to each other.
 * Each SymbolFillBar has it's own capacity. The
 * capacity indicates how many of the symbols are drawn
 * when the input number is 1. The input float is normally
 * a value between 0 and 1. When 0 is the input value, no
 * symbols are drawn at all. The symbol fill bar may span
 * to whatever direction the user wishes, although the drawing
 * still starts in the top left corner (according to standards).
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public class SymbolFillBar extends SimpleOverlayElement {
	/** The symbol to draw */
	public final TextureResource symbol;
	
	/** The direction of drawing */
	public final Direction2D direction;
	
	/** The maximal number of symbols drawn */
	protected int capacity;
	
	/** The spacing between the elements */
	protected int spacing;
	
	public SymbolFillBar(TextureResource symbol, Direction2D direction)
	{
		super(new AxisMappedDimension(symbol.getSize()));
		this.direction = direction;
		this.symbol = symbol;
		
		this.capacity = 1;
		this.spacing = 0;
	}
	
	/**
	 * Sets the spacing between the elements.
	 * @param spacing The spacing in pixels.
	 */
	public void setSpacing(int spacing)
	{
		this.spacing = spacing;
		this.recalculateMajorDimension();
	}
	
	/**
	 * Sets the bar's {@link #capacity}.
	 * @param count The capacity of the bar.
	 */
	public void setCapacity(int count)
	{
		this.capacity = count;
		this.recalculateMajorDimension();
	}
	
	/**
	 * @return The current capacity of the bar.
	 */
	public int getCapacity()
	{
		return this.capacity;
	}
	
	/**
	 * @return The current spacing of the bar.
	 */
	public int getSpacing()
	{
		return this.spacing;
	}
	
	protected void recalculateMajorDimension()
	{
		int majordim = this.symbol.getSize(this.direction.axis);
		((AxisMappedDimension)this.size).setSizeOn(this.direction.axis, this.capacity * (majordim + this.spacing));
	}
	
	protected ReadableDimension calculateDimension(float fill)
	{
		AxisMappedDimension dim = new AxisMappedDimension(this.symbol.getSize());
		
		float orig = dim.getSizeOn(this.direction.axis);
		dim.setSizeOn(this.direction.axis, Math.round(orig * fill));
		
		return dim;
	}
	
	@Override
	public ReadableDimension getSize()
	{
		return this.size;
	}
	
	@Override
	public void draw(ReadablePoint position, float partialTicks, Object... arguments)
	{
		float length = OverlayElement.getArgument(arguments, 0, Float.class).get() * this.capacity;
		
		int steps = (int)Math.round(Math.floor(length)); // Number of full symbols
		
		Point current_position = new Point(position);
		
		/*
		 * If the direction is not normal for the given axis, we need to shift the position, since drawing
		 * always begins in the top left corner regardless of the direction of drawing. And since the symbols
		 * themselves are drawn from top left corner, we need to move n(w+s)-w pixels to the left, where w is
		 * the width of each symbol (excluding spacing), s is the spacing and n is the number of symbols. Because we
		 * can't simply reverse the direction, we need to enter negative number of steps (which effectively reverses
		 * the direction). This lends us this formula: -n(w+s)+w, which can be written as w-n(w+s). Additionally,
		 * move +1x spacing in the specified direction to negate the last spacing effect.
		 */
		if(!this.direction.isNatural()) this.direction.movePoint(current_position, this.symbol.getSize(this.direction.axis) - this.getSize(this.direction.axis) + this.spacing);
		
		for(int piece = 0; piece <= steps; piece++)
		{
			float fill = Math.min(1F, length - piece);
			
			// The region of the texture to draw.
			Rectangle region = new Rectangle(POINT_ZERO, this.calculateDimension(fill));
			if(region.isEmpty()) continue; // Avoid IAE stemming from TextureResource#region
			
			// The position of the symbol on screen
			ReadablePoint symbolpos = current_position;
			
			// If the direction is reverse, we also need to shift the texture
			if(!this.direction.isNatural())
			{
				// Fetch the size of the symbol
				ReadableDimension symbolsize = this.symbol.getSize();
				
				// Initialize the offset
				region.setLocation(new Point(symbolsize.getWidth() - region.getWidth(), symbolsize.getHeight() - region.getHeight()));
				
				// Make corrections to the drawing position so the texture is drawn where it should be
				Point pt = new Point(symbolpos);
				this.direction.movePoint(pt, -this.direction.axis.getPointAxialValue(region));
				symbolpos = pt;
			}
			
			// Draw the symbol
			this.symbol.region(region).draw(symbolpos, partialTicks);
			
			// Move to the next position
			this.direction.movePoint(current_position, this.symbol.getSize(this.direction.axis) + this.spacing);
		}
	}

}
