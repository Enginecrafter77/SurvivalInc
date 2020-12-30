package enginecrafter77.survivalinc.client;

import java.util.function.Predicate;

import net.minecraft.client.gui.ScaledResolution;

public class HideRenderFilter<ARG> implements ElementRenderFilter<ARG>  {

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
