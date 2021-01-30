package enginecrafter77.survivalinc.season;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.util.ResourceLocation;

public class SeasonCalendar {
	
	private final Map<ResourceLocation, Integer> namemap;
	private final List<SeasonCalendarEntry> entries;
	
	private int length;
	
	public SeasonCalendar()
	{
		this.entries = new ArrayList<SeasonCalendarEntry>();
		this.namemap = new HashMap<ResourceLocation, Integer>();
	}
	
	public int getSeasonCount()
	{
		return this.entries.size();
	}
	
	public List<SeasonCalendarEntry> getSeasons()
	{
		return ImmutableList.copyOf(this.entries);
	}
	
	public SeasonCalendarEntry registerSeason(SeasonProvider season)
	{
		SeasonCalendarEntry entry = this.createNewEntry(season);
		this.namemap.put(entry.getIdentifier(), entry.index);
		this.entries.add(entry);
		this.length += season.getLength();
		return entry;
	}
	
	public SeasonCalendarEntry getSeason(ResourceLocation name)
	{
		try
		{
			int index = this.namemap.get(name);
			return this.entries.get(index);
		}
		catch(NullPointerException exc)
		{
			SurvivalInc.logger.warn("Requested season entry with name \"{}\" not found.", name.toString());
			return null;
		}
	}
	
	public int getYearLength()
	{
		return this.length;
	}
	
	protected SeasonCalendarEntry createNewEntry(SeasonProvider provider)
	{
		return new SeasonCalendarEntry(provider);
	}
	
	private SeasonCalendarEntry at(int index)
	{
		index %= this.entries.size();
		if(index < 0) index += this.entries.size();
		return this.entries.get(index);
	}
	
	public class SeasonCalendarEntry
	{
		public final SeasonProvider instance;
		
		private final int index;
		
		public SeasonCalendarEntry(SeasonProvider instance)
		{
			this.index = SeasonCalendar.this.getSeasonCount();
			this.instance = instance;
		}
		
		public SeasonCalendar getCalendar()
		{
			return SeasonCalendar.this;
		}
		
		public SeasonProvider getSeason()
		{
			return this.instance;
		}
		
		public ResourceLocation getIdentifier()
		{
			return this.getSeason().getName();
		}
		
		public int getStartingDay()
		{
			int day = 0;
			for(int index = 0; index < this.index; index++)
				day += SeasonCalendar.this.at(index).getSeason().getLength();
			return day;
		}
		
		public SeasonCalendarEntry getFollowing(int steps)
		{
			return SeasonCalendar.this.at(this.index + steps);
		}
		
		@Override
		public String toString()
		{
			return String.format("SeasonCalendarEntry(%s)", this.getSeason().getName().toString());
		}
	}
}
