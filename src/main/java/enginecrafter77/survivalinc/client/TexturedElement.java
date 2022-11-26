package enginecrafter77.survivalinc.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.Dimension;
import org.lwjgl.util.Point;
import org.lwjgl.util.ReadableDimension;
import org.lwjgl.util.ReadablePoint;

import java.io.Closeable;

/**
 * TextureElement is a convenient element implementation,
 * allowing for context-based texture rendering. TexturedElement
 * needs {@link TextureResource} as a source for the texture.
 * TexturedElement is designed to be easy to use, so the programmer
 * doesn't have to remember and specify the exact texture dimensions
 * each time a part of texture is drawn.
 * @deprecated {@link TextureResource#region(org.lwjgl.util.ReadableRectangle)} now provides the same functionality
 * @author Enginecrafter77
 */
@Deprecated
@SideOnly(Side.CLIENT)
public class TexturedElement extends SimpleOverlayElement {
	/** The texture resource to pull the texture from */
	public final TextureResource resource;
	
	/** The x offset of the texture */
	protected final int offset_x;
	/** The y offset of the texture */
	protected final int offset_y;
	
	/** Whether this texutre should be drawn with alpha or not */
	private boolean hasAlpha;
	
	/** The current texture drawing context, might be replaced with stack in the future */
	private static TextureDrawingContext current_context = null;
	
	public TexturedElement(TextureResource resource, int offset_x, int offset_y, int width, int height)
	{
		super(width, height);
		this.resource = resource;
		this.offset_x = offset_x;
		this.offset_y = offset_y;
		this.hasAlpha = true;
	}

	public TexturedElement(TexturedElement copyFrom)
	{
		this(copyFrom.resource, copyFrom.offset_x, copyFrom.offset_y, copyFrom.getWidth(), copyFrom.getHeight());
	}
	
	public TexturedElement noAlpha()
	{
		TexturedElement newelement = new TexturedElement(this);
		newelement.hasAlpha = false;
		return newelement;
	}
	
	@Override
	public void draw(RenderFrameContext renderContext, ReadablePoint position)
	{
		TextureDrawingContext textureContext = this.createContext(this.texturer);
		textureContext.draw(renderContext, position);
		textureContext.close();
	}
	
	/**
	 * Creates texture drawing context for this element.
	 * @param manager The texture manager to initialize the context with
	 * @return A new texture drawing context.
	 */
	public TextureDrawingContext createContext(TextureManager manager) throws IllegalStateException
	{
		if(TexturedElement.current_context != null) throw new IllegalStateException("Attempting to create a nested context!");
		TexturedElement.current_context = new TextureDrawingContext(manager, this.getWidth(), this.getHeight());
		TexturedElement.current_context.enable(manager);
		return TexturedElement.current_context;
	}
	
	public class TextureDrawingContext implements OverlayElement, Closeable {
		public final TextureManager manager;
		
		protected final Dimension size;
		
		public TextureDrawingContext(TextureManager manager, int width, int height)
		{
			this.size = new Dimension(width, height);
			this.manager = manager;
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

		public void drawPartial(ReadablePoint position, ReadablePoint offset, int width, int height)
		{
			Gui.drawModalRectWithCustomSizedTexture(position.getX(), position.getY(), offset_x + offset.getX(), offset_y + offset.getY(), width, height, resource.texturedim.getWidth(), resource.texturedim.getHeight());
		}
		
		public void draw(ReadablePoint position)
		{
			this.drawPartial(position, new Point(0, 0), this.size.getWidth(), this.size.getHeight());
		}
		
		@Override
		public void draw(RenderFrameContext context, ReadablePoint position)
		{
			this.draw(position);
		}
		
		@Override
		public ReadableDimension getSize()
		{
			return this.size;
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
