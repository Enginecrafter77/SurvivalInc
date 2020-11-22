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
	protected final int offX;
	
	/** The Y offset from the {@link #mulY Y position origin} */
	protected final int offY;
	
	/** The X position origin */
	protected final float mulX;
	
	/** The Y position origin */
	protected final float mulY;
	
	public ElementPositioner(float mx, float my, int ox, int oy)
	{
		this.mulX = mx;
		this.mulY = my;
		this.offX = ox;
		this.offY = oy;
	}
	
	public ElementPositioner(ElementPositioner origin, int x, int y)
	{
		this(origin.mulX, origin.mulY, origin.offX + x, origin.offY + y);
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
		return (int)((float)resolution.getScaledWidth() * this.mulX) + this.offX;
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
		return (int)((float)resolution.getScaledHeight() * this.mulY) + this.offY;
	}
}
