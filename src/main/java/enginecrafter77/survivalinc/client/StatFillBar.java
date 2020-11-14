package enginecrafter77.survivalinc.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class StatFillBar<RECORD extends StatRecord> implements OverlayElement<StatTracker> {
	
	public final SymbolFillBar background;
	
	public final StatProvider provider;
	
	protected final Map<SymbolFillBar, Function<RECORD, Float>> bars;
	
	private final Class<RECORD> recordclass;
	private int spacing;
	
	public StatFillBar(StatProvider provider, Class<RECORD> record, TexturedElement base, int count)
	{
		this.bars = new LinkedHashMap<SymbolFillBar, Function<RECORD, Float>>();
		this.background = new SymbolFillBar(base, count);
		this.recordclass = record;
		this.provider = provider;
		this.spacing = 0;
		
		if(!record.isAssignableFrom(provider.createNewRecord().getClass()))
		{
			throw new ClassCastException("Provider StatProvider doesn't use the desired records!");
		}
	}
	
	@Override
	public Set<ElementType> disableElements(StatTracker arg)
	{
		return OverlayElement.ALLOW_ALL;
	}

	@Override
	public int getWidth()
	{
		return this.background.getWidth();
	}

	@Override
	public int getHeight()
	{
		return this.background.getHeight();
	}
	
	public void setSpacing(int spacing)
	{
		this.spacing = spacing;
		this.background.setSpacing(spacing);
		for(SymbolFillBar bar : this.bars.keySet()) bar.setSpacing(spacing);
	}
	
	public void addOverlay(TexturedElement texture, Function<RECORD, Float> getter)
	{
		SymbolFillBar bar = new SymbolFillBar(texture, this.background.count);
		bar.setSpacing(spacing);
		this.bars.put(bar, getter);
	}
	
	public RECORD getRecord(StatTracker tracker)
	{
		return this.recordclass.cast(tracker.getRecord(this.provider));
	}
	
	@Override
	public void draw(ScaledResolution resolution, ElementPositioner position, float partialTicks, StatTracker arg)
	{
		this.background.draw(resolution, position, partialTicks, 1F);
		
		RECORD record = this.getRecord(arg);
		for(Map.Entry<SymbolFillBar, Function<RECORD, Float>> entry : this.bars.entrySet())
		{
			Function<RECORD, Float> transformer = entry.getValue();
			Float value = transformer.apply(record);
			if(value != null) entry.getKey().draw(resolution, position, partialTicks, value);
		}
	}
	
}
