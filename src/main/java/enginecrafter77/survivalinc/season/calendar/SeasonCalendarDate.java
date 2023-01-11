package enginecrafter77.survivalinc.season.calendar;

public interface SeasonCalendarDate extends Comparable<SeasonCalendarDate> {
	public abstract CalendarBoundSeason getCalendarBoundSeason();

	public abstract int getDay();

	public default int getDayInYear()
	{
		return this.getCalendarBoundSeason().getStartingDay() + this.getDay();
	}
}
