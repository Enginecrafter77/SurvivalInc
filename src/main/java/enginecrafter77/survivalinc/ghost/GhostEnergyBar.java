package enginecrafter77.survivalinc.ghost;

import com.google.common.collect.ImmutableSet;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.client.Direction2D;
import enginecrafter77.survivalinc.client.RenderFrameContext;
import enginecrafter77.survivalinc.client.StatFillBar;
import enginecrafter77.survivalinc.client.TextureResource;
import enginecrafter77.survivalinc.stats.StatCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.ReadablePoint;
import org.lwjgl.util.Rectangle;

import java.util.Set;

@SideOnly(Side.CLIENT)
public class GhostEnergyBar extends StatFillBar<GhostEnergyRecord> {
	
	public static final TextureResource texture = new TextureResource(new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/ghostenergy.png"), 9, 27);
	public static final Set<ElementType> replaced = ImmutableSet.of(ElementType.FOOD, ElementType.ARMOR, ElementType.HEALTH);
	
	public GhostEnergyBar()
	{
		super(SurvivalInc.ghost, Direction2D.RIGHT, texture.region(new Rectangle(0, 0, 9, 9)));
		this.addLayer(texture.region(new Rectangle(0, 9, 9, 9)), GhostEnergyRecord::getNormalizedValue);
		this.addLayer(texture.region(new Rectangle(0, 18, 9, 9)), GhostEnergyBar::ressurectionValue);
		this.setCapacity(10);
		this.setSpacing(-1);
	}
	
	public static float ressurectionValue(GhostEnergyRecord record)
	{
		return record.isResurrectionActive() ? record.getResurrectionProgress() : 0F;
	}
	
	@Override
	public void draw(RenderFrameContext context, ReadablePoint position)
	{
		if(StatCapability.obtainRecord(SurvivalInc.ghost, Minecraft.getMinecraft().player).map(GhostEnergyRecord::isActive).orElse(false))
			super.draw(context, position);
	}

}
