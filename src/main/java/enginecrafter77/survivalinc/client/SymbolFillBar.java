package enginecrafter77.survivalinc.client;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class SymbolFillBar implements OverlayElement<Float> {	
	public final TexturedElement symbol;
	
	protected int capacity;
	protected int spacing;
	
	public SymbolFillBar(TexturedElement symbol)
	{
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
	
	protected int calculateOffset(int index)
	{
		return index * (this.symbol.getWidth() + this.spacing);
	}
	
	protected int calculateWidthFor(float length, int piece)
	{
		return Math.round((float)this.symbol.width * Math.min(1F, length - piece));
	}
	
	@Override
	public Set<ElementType> disableElements(Float arg)
	{
		return OverlayElement.ALLOW_ALL;
	}
	
	@Override
	public int getHeight()
	{
		return this.symbol.getHeight();
	}
	
	@Override
	public int getWidth()
	{
		return this.calculateOffset(this.capacity);
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
			context.drawScaled(x + this.calculateOffset(piece), y, this.calculateWidthFor(length, piece), this.getHeight());
		}
		context.close();
	}

}
