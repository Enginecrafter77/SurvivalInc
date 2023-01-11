package enginecrafter77.survivalinc.season.calendar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import enginecrafter77.survivalinc.season.AbstractSeason;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class SimpleSeasonCalendar implements SeasonCalendar {
	
	private final ImmutableMap<ResourceLocation, Integer> idMapping;

	private final ImmutableList<SimpleCalendarBoundSeason> seasonList;
	
	private final int length;
	
	public SimpleSeasonCalendar(List<AbstractSeason> seasons)
	{
		ImmutableMap.Builder<ResourceLocation, Integer> idMapBuilder = ImmutableMap.builder();
		ImmutableList.Builder<SimpleCalendarBoundSeason> seasonListBuilder = ImmutableList.builder();

		int nextDay = 0;
		for(int index = 0; index < seasons.size(); ++index)
		{
			AbstractSeason season = seasons.get(index);
			SimpleCalendarBoundSeason boundSeason = new SimpleCalendarBoundSeason(season, this, nextDay, index);
			idMapBuilder.put(boundSeason.getIdentifier(), index);
			seasonListBuilder.add(boundSeason);
			nextDay += season.getLength();
		}

		this.idMapping = idMapBuilder.build();
		this.seasonList = seasonListBuilder.build();
		this.length = nextDay;
	}

	@Override
	public List<? extends CalendarBoundSeason> getSeasons()
	{
		return this.seasonList;
	}

	@Nullable
	@Override
	public SimpleCalendarBoundSeason findSeason(ResourceLocation name)
	{
		return Optional.ofNullable(this.idMapping.get(name)).map(this.seasonList::get).orElse(null);
	}
	
	public int getYearLengthDays()
	{
		return this.length;
	}

	@Override
	public CalendarBoundSeason getSeasonDuring(int day)
	{
		day %= this.getYearLengthDays();

		List<? extends CalendarBoundSeason> seasons = this.getSeasons();
		for(int index = 1; index < seasons.size(); ++index)
		{
			CalendarBoundSeason season = seasons.get(index);
			if(season.getStartingDay() > day)
				return seasons.get(index - 1);
		}
		throw new IllegalArgumentException("Day %d is not in any season. Weird.");
	}
}
