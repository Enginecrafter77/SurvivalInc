package enginecrafter77.survivalinc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class SymbolFillBar implements OverlayElement<Float> {	
	public final TexturedElement symbol;
	public final Direction2D direction;
	
	protected int capacity;
	protected int spacing;
	
	public SymbolFillBar(TexturedElement symbol, Direction2D direction)
	{
		this.direction = direction;
		this.symbol = symbol;
		
		this.capacity = 1;
		this.spacing = 0;
	}
	
	public void setSpacing(int spacing)
	{
		this.spacing = spacing;
	}
	
	public void setCapacity(int count)
	{
		this.capacity = count;
	}
	
	public int getCapacity()
	{
		return this.capacity;
	}
	
	public int getSpacing()
	{
		return this.spacing;
	}
	
	protected int calculateOffsetOn(float fill, int index, Axis2D axis)
	{
		int size = this.symbol.getSize(axis);
		int offset = 0;
		if(this.direction.axis == axis)
		{
			offset = index * (size + this.spacing);
			if(this.direction.isReverse())
			{
				offset += Math.round((1F - fill) * size);
			}
		}
		return offset;
	}
	
	protected int calculateDimensionOn(float fill, int piece, Axis2D axis)
	{
		int dimension = this.symbol.getSize(axis);
		if(this.direction.axis == axis)
			dimension = Math.round((float)dimension * fill);
		return dimension;
	}
	
	protected void drawSymbol(TexturedElement.TextureDrawingContext context, int index, int x, int y, float fill)
	{
		int width = this.calculateDimensionOn(fill, index, Axis2D.HORIZONTAL);
		int height = this.calculateDimensionOn(fill, index, Axis2D.VERTICAL);
		int offx = 0, offy = 0;
		
		if(this.direction.isReverse())
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
