package enginecrafter77.survivalinc.ghost;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.client.TextureResource;
import enginecrafter77.survivalinc.client.ElementPositioner;
import enginecrafter77.survivalinc.client.ImmutableElementPosition;
import enginecrafter77.survivalinc.client.SimpleOverlayElement;
import enginecrafter77.survivalinc.client.SymbolFillBar;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GhostEnergyBar extends SimpleOverlayElement<StatTracker> {
	
	public static final TextureResource texture = new TextureResource(new ResourceLocation(SurvivalInc.MOD_ID, "textures/gui/ghostenergy.png"), 9, 27);
	public static final Set<ElementType> replaced = ImmutableSet.of(ElementType.FOOD, ElementType.ARMOR, ElementType.HEALTH);
	public static final ElementPositioner position = new ImmutableElementPosition(0.5F, 1F, -91, -39);
	public static final int count = 10;
	
	public final SymbolFillBar hearts_bg, hearts_fill, hearts_rev;
	
	public GhostEnergyBar()
	{
		super(GhostEnergyBar.count * 9, 9);
		
		this.hearts_bg = new SymbolFillBar(texture.createDrawable(0, 0, 9, 9, true), GhostEnergyBar.count);
		this.hearts_bg.setSpacing(-1);
		
		this.hearts_fill = new SymbolFillBar(texture.createDrawable(0, 9, 9, 9, true), GhostEnergyBar.count);
		this.hearts_fill.setSpacing(-1);
		
		this.hearts_rev = new SymbolFillBar(texture.createDrawable(0, 18, 9, 9, true), GhostEnergyBar.count);
		this.hearts_rev.setSpacing(-1);
	}
	
	@Override
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, StatTracker tracker)
	{
		GhostEnergyRecord energy = (GhostEnergyRecord)tracker.getRecord(GhostProvider.instance);
		if(energy.isActive())
		{
			this.hearts_bg.draw(resolution, GhostEnergyBar.position, partialTicks, 1F);
			this.hearts_fill.draw(resolution, GhostEnergyBar.position, partialTicks, energy.getNormalizedValue());
			if(energy.isResurrectionActive())
				this.hearts_rev.draw(resolution, GhostEnergyBar.position, partialTicks, energy.getResurrectionProgress());
		}
	}
	
	@Override
	public Set<ElementType> disableElements(StatTracker tracker)
	{
		GhostEnergyRecord ghost = (GhostEnergyRecord)tracker.getRecord(GhostProvider.instance);
		return ghost.isActive() ? GhostEnergyBar.replaced : ALLOW_ALL;
	}

}
