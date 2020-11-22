package enginecrafter77.survivalinc.client;

import java.io.Closeable;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;

public class TexturedElement extends SimpleOverlayElement<Object> {
	public final TextureResource resource;
	
	public final int offset_x;
	public final int offset_y;
	
	public final boolean hasAlpha;
	
	public TexturedElement(TextureResource resource, int offset_x, int offset_y, int width, int height, boolean alpha)
	{
		super(width, height);
		this.resource = resource;
		this.offset_x = offset_x;
		this.offset_y = offset_y;
		this.hasAlpha = alpha;
	}
	
	@Override
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, Object arg)
	{
		TextureDrawingContext context = this.createContext(this.texturer);
		context.draw(resolution, position, partialTicks, arg);
		context.close();
	}
	
	public TextureDrawingContext createContext(TextureManager manager)
	{
		this.resource.load(manager);
		if(this.hasAlpha) GlStateManager.enableAlpha();
		return new TextureDrawingContext(this.width, this.height);
	}
	
	public class TextureDrawingContext extends SimpleOverlayElement<Object> implements Closeable
	{
		public TextureDrawingContext(int width, int height)
		{
			super(width, height);
		}

		public void drawPartial(int x, int y, int offx, int offy, int width, int height)
		{
			Gui.drawModalRectWithCustomSizedTexture(x, y, offset_x + offx, offset_y + offy, width, height, resource.texture_width, resource.texture_height);
		}
		
		public void draw(int x, int y)
		{
			this.drawPartial(x, y, 0, 0, this.width, this.height);
		}
		
		@Override
		public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, Object arg)
		{
			this.draw(position.getX(resolution), position.getY(resolution));
		}
		
		@Override
		public void close()
		{
			if(hasAlpha) GlStateManager.disableAlpha();
		}
		
	}
}