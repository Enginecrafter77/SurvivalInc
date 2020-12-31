package enginecrafter77.survivalinc.client;

import net.minecraft.client.Minecraft;
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
public class SymbolFillBar implements OverlayElement<Float> {
	/** The symbol to draw */
	public final TexturedElement symbol;
	
	/** The direction of drawing */
	public final Direction2D direction;
	
	/** The maximal number of symbols drawn */
	protected int capacity;
	
	/** The spacing between the elements */
	protected int spacing;
	
	public SymbolFillBar(TexturedElement symbol, Direction2D direction)
	{
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
	}
	
	/**
	 * Sets the bar's {@link #capacity}.
	 * @param count The capacity of the bar.
	 */
	public void setCapacity(int count)
	{
		this.capacity = count;
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
	
	/**
	 * Calculates the axial dimension of the icon with the desired fill.
	 * Basically if the axis matches the drawing direction's axis, the width
	 * is multiplied by the fill ratio, otherwise it is returned as it is. 
	 * @param fill
	 * @param axis
	 * @return
	 */
	protected int calculateDimensionOn(float fill, Axis2D axis)
	{
		int dimension = this.symbol.getSize(axis);
		if(this.direction.axis == axis)
			dimension = Math.round((float)dimension * fill);
		return dimension;
	}
	
	@Override
	public int getSize(Axis2D axis)
	{
		int dimension = this.symbol.getSize(axis);
		if(this.direction.axis == axis)
			dimension = this.capacity * (dimension + this.spacing);
		return dimension;
	}
	
	@Override
	public void draw(Position2D position, float partialTicks, Float length)
	{
		length *= this.capacity;
		
		int steps = (int)Math.round(Math.floor(length)); // Number of full symbols
		
		TexturedElement.TextureDrawingContext context = this.symbol.createContext(Minecraft.getMinecraft().getTextureManager());
		Position2D.MutablePosition current_position = new Position2D.MutablePosition(position);
		
		/*
		 * If the direction is not normal for the given axis, we need to shift the position, since drawing
		 * always begins in the top left corner regardless of the direction of drawing. And since the symbols
		 * themselves are drawn from top left corner, we need to move n(w+s)-w pixels to the left, where w is
		 * the width of each symbol (excluding spacing), s is the spacing and n is the number of symbols. Because we
		 * can't simply reverse the direction, we need to enter negative number of steps (which effectively reverses
		 * the direction). This lends us this formula: -n(w+s)+w, which can be written as w-n(w+s).
		 */
		if(!this.direction.isNormal()) current_position.move(this.direction, this.symbol.getSize(this.direction.axis) - this.getSize(this.direction.axis));
		
		for(int piece = 0; piece <= steps; piece++)
		{
			float fill = Math.min(1F, length - piece);
			
			int width = this.calculateDimensionOn(fill, Axis2D.HORIZONTAL);
			int height = this.calculateDimensionOn(fill, Axis2D.VERTICAL);
			Position2D offset = Position2D.ZERO; // Stores the texture offset relative to it's base
			Position2D symbolpos = current_position; // The symbol position
			
			// If the direction is reverse, we also need to shift the texture
			if(!this.direction.isNormal())
			{
				// Initialize the offset
				offset = new Position2D(context.width - width, context.height - height);
				
				// Make corrections to the drawing position so the texture is drawn where it should be
				symbolpos = current_position.toImmutable().move(this.direction, -offset.getPositionOn(this.direction.axis));
			}
			
			// Draw the symbol
			context.drawPartial(symbolpos, offset, width, height);
			
			// Move to the next position
			current_position.move(this.direction, this.symbol.getSize(this.direction.axis) + this.spacing);
		}
		context.close();
	}

}
