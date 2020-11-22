package enginecrafter77.survivalinc.client;

import java.awt.Color;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Deprecated
@SideOnly(Side.CLIENT)
public class SimpleStatBar extends OverlayElementGroup<StatTracker> {	
	public final StatProvider<? extends SimpleStatRecord> provider;
	
	public SimpleStatBar(StatProvider<? extends SimpleStatRecord> provider, TexturedElement icon, Color color)
	{
		super(Axis2D.VERTICAL);
		this.provider = provider;
		
		this.elements.add(new DifferentialArrow(provider, 8, 12, true));
		this.elements.add(new ElementTypeAdapter<StatTracker, Float>(new GaugeBar(color), this::getRecordValue));
		this.elements.add(icon);
	}

	@Override
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, StatTracker tracker)
	{
		if(tracker.isActive(this.provider, Minecraft.getMinecraft().player))
		{
			super.draw(resolution, position, partialTicks, tracker);
		}
	}

	private Float getRecordValue(StatTracker tracker)
	{
		return tracker.getRecord(provider).getNormalizedValue();
	}
}