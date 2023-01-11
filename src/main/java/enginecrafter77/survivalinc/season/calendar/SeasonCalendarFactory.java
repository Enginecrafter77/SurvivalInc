package enginecrafter77.survivalinc.season.calendar;

import enginecrafter77.survivalinc.season.AbstractSeason;

import java.util.List;

public interface SeasonCalendarFactory<CAL extends SeasonCalendar> {
	public CAL createCalendar(List<AbstractSeason> seasons);
}
