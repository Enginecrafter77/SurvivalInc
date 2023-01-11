package enginecrafter77.survivalinc.season.calendar;

import enginecrafter77.survivalinc.season.AbstractSeason;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class SimpleCalendarBoundSeason implements CalendarBoundSeason {
	public final SeasonCalendar owner;

	public final AbstractSeason season;

	private final int startingDay;

	private final int ordinal;

	public SimpleCalendarBoundSeason(AbstractSeason season, SeasonCalendar owner, int startingDay, int ordinal)
	{
		this.owner = owner;
		this.season = season;
		this.ordinal = ordinal;
		this.startingDay = startingDay;
	}

	@Override
	public SeasonCalendar getOwningCalendar()
	{
		return this.owner;
	}

	@Override
	public AbstractSeason getSeason()
	{
		return this.season;
	}

	@Override
	public ResourceLocation getIdentifier()
	{
		return this.getSeason().getId();
	}

	@Override
	public int getStartingDay()
	{
		return this.startingDay;
	}

	@Override
	public CalendarBoundSeason getFollowingSeason()
	{
		List<? extends CalendarBoundSeason> seasons = this.getOwningCalendar().getSeasons();
		int nextIndex = (this.ordinal + 1) % seasons.size();
		return seasons.get(nextIndex);
	}

	@Override
	public CalendarBoundSeason getPrecedingSeason()
	{
		List<? extends CalendarBoundSeason> seasons = this.getOwningCalendar().getSeasons();
		int nextIndex = (this.ordinal - 1) % seasons.size();
		return seasons.get(nextIndex);
	}

	@Override
	public String toString()
	{
		return String.format("SeasonCalendarEntry(%s)", this.getSeason().getId());
	}
}
