package enginecrafter77.survivalinc.client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.Point;
import org.lwjgl.util.ReadablePoint;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * ElementLayoutFunction describes a functional interface that
 * is used to dynamically position an element onto a screen.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
@FunctionalInterface
public interface ElementLayoutFunction {
	public ReadablePoint getPositionFor(RenderFrameContext context, OverlayElement element);
	
	public static ReadablePoint fromFunction(Function<Axis2D, Integer> provider)
	{
		return new Point(provider.apply(Axis2D.HORIZONTAL), provider.apply(Axis2D.VERTICAL));
	}
	
	public static ReadablePoint fromFunction(BiFunction<RenderFrameContext, Axis2D, Integer> provider, RenderFrameContext context)
	{
		return new Point(provider.apply(context, Axis2D.HORIZONTAL), provider.apply(context, Axis2D.VERTICAL));
	}
}
