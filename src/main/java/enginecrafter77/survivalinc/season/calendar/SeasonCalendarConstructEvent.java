package enginecrafter77.survivalinc.season.calendar;

import enginecrafter77.survivalinc.season.AbstractSeason;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;

public class SeasonCalendarConstructEvent extends Event {
	private final SeasonCalendarBuilder builder;

	private SeasonCalendarFactory<?> factory;

	public SeasonCalendarConstructEvent(SeasonCalendarFactory<?> defaultFactory)
	{
		this.builder = new SeasonCalendarBuilder();
		this.factory = defaultFactory;
	}

	public void registerSeason(AbstractSeason season)
	{
		this.builder.register(season);
	}

	public void registerSeasons(List<AbstractSeason> seasons)
	{
		seasons.forEach(this::registerSeason);
	}

	public void setFactory(SeasonCalendarFactory<?> factory)
	{
		this.factory = factory;
	}

	public SeasonCalendar buildCalendar()
	{
		return this.builder.build(this.factory);
	}

	@Override
	public boolean isCancelable()
	{
		return false;
	}

	@Override
	public boolean hasResult()
	{
		return false;
	}
}
