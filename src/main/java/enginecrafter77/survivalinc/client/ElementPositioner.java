package enginecrafter77.survivalinc.client;

import net.minecraft.client.gui.ScaledResolution;
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
public class ElementPositioner {
	
	/** The X offset from the {@link #mulX X position origin} */
	protected int offX;
	
	/** The Y offset from the {@link #mulY Y position origin} */
	protected int offY;
	
	/** The X position origin */
	protected float mulX;
	
	/** The Y position origin */
	protected float mulY;
	
	public ElementPositioner()
	{
		this.mulX = 0F;
		this.mulY = 0F;
		this.offX = 0;
		this.offY = 0;
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
	public int getX(ScaledResolution resolution)
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
	public int getY(ScaledResolution resolution)
	{
		return Math.round((float)resolution.getScaledHeight() * this.mulY + (float)this.offY);
	}
}
