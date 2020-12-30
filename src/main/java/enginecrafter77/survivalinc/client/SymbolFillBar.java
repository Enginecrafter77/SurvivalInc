package enginecrafter77.survivalinc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
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
	 * Calculates offset of the item at <i>index</i> on the target axis based on the fill ratio.
	 * @param fill The fill ratio of the symbol. Value of 1 means full, 0 means empty.
	 * @param index The index of the symbol.
	 * @param axis The axis to get the offset on.
	 * @return The absolute offset from the origin point on the target axis.
	 */
	protected int calculateOffsetOn(float fill, int index, Axis2D axis)
	{
		int offset = 0;
		int size = this.symbol.getSize(axis);
		if(this.direction.axis == axis)
		{
			if(!this.direction.isNormal()) // If the direction is in reverse, we need to follow some other rules.
			{
				/*
				 * Since every position is in the top left corner, start the offset at the other end (O1).
				 * This leads us position on the end of the chain. Since we are at the very end of the
				 * chain (last pixel), we need to step back by entire 1 space (icon + spacing) by subtracting
				 * the icon and spacing sizes (O2). Then, we need to advance forward to shift the texture, since
				 * we may be drawing a partial icon (O3).
				 * 
				 * Operands: offset = O1 - O2 + O3
				 */
				offset = this.getSize(axis) - (size + this.spacing) + Math.round((1F - fill) * size);
			}
			// Ultimately, shift the position by the number of symbols (icon + spacing) in the desired way.
			offset += index * (size + this.spacing) * this.direction.getAxialDelta();
		}
		return offset;
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
	
	/**
	 * Draws a single symbol.
	 * @param context The drawing context 
	 * @param index The index of the icon to be drawn
	 * @param x The X coordinate of the drawing origin point (top left corner of the whole bar)
	 * @param y The Y coordinate of the drawing origin point (top left corner of the whole bar)
	 * @param fill The fill fraction of the symbol.
	 */
	protected void drawSymbol(TexturedElement.TextureDrawingContext context, int index, int x, int y, float fill)
	{
		int width = this.calculateDimensionOn(fill, Axis2D.HORIZONTAL);
		int height = this.calculateDimensionOn(fill, Axis2D.VERTICAL);
		int offx = 0, offy = 0;
		
		// If the direction is reverse, we also need to shift the texture
		if(!this.direction.isNormal())
		{
			offx = context.width - width;
			offy = context.height - height;
		}
		
		x += this.calculateOffsetOn(fill, index, Axis2D.HORIZONTAL);
		y += this.calculateOffsetOn(fill, index, Axis2D.VERTICAL);
		context.drawPartial(x, y, offx, offy, width, height);
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
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, Float length)
	{
		length *= this.capacity;
		
		int x = position.getX(resolution), y = position.getY(resolution);		
		int steps = (int)Math.round(Math.floor(length)); // Number of full symbols
		
		TexturedElement.TextureDrawingContext context = this.symbol.createContext(Minecraft.getMinecraft().getTextureManager());
		for(int piece = 0; piece <= steps; piece++)
		{
			this.drawSymbol(context, piece, x, y, Math.min(1F, length - piece));
		}
		context.close();
	}

}
