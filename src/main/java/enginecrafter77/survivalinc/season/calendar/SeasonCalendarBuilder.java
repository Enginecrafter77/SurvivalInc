package enginecrafter77.survivalinc.season.calendar;

import enginecrafter77.survivalinc.season.AbstractSeason;
import net.minecraft.util.ResourceLocation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SeasonCalendarBuilder {
	private final Map<ResourceLocation, SeasonBuilderEntry> seasonList;

	public SeasonCalendarBuilder()
	{
		this.seasonList = new HashMap<ResourceLocation, SeasonBuilderEntry>();
	}

	public int getSeasonCount()
	{
		return this.seasonList.size();
	}

	public boolean isRegistered(ResourceLocation id)
	{
		return this.seasonList.containsKey(id);
	}

	public SeasonCalendarBuilder register(AbstractSeason season)
	{
		ResourceLocation key = season.getId();
		SeasonBuilderEntry entry = this.seasonList.get(key);
		if(entry == null)
			entry = new SeasonBuilderEntry(season, this.seasonList.size());
		else
			entry.replaceSeason(season);
		this.seasonList.put(key, entry);
		return this;
	}

	public <CAL extends SeasonCalendar> CAL build(SeasonCalendarFactory<CAL> factory)
	{
		List<AbstractSeason> seasons = this.seasonList.values().stream().sorted(SeasonBuilderEntry.ORDINAL_COMPARATOR).map(SeasonBuilderEntry::getSeason).collect(Collectors.toList());
		return factory.createCalendar(seasons);
	}

	protected static class SeasonBuilderEntry
	{
		public static final Comparator<SeasonBuilderEntry> ORDINAL_COMPARATOR = Comparator.comparing(SeasonBuilderEntry::getOrdinal);

		private final int ordinal;

		private AbstractSeason season;

		public SeasonBuilderEntry(AbstractSeason season, int ordinal)
		{
			this.season = season;
			this.ordinal = ordinal;
		}

		public void replaceSeason(AbstractSeason season)
		{
			this.season = season;
		}

		public int getOrdinal()
		{
			return this.ordinal;
		}

		public AbstractSeason getSeason()
		{
			return this.season;
		}
	}
}
