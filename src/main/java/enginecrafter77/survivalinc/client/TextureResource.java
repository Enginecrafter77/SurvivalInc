package enginecrafter77.survivalinc.client;

import org.lwjgl.util.Dimension;
import org.lwjgl.util.Point;
import org.lwjgl.util.ReadableDimension;
import org.lwjgl.util.ReadablePoint;
import org.lwjgl.util.ReadableRectangle;
import org.lwjgl.util.Rectangle;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

/**
 * TextureResource is an object that specifies
 * a texture that can be drawn onto the screen.
 * Texture is defined by 3 parameters:
 * {@link #texture The texture resource},
 * {@link #texturedim the texture size} and
 * {@link #region the drawn region}.
 * TextureResources can be separated in 2 categories:
 * master and derivatives. Master TextureResources are
 * generally those which have been created through one
 * of the public constructors. These texture resources,
 * when drawn, will draw the entire texture. In other words,
 * it's {@link #region} will be a rectangle at 0, 0 with
 * the exact same dimensions as {@link #texturedim}.
 * Of course, this is not always desirable behavior. 
 * Because of this, a derivative texture resource can
 * be created using {@link #region(ReadableRectangle)}
 * method. This method restricts the resource on a certain
 * region specified by the parameter. This resource can
 * theoretically be infinitely divided using
 * {@link #region(ReadableRectangle)}.
 * @author Enginecrafter77
 */
public class TextureResource extends SimpleOverlayElement {
	
	/** The {@link ResourceLocation} of the texture */
	public final ResourceLocation texture;
	
	/** The dimensions of the texture specified by {@link #texture} */
	public final ReadableDimension texturedim;
	
	/** The region drawn using {@link #draw(ReadablePoint, float, Object)} */
	public final Rectangle region;
	
	/**
	 * Constructs new master instance of TextureResource with the specified texture parameters.
	 * @param texture The texture resource location
	 * @param width The texture width in pixels
	 * @param height The texture height in pixels
	 */
	public TextureResource(ResourceLocation texture, int width, int height)
	{
		this(texture, new Dimension(width, height));
	}
	
	/**
	 * Constructs new master instance of TextureResource with the specified texture parameters.
	 * @param texture The texture resource location
	 * @param texturesize A {@link ReadableDimension} defining the size of the texture resource
	 */
	public TextureResource(ResourceLocation texture, ReadableDimension texturesize)
	{
		this(texture, texturesize, new Rectangle(new Point(0, 0), texturesize));
	}
	
	/**
	 * Creates TextureResource using the specified texture parameters, along with the provided
	 * region. No region validation is being done in this constructor. As such, it's advised to
	 * use this constructor only if necessary. If possible, use {@link #region(ReadableRectangle)}
	 * instead, since it also does region subset validation.
	 * @param texture The texture resource location
	 * @param texturesize A {@link ReadableDimension} defining the size of the texture resource
	 * @param region The region from this texture to draw
	 */
	protected TextureResource(ResourceLocation texture, ReadableDimension texturesize, Rectangle region)
	{
		super(region);
		this.texturedim = texturesize;
		this.texture = texture;
		this.region = region;
	}
	
	@Override
	protected TextureResource clone()
	{
		return new TextureResource(texture, texturedim, this.region);
	}
	
	/**
	 * Creates a new TextureResource from a cut of this texture. In other words,
	 * grabs a rectangle defined by the parameter out of this texture and makes
	 * a new TextureResource instance wrapping it. This method also validates
	 * the entered rectangle, throwing exceptions when the region is deemed invalid.
	 * @param region The rectangular region to cut from this texture
	 * @throws IllegalArgumentException when the region is empty
	 * @throws TextureOverflowException when the region overflows it's parent
	 * @return A new TextureResource enclosing the defined region from this texture
	 */
	public TextureResource region(ReadableRectangle region)
	{
		Rectangle nested = new Rectangle(region);
		nested.translate(this.region);
		
		if(nested.isEmpty()) throw new IllegalArgumentException("Requested region is empty!");
		if(!this.region.contains(nested)) throw new TextureOverflowException(this, nested);
		
		return new TextureResource(this.texture, this.texturedim, nested);
	}
	
	/**
	 * Cuts a region from the specified point up to the bottom right corner of this texture.
	 * @param offset The first point
	 * @return A new texture resource enclosing the remaining region
	 */
	public TextureResource regionRemaining(ReadablePoint offset)
	{
		Dimension remaining = new Dimension(this.region.getWidth() - offset.getX(), this.region.getHeight() - offset.getY());
		return this.region(new Rectangle(offset, remaining));
	}
	
	@Deprecated
	public void load(TextureManager manager)
	{
		manager.bindTexture(this.texture);
	}

	@Override
	public void draw(ReadablePoint position, float partialTicks, Object... args)
	{
		GlStateManager.enableAlpha();
		this.texturer.bindTexture(this.texture);
		Gui.drawModalRectWithCustomSizedTexture(position.getX(), position.getY(), this.region.getX(), this.region.getY(), this.region.getWidth(), this.region.getHeight(), this.texturedim.getWidth(), this.texturedim.getHeight());
	}
	
	/**
	 * An exception thrown when a sub-region that is outside of the range of the parent region is requested.
	 * @author Enginecrafter77
	 */
	public static class TextureOverflowException extends IllegalArgumentException
	{
		private static final long serialVersionUID = 3504988036989403207L;
		
		/** The parent texture */
		public final TextureResource texture;
		
		/** The requested region */
		public final ReadableRectangle region;
		
		public TextureOverflowException(TextureResource texture, ReadableRectangle region)
		{
			super(String.format("Region(%s) overflows parent texture [%s] region (%s)!", toString(region), texture.texture.toString(), toString(texture.region)));
			this.texture = texture;
			this.region = region;
		}
		
		public static String toString(ReadableRectangle region)
		{
			return String.format("%d:%d %dx%d", region.getX(), region.getY(), region.getWidth(), region.getHeight());
		}
	}
	
}
