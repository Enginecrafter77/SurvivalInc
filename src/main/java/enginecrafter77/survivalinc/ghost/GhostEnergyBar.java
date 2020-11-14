package enginecrafter77.survivalinc.ghost;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.client.TextureResource;
import enginecrafter77.survivalinc.client.TexturedElement;
import enginecrafter77.survivalinc.client.ElementPositioner;
import enginecrafter77.survivalinc.client.StatFillBar;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GhostEnergyBar extends StatFillBar<GhostEnergyRecord> {
	
	public static final TextureResource texture = new TextureResource(new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/ghostenergy.png"), 9, 27);
	public static final Set<ElementType> replaced = ImmutableSet.of(ElementType.FOOD, ElementType.ARMOR, ElementType.HEALTH);
	
	public GhostEnergyBar()
	{
		super(GhostProvider.instance, GhostEnergyRecord.class, new TexturedElement(texture, 0, 0, 9, 9, true), 10);
		this.addOverlay(new TexturedElement(texture, 0, 9, 9, 9, true), GhostEnergyRecord::getNormalizedValue);
		this.addOverlay(new TexturedElement(texture, 0, 18, 9, 9, true), GhostEnergyBar::ressurectionValue);
		this.setSpacing(-1);
	}
	
	public static Float ressurectionValue(GhostEnergyRecord record)
	{
		return record.isResurrectionActive() ? record.getResurrectionProgress() : null;
	}
	
	@Override
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, StatTracker tracker)
	{
		if(tracker.getRecord(this.provider).isActive())
		{
			super.draw(resolution, position, partialTicks, tracker);
		}
	}

	@Override
	public Set<ElementType> disableElements(StatTracker tracker)
	{
		return tracker.getRecord(this.provider).isActive() ? GhostEnergyBar.replaced : ALLOW_ALL;
	}

}
