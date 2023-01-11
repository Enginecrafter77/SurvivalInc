package enginecrafter77.survivalinc.season.calendar;

public abstract class AbstractSeasonCalendarDate implements SeasonCalendarDate {
	@Override
	public int compareTo(SeasonCalendarDate other)
	{
		int local = this.getDayInYear();
		int target = other.getDayInYear();
		return Integer.compare(local, target);
	}

	@Override
	public String toString()
	{
		return String.format("%s|%d/%d", this.getCalendarBoundSeason().getIdentifier(), this.getDay(), this.getDayInYear());
	}
}
