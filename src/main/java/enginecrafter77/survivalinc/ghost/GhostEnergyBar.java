package enginecrafter77.survivalinc.ghost;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.client.ElementPositioner;
import enginecrafter77.survivalinc.client.ImmutableElementPosition;
import enginecrafter77.survivalinc.client.SimpleOverlayElement;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GhostEnergyBar extends SimpleOverlayElement<StatTracker> {
	
	public static final ResourceLocation texture = new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/ghostenergy.png");
	public static final Set<ElementType> replaced = ImmutableSet.of(ElementType.FOOD, ElementType.ARMOR, ElementType.HEALTH);
	public static final ElementPositioner position = new ImmutableElementPosition(0.5F, 1F, -91, -39);
	public static final int count = 10;
	
	public GhostEnergyBar()
	{
		super(GhostEnergyBar.count * 9, 9);
	}
	
	@Override
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, StatTracker tracker)
	{
		GhostEnergyRecord energy = (GhostEnergyRecord)tracker.getRecord(GhostProvider.instance);
		if(energy.isActive())
		{
			this.texturer.bindTexture(GhostEnergyBar.texture);
			GlStateManager.enableAlpha();
			this.renderHearts(resolution, 1F, 0, 0);
			this.renderHearts(resolution, energy.getNormalizedValue(), 0, 9);
			if(energy.isResurrectionActive()) this.renderHearts(resolution, energy.getResurrectionProgress(), 0, 18);
			GlStateManager.disableAlpha();
		}
	}
	
	public void renderHearts(ScaledResolution resolution, float fill, int u, int v)
	{
		float value = GhostEnergyBar.count * fill;
		int y = GhostEnergyBar.position.getY(resolution);
		for(int current = 0; current < GhostEnergyBar.count; current++)
		{
			int offset = GhostEnergyBar.position.getX(resolution) + current * 8;			
			if(value > 0)
			{
				float part = value > 1F ? 1F : value;
				Gui.drawModalRectWithCustomSizedTexture(offset, y, u, v, Math.round(part * 9F), this.getHeight(), 9, 27);
				value -= part;
			}
		}
	}
	
	@Override
	public Set<ElementType> disableElements(StatTracker tracker)
	{
		GhostEnergyRecord ghost = (GhostEnergyRecord)tracker.getRecord(GhostProvider.instance);
		return ghost.isActive() ? GhostEnergyBar.replaced : ALLOW_ALL;
	}

}
