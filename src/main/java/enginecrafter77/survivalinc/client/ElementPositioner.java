package enginecrafter77.survivalinc.client;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.lwjgl.util.Point;
import org.lwjgl.util.ReadablePoint;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * ElementPositioner describes a functional interface that
 * is used to dynamically position an element onto a screen.
 * @author Enginecrafter77
 */
@SideOnly(Side.CLIENT)
@FunctionalInterface
public interface ElementPositioner {	
	public ReadablePoint getPositionFor(ScaledResolution resolution, OverlayElement element);
	
	public static ReadablePoint fromFunction(Function<Axis2D, Integer> provider)
	{
		return new Point(provider.apply(Axis2D.HORIZONTAL), provider.apply(Axis2D.VERTICAL));
	}
	
	public static ReadablePoint fromFunction(BiFunction<Axis2D, ScaledResolution, Integer> provider, ScaledResolution resolution)
	{
		return new Point(provider.apply(Axis2D.HORIZONTAL, resolution), provider.apply(Axis2D.VERTICAL, resolution));
	}
}
