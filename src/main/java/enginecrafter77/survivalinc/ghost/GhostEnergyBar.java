package enginecrafter77.survivalinc.ghost;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.client.TextureResource;
import enginecrafter77.survivalinc.client.TexturedElement;
import enginecrafter77.survivalinc.client.Direction2D;
import enginecrafter77.survivalinc.client.Position2D;
import enginecrafter77.survivalinc.client.StatFillBar;
import enginecrafter77.survivalinc.stats.StatTracker;
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
		super(GhostProvider.instance, Direction2D.RIGHT, new TexturedElement(texture, 0, 0, 9, 9));
		this.addLayer(new TexturedElement(texture, 0, 9, 9, 9), GhostEnergyRecord::getNormalizedValue);
		this.addLayer(new TexturedElement(texture, 0, 18, 9, 9), GhostEnergyBar::ressurectionValue);
		this.setCapacity(10);
		this.setSpacing(-1);
	}
	
	public static Float ressurectionValue(GhostEnergyRecord record)
	{
		return record.isResurrectionActive() ? record.getResurrectionProgress() : null;
	}
	
	@Override
	public void draw(Position2D position, float partialTicks, StatTracker tracker)
	{
		if(tracker.getRecord(this.provider).isActive())
		{
			super.draw(position, partialTicks, tracker);
		}
	}

}
