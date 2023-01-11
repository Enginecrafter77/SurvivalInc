package enginecrafter77.survivalinc.season.calendar;

import javax.annotation.Nonnull;

public class MutableSeasonCalendarDate extends AbstractSeasonCalendarDate {
	private CalendarBoundSeason season;
	private int day;
	
	public MutableSeasonCalendarDate(@Nonnull CalendarBoundSeason season, int day)
	{
		this.season = season;
		this.day = day;
	}

	public MutableSeasonCalendarDate(SeasonCalendarDate other)
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
	
	public void setSeason(CalendarBoundSeason season)
	{
		this.season = season;
		this.day %= season.getSeason().getLength();
	}
	
	public void setDay(int day)
	{
		if(day >= this.season.getSeason().getLength())
			throw new IndexOutOfBoundsException("Day out of season bounds!");
		this.day = day;
	}
	
	/**
	 * Advances the date information by the specified number of days. If the method
	 * also happens to cross a boundary of a season (i.e. not only the day is updated,
	 * but also the season), the returned value is greater than 0. In fact, the return value is
	 * equal to the times this method has crossed a season boundary while calculating the new date.
	 * @param days The days to advance the date by
	 * @return Number of seasons advanced.
	 */
	public int advance(int days)
	{
		this.day += days;
		int traversed = 0;
		while(this.day >= this.getCalendarBoundSeason().getSeason().getLength() || this.day < 0)
		{
			int length = this.getCalendarBoundSeason().getSeason().getLength();
			int way = Integer.compare(this.day, length - 1); // Length - 1 to avoid 0
			
			this.day -= way * length;
			this.season = way > 0 ? this.getCalendarBoundSeason().getFollowingSeason() : this.getCalendarBoundSeason().getPrecedingSeason();
			traversed++;
		}
		return traversed;
	}

	public ImmutableSeasonCalendarDate toImmutable()
	{
		return new ImmutableSeasonCalendarDate(this);
	}

	public static MutableSeasonCalendarDate shareOrCopy(SeasonCalendarDate date)
	{
		if(date instanceof MutableSeasonCalendarDate)
			return (MutableSeasonCalendarDate)date;
		return new MutableSeasonCalendarDate(date);
	}
}
