package enginecrafter77.survivalinc.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class StatFillBar<RECORD extends StatRecord> implements OverlayElement<StatTracker> {
	
	public final SymbolFillBar background;
	
	public final StatProvider<RECORD> provider;
	
	protected final Map<SymbolFillBar, Function<RECORD, Float>> bars;
	
	public StatFillBar(StatProvider<RECORD> provider, Class<RECORD> record, Direction2D direction, TexturedElement base)
	{
		this.bars = new LinkedHashMap<SymbolFillBar, Function<RECORD, Float>>();
		this.background = new SymbolFillBar(base, direction);
		this.provider = provider;
	}
	
	public void setSpacing(int spacing)
	{
		this.background.setSpacing(spacing);
		for(SymbolFillBar bar : this.bars.keySet()) bar.setSpacing(spacing);
	}
	
	public void setCapacity(int capacity)
	{
		this.background.setCapacity(capacity);
		for(SymbolFillBar bar : this.bars.keySet()) bar.setCapacity(capacity);
	}
	
	public void addOverlay(TexturedElement texture, Function<RECORD, Float> getter)
	{
		SymbolFillBar bar = new SymbolFillBar(texture, this.background.direction);
		bar.setCapacity(this.background.getCapacity());
		bar.setSpacing(this.background.getSpacing());
		this.bars.put(bar, getter);
	}
	
	@Override
	public int getSize(Axis2D axis)
	{
		return this.background.getSize(axis);
	}
	
	@Override
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, StatTracker arg)
	{
		if(arg.isActive(this.provider, Minecraft.getMinecraft().player))
		{
			this.background.draw(resolution, position, partialTicks, 1F);
			
			RECORD record = arg.getRecord(this.provider);
			for(Map.Entry<SymbolFillBar, Function<RECORD, Float>> entry : this.bars.entrySet())
			{
				Function<RECORD, Float> transformer = entry.getValue();
				Float value = transformer.apply(record);
				if(value != null) entry.getKey().draw(resolution, position, partialTicks, value);
			}
		}
	}
	
}
