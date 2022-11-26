package enginecrafter77.survivalinc.client;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.GuiIngameForge;
import org.lwjgl.util.Point;
import org.lwjgl.util.ReadablePoint;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * StackingElementPositioner is a type of element positioner utilizing {@link GuiIngameForge}'s height
 * fields. Basically, each of these fields indicates how much space from bottom-up has been used. This
 * field is utilized by all of minecraft's rendering. StackingElementPositioner uses this value the same
 * way as forge does. It computes the render origin using this position and increments it accordingly.
 * The vanilla HUD has 2 columns (the stacks). Each of the columns is represented as value in this enum.
 * Thus, one may use a specific instance for the desired column.
 * @author Enginecrafter77
 */
public enum StackingElementLayoutFunction implements ElementLayoutFunction {
	
	LEFT(-91, () -> GuiIngameForge.left_height, (Integer arg) -> { GuiIngameForge.left_height = arg; }),
	RIGHT(10, () -> GuiIngameForge.right_height, (Integer arg) -> { GuiIngameForge.right_height = arg; });
	
	private final Supplier<Integer> getter;
	private final Consumer<Integer> setter;
	private final int x;
	
	private StackingElementLayoutFunction(int x, Supplier<Integer> getter, Consumer<Integer> setter)
	{
		this.getter = getter;
		this.setter = setter;
		this.x = x;
	}
	
	@Override
	public ReadablePoint getPositionFor(ScaledResolution resolution, OverlayElement element)
	{
		int height = this.getter.get();
		int y = resolution.getScaledHeight() - height;
		height += element.getSize().getHeight() + 1; // 1 = spacing
		this.setter.accept(height);
		
		return new Point(resolution.getScaledWidth() / 2 + this.x, y);
	}
	
}
