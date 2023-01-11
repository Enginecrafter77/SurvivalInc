package enginecrafter77.survivalinc.season.calendar;

public class ImmutableSeasonCalendarDate extends AbstractSeasonCalendarDate {
	private final CalendarBoundSeason season;

	private final int day;

	public ImmutableSeasonCalendarDate(CalendarBoundSeason season, int day)
	{
		this.season = season;
		this.day = day;
	}

	public ImmutableSeasonCalendarDate(SeasonCalendarDate other)
	{
		this.season = other.getCalendarBoundSeason();
		this.day = other.getDay();
	}

	@Override
	public CalendarBoundSeason getCalendarBoundSeason()
	{
		return this.season;
	}

	@Override
	public int getDay()
	{
		return this.day;
	}

	public ImmutableSeasonCalendarDate afterDays(int days)
	{
		int day = this.day + days;
		CalendarBoundSeason season = this.season;

		while(this.day >= this.getCalendarBoundSeason().getSeason().getLength() || this.day < 0)
		{
			int length = this.getCalendarBoundSeason().getSeason().getLength();
			int way = Integer.compare(this.day, length - 1); // Length - 1 to avoid 0

			day -= way * length;
			season = way > 0 ? this.getCalendarBoundSeason().getFollowingSeason() : this.getCalendarBoundSeason().getPrecedingSeason();
		}

		return new ImmutableSeasonCalendarDate(season, day);
	}
}
