package enginecrafter77.survivalinc.season;

import javax.annotation.Nonnull;

public class SeasonCalendarDate implements Comparable<SeasonCalendarDate> {
	private SeasonCalendar.SeasonCalendarEntry season;
	private int day;
	
	public SeasonCalendarDate(@Nonnull SeasonCalendar.SeasonCalendarEntry season, int day)
	{
		this.season = season;
		this.day = day;
	}
	
	public SeasonCalendar.SeasonCalendarEntry getCalendarEntry()
	{
		return this.season;
	}
	
	public int getDay()
	{
		return this.day;
	}
	
	public int getDayInYear()
	{
		return this.season.getStartingDay() + this.getDay();
	}
	
	public void setSeason(SeasonCalendar.SeasonCalendarEntry season)
	{
		this.season = season;
	}
	
	public void setDay(int day)
	{
		this.day = day;
	}
	
	/**
	 * Advances the date information by the
	 * specified number of days. If the method
	 * also happens to cross a boundary of a
	 * season (i.e. not only the day is updated,
	 * but also the season), the returned value
	 * is greater than 0. In fact, the return
	 * value is equal to the times this method
	 * has crossed a season boundary while calculating
	 * the new date.
	 * @param days The days to advance the date by
	 * @return Number of seasons advanced.
	 */
	public int advance(int days)
	{
		this.day += days;
		int traversed = 0;
		while(this.day >= this.getCalendarEntry().getSeason().getLength() || this.day < 0)
		{
			int length = this.getCalendarEntry().getSeason().getLength();
			int way = Integer.compare(this.day, length - 1); // Length - 1 to avoid 0
			
			this.day -= way * length;
			this.season = this.season.getFollowing(way);
			traversed++;
		}
		return traversed;
	}
	
	@Override
	public SeasonCalendarDate clone()
	{
		return new SeasonCalendarDate(this.season, this.day);
	}

	@Override
	public String toString()
	{
		return String.format("%s(%s/%d|%d)", this.getClass().getSimpleName(), this.getCalendarEntry().getIdentifier().toString(), this.getDay(), this.getDayInYear());
	}
	
	@Override
	public int compareTo(SeasonCalendarDate other)
	{
		int local = this.getDayInYear();
		int target = other.getDayInYear();
		
		if(local == target) return 0;
		else return local > target ? 1 : -1;
	}
}