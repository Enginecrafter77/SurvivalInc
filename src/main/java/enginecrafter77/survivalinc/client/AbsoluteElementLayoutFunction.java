package enginecrafter77.survivalinc.client;

import com.google.common.collect.ImmutableMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.ReadablePoint;

/**
 * AbsoluteElementLayoutFunction is a class used to dynamically compute position of an on-screen element, using a linear mathematical function.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
public class AbsoluteElementLayoutFunction implements ElementLayoutFunction {
	public static final AbsoluteElementLayoutFunction ORIGIN = new AbsoluteElementLayoutFunction(0F, 0F, 0, 0);
	public static final AbsoluteElementLayoutFunction CENTER = new AbsoluteElementLayoutFunction(0.5F, 0.5F, 0, 0);
	
	protected final ImmutableMap<Axis2D, Integer> offset;
	protected final ImmutableMap<Axis2D, Float> origin;
	
	public AbsoluteElementLayoutFunction(float mx, float my, int ox, int oy)
	{
		this.offset = ImmutableMap.of(Axis2D.HORIZONTAL, ox, Axis2D.VERTICAL, oy);
		this.origin = ImmutableMap.of(Axis2D.HORIZONTAL, mx, Axis2D.VERTICAL, my);
	}
	
	public int calculateAxialPosition(RenderFrameContext context, Axis2D axis)
	{
		return (int)((float)axis.getResolutionAxialValue(context.getResolution()) * this.origin.get(axis)) + this.offset.get(axis);
	}
	
	@Override
	public ReadablePoint getPositionFor(RenderFrameContext context, OverlayElement element)
	{
		return ElementLayoutFunction.fromFunction(this::calculateAxialPosition, context);
	}
}
