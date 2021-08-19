package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.client.ElementRenderFilter;
import enginecrafter77.survivalinc.client.OverlayElement;
import enginecrafter77.survivalinc.client.RenderStageFilter;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public final class GhostConditionRenderFilter implements ElementRenderFilter, RenderStageFilter {
	public static final GhostConditionRenderFilter INSTANCE = new GhostConditionRenderFilter();
	
	private GhostConditionRenderFilter() {}
	
	@Override
	public boolean begin(ScaledResolution resoultion, ElementType element)
	{
		return !this.begin(resoultion, (OverlayElement)null);
	}

	@Override
	public void end(ScaledResolution resoultion, ElementType element)
	{
		this.end(resoultion, (OverlayElement)null);
	}

	@Override
	public boolean begin(ScaledResolution resoultion, OverlayElement element)
	{
		StatTracker tracker = Minecraft.getMinecraft().player.getCapability(StatCapability.target, null);
		return tracker.getRecord(GhostProvider.instance).isActive();
	}

	@Override
	public void end(ScaledResolution resoultion, OverlayElement element) {}
}