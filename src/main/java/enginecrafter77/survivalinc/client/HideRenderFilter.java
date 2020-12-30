package enginecrafter77.survivalinc.client;

import java.util.function.Predicate;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * HideRenderFilter is a simple filter used to hide
 * the target element whenever it's predicate returns true.
 * @author Enginecrafter77
 * @param <ARG> The filter argument
 */
@SideOnly(Side.CLIENT)
public class HideRenderFilter<ARG> implements ElementRenderFilter<ARG> {

	/** The condition to check */
	public final Predicate<ARG> condition;
	
	public HideRenderFilter(Predicate<ARG> condition)
	{
		this.condition = condition;
	}
	
	@Override
	public boolean begin(ScaledResolution resoultion, ARG arg)
	{
		return !condition.test(arg);
	}

	@Override
	public void end(ScaledResolution resoultion, ARG arg) {}

}
