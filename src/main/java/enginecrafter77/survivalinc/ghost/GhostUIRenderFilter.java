package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.client.ElementRenderFilter;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.gui.ScaledResolution;

public class GhostUIRenderFilter implements ElementRenderFilter<StatTracker> {

	@Override
	public boolean begin(ScaledResolution resoultion, StatTracker arg)
	{
		return !arg.getRecord(GhostProvider.instance).isActive();
	}

	@Override
	public void end(ScaledResolution resoultion, StatTracker arg) {}

}
