package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.client.OverlayElement;
import enginecrafter77.survivalinc.client.RenderFrameContext;
import enginecrafter77.survivalinc.client.RenderStageFilter;
import enginecrafter77.survivalinc.client.StackingElementLayoutFunction;
import enginecrafter77.survivalinc.stats.StatCapability;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.ReadablePoint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class GhostEnergyBarRenderer implements RenderStageFilter {
	private final ElementType renderGhostEnergyOn;
	private final OverlayElement ghostEnergyIndicator;

	private final Set<ElementType> suppressedStages;

	public GhostEnergyBarRenderer(ElementType renderGhostEnergyOn, OverlayElement ghostEnergyIndicator)
	{
		this.renderGhostEnergyOn = renderGhostEnergyOn;
		this.ghostEnergyIndicator = ghostEnergyIndicator;

		this.suppressedStages = new HashSet<ElementType>();
	}

	public GhostEnergyBarRenderer suppresses(Collection<ElementType> elements)
	{
		this.suppressedStages.addAll(elements);
		return this;
	}

	protected boolean isGhostActive()
	{
		return StatCapability.obtainRecord(SurvivalInc.ghost, Minecraft.getMinecraft().player).map(GhostEnergyRecord::isActive).orElse(false);
	}

	@Override
	public boolean begin(RenderFrameContext context, ElementType element)
	{
		if(!this.isGhostActive())
			return true;

		if(GhostEnergyBarRenderer.this.renderGhostEnergyOn == element)
		{
			OverlayElement indicator = GhostEnergyBarRenderer.this.ghostEnergyIndicator;
			ReadablePoint position = StackingElementLayoutFunction.LEFT.getPositionFor(context, indicator);
			indicator.draw(context, position);
		}
		return !GhostEnergyBarRenderer.this.suppressedStages.contains(element);
	}

	@Override
	public void end(RenderFrameContext context, ElementType element) {}
}
