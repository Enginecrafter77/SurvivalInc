package enginecrafter77.survivalinc.season;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.Range;

public class SeasonCalendar {
	
	private final List<SeasonProvider> entries;
	
	public SeasonCalendar()
	{
		this.entries = new ArrayList<SeasonProvider>();
	}
	
	public void registerSeason(SeasonProvider season)
	{
		this.entries.add(season);
	}
	
	@Deprecated
	public Range<Float> getTemperatureRange()
	{
		return Range.closed(Float.MIN_VALUE, Float.MAX_VALUE);
	}
	
	public int getYearLength()
	{
		return 0;
	}
	
	public int getSeasonCount()
	{
		return this.entries.size();
	}
	
	protected List<SeasonProvider> getPreceding(int index)
	{
		return this.entries.subList(0, index);
	}
	
	protected int indexOf(SeasonProvider provider)
	{
		return this.entries.indexOf(provider);
	}
	
	protected SeasonProvider valueOf(int index)
	{
		return this.entries.get(index);
	}
}
