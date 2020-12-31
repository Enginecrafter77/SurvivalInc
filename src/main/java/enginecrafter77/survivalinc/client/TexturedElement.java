package enginecrafter77.survivalinc.client;

import java.io.Closeable;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * TextureElement is a convenient element implementation,
 * allowing for context-based texture rendering. TexturedElement
 * needs {@link TextureResource} as a source for the texture.
 * TexturedElement is designed to be easy to use, so the programmer
 * doesn't have to remember and specify the exact texture dimensions
 * each time a part of texture is drawn.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public class TexturedElement extends SimpleOverlayElement<Object> {
	/** The texture resource to pull the texture from */
	public final TextureResource resource;
	
	/** The x offset of the texture */
	public final int offset_x;
	/** The y offset of the texture */
	public final int offset_y;
	
	/** Whether this texutre should be drawn with alpha or not */
	public final boolean hasAlpha;
	
	/** The current texture drawing context, might be replaced with stack in the future */
	private static TextureDrawingContext current_context = null;
	
	public TexturedElement(TextureResource resource, int offset_x, int offset_y, int width, int height, boolean alpha)
	{
		super(width, height);
		this.resource = resource;
		this.offset_x = offset_x;
		this.offset_y = offset_y;
		this.hasAlpha = alpha;
	}
	
	@Override
	public void draw(Position2D position, float partialTicks, Object arg)
	{
		TextureDrawingContext context = this.createContext(this.texturer);
		context.draw(position, partialTicks, arg);
		context.close();
	}
	
	/**
	 * Creates texture drawing context for this element.
	 * @param manager The texture manager to initialize the context with
	 * @return A new texture drawing context.
	 */
	public TextureDrawingContext createContext(TextureManager manager) throws IllegalStateException
	{
		if(TexturedElement.current_context != null) throw new IllegalStateException("Attempting to create a nested context!");
		TexturedElement.current_context = new TextureDrawingContext(manager, this.width, this.height);
		TexturedElement.current_context.enable(manager);
		return TexturedElement.current_context;
	}
	
	public class TextureDrawingContext implements OverlayElement<Object>, Closeable {
		public final TextureManager manager;
		
		protected final int width;
		protected final int height;
		
		public TextureDrawingContext(TextureManager manager, int width, int height)
		{
			this.manager = manager;
			this.height = height;
			this.width = width;
		}
		
		protected void enable(TextureManager manager)
		{
			if(hasAlpha) GlStateManager.enableAlpha();
			resource.load(manager);
		}
		
		protected void disable()
		{
			if(hasAlpha) GlStateManager.disableAlpha();
		}

		public void drawPartial(Position2D position, Position2D offset, int width, int height)
		{
			Gui.drawModalRectWithCustomSizedTexture(position.getX(), position.getY(), offset_x + offset.getX(), offset_y + offset.getY(), width, height, resource.texture_width, resource.texture_height);
		}
		
		public void draw(Position2D position)
		{
			this.drawPartial(position, Position2D.ZERO, this.width, this.height);
		}
		
		@Override
		public void draw(Position2D position, float partialTicks, Object arg)
		{
			this.draw(position);
		}
		
		@Override
		public int getSize(Axis2D axis)
		{
			switch(axis)
			{
			case HORIZONTAL:
				return this.width;
			case VERTICAL:
				return this.height;
			default:
				throw new UnsupportedOperationException("Axis " + axis.name() + " doesn't exist!");
			}
		}
		
		@Override
		public void close()
		{
			this.disable();
			
			if(TexturedElement.current_context != this) throw new IllegalStateException("Attepting to close from non-owning context!");
			TexturedElement.current_context = null;
		}
		
	}
}