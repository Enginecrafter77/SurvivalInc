package enginecrafter77.survivalinc.client;

import java.util.Comparator;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * ScalableOverlayElement specifies an element, which can be dynamically
 * positioned on the screen using simple mathematics. The element basically
 * uses four variables: {@link #mulX}, {@link #mulY}, {@link #offX}, {@link #offY}.
 * Each of these variables are used to compute the resultant X and Y positions
 * in {@link #calculateX(ScaledResolution)} and {@link #calculateY(ScaledResolution)}
 * respectively. The position is only recalculated when the element detects a change
 * in resolution.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public abstract class ScalableOverlayElement implements OverlayElement {
	
	public static final Comparator<ScaledResolution> resolutionComparator = new Comparator<ScaledResolution>() {
		@Override
		public int compare(ScaledResolution first, ScaledResolution second)
		{
			if(first == null && second == null) return 0; // If both are null, they are equal
			if(first == null) return 1; // If only first is null, the second is automatically bigger
			if(second == null) return -1; // If only second is null, the first is automatically bigger
			
			int fpixels = first.getScaledWidth() * first.getScaledHeight() * first.getScaleFactor();
			int spixels = second.getScaledWidth() * second.getScaledHeight() * first.getScaleFactor();
			
			if(fpixels == spixels)
			{
				float fratio = (float)(first.getScaledWidth_double() / first.getScaledHeight_double());
				float sratio = (float)(second.getScaledWidth_double() / second.getScaledHeight_double());
				return Float.compare(fratio, sratio);
			}
			
			return Integer.compare(fpixels, spixels);
		}
	};
	
	/** The X offset from the {@link #mulX X position origin} */
	protected int offX;
	
	/** The Y offset from the {@link #mulY Y position origin} */
	protected int offY;
	
	/** The X position origin */
	protected float mulX;
	
	/** The Y position origin */
	protected float mulY;
	
	/** The resolution the position is currently computed for */
	private ScaledResolution computed;
	
	/** The absolute X position of the element */
	private int posX;
	
	/** The absolute Y position of the element */
	private int posY;
	
	public final int width, height;
	
	public ScalableOverlayElement(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		this.computed = null;
		this.mulX = 0F;
		this.mulY = 0F;
		this.offX = 0;
		this.offY = 0;
		this.posX = 0;
		this.posY = 0;
	}
	
	public abstract void draw();
	
	@Override
	public void draw(RenderGameOverlayEvent event)
	{
		ScaledResolution res = event.getResolution();
		if(ScalableOverlayElement.resolutionComparator.compare(res, this.computed) != 0)
		{
			this.onResolutionChange(res);
			this.computed = res;
		}
		
		this.draw();
	}
	
	@Override
	public int getWidth()
	{
		return this.width;
	}

	@Override
	public int getHeight()
	{
		return this.height;
	}
	
	/**
	 * Sets the absolute offsets relative to the screen
	 * origin.
	 * @see #setPositionOrigin(float, float)
	 * @param x The X offset
	 * @param y The Y offset
	 */
	public void setPositionOffset(int x, int y)
	{
		this.offX = x;
		this.offY = y;
		this.markPositionForUpdate();
	}
	
	/**
	 * Sets the position of the origin point relative
	 * to the screen dimensions. For example, value
	 * 0.5 on X indicates that the origin will be in
	 * the middle of the screen.
	 * @see #setPositionOffset(int, int)
	 * @param x The fraction of the screen dimension for the X origin point
	 * @param y The fraction of the screen dimension for the Y origin point
	 */
	public void setPositionOrigin(float x, float y)
	{
		this.mulX = x;
		this.mulY = y;
		this.markPositionForUpdate();
	}
	
	/**
	 * Calculates the X position of the element with regards to the
	 * supplied resolution. By default, this equals to:
	 * <pre>
	 * 	x = w.m + o
	 * </pre>
	 * Where w is the width of the screen, m is the {@link #mulX origin}
	 * and o is the {@link #offX offset}.
	 * @param resolution The resolution to compute for
	 * @return Absolute X coordinate relative to the top left screen corner
	 */
	protected int calculateX(ScaledResolution resolution)
	{
		return Math.round((float)resolution.getScaledWidth() * this.mulX + (float)this.offX);
	}
	
	/**
	 * Calculates the Y position of the element with regards to the
	 * supplied resolution. By default, this equals to:
	 * <pre>
	 * 	y = h.m + o
	 * </pre>
	 * Where h is the width of the screen, m is the {@link #mulY origin}
	 * and o is the {@link #offY offset}.
	 * @param resolution The resolution to compute for
	 * @return Absolute Y coordinate relative to the top left screen corner
	 */
	protected int calculateY(ScaledResolution resolution)
	{
		return Math.round((float)resolution.getScaledHeight() * this.mulY + (float)this.offY);
	}
	
	/**
	 * @return The X coordinate of the element
	 */
	public int getX()
	{
		return this.posX;
	}
	
	/**
	 * @return The Y coordinate of the element
	 */
	public int getY()
	{
		return this.posY;
	}
	
	/**
	 * Forces the position to be recalculated next render tick,
	 * no matter whether the resolution changed or not.
	 */
	public void markPositionForUpdate()
	{
		this.computed = null;
	}
	
	/**
	 * This method gets called whenever resolution changes, OR
	 * the change is forced using {@link #markPositionForUpdate()}.
	 * @param resolution The new resolution for rendering this element
	 */
	public void onResolutionChange(ScaledResolution resolution)
	{
		this.posX = this.calculateX(resolution);
		this.posY = this.calculateY(resolution);
	}
}
