package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.client.ElementRenderFilter;
import enginecrafter77.survivalinc.client.OverlayElement;
import enginecrafter77.survivalinc.client.RenderFrameContext;
import enginecrafter77.survivalinc.client.RenderStageFilter;
import enginecrafter77.survivalinc.stats.StatCapability;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GhostConditionRenderFilter implements ElementRenderFilter, RenderStageFilter {
	public static final GhostConditionRenderFilter INSTANCE = new GhostConditionRenderFilter();
	
	private GhostConditionRenderFilter() {}
	
	@Override
	public boolean begin(RenderFrameContext context, ElementType element)
	{
		return !this.begin(context, (OverlayElement)null);
	}

	@Override
	public void end(RenderFrameContext context, ElementType element)
	{
		this.end(context, (OverlayElement)null);
	}

	@Override
	public boolean begin(RenderFrameContext context, OverlayElement element)
	{
		return StatCapability.obtainRecord(SurvivalInc.ghost, Minecraft.getMinecraft().player).map(GhostEnergyRecord::isActive).orElse(false);
	}

	@Override
	public void end(RenderFrameContext context, OverlayElement element) {}
}
