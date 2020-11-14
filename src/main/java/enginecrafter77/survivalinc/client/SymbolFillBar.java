package enginecrafter77.survivalinc.client;

import net.minecraft.client.gui.ScaledResolution;

public class SymbolFillBar extends SimpleOverlayElement<Float> {	
	public final TexturedElement symbol;
	public final int count;
	
	private int spacing;
	
	public SymbolFillBar(TexturedElement symbol, int count)
	{
		super(symbol.getWidth(), symbol.getHeight());
		this.symbol = symbol;
		this.count = count;
		
		this.spacing = 0;
	}
	
	public void setSpacing(int spacing)
	{
		this.spacing = spacing;
	}
	
	@Override
	public int getWidth()
	{
		return (this.width + this.spacing) * this.count;
	}
	
	@Override
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, Float length)
	{
		length *= this.count;
		
		int x = position.getX(resolution), y = position.getY(resolution);		
		int steps = (int)Math.round(Math.floor(length)); // Number of full symbols
		
		TexturedElement.TextureDrawingContext context = this.symbol.createContext(this.texturer);
		for(int piece = 0; piece <= steps; piece++)
		{
			int offset = piece * (this.width + this.spacing);
			int width = Math.round((float)this.width * Math.min(1F, length - piece));
			context.drawScaled(x + offset, y, width, this.getHeight());
		}
		context.close();
	}

}
