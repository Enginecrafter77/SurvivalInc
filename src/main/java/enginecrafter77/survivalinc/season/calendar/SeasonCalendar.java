package enginecrafter77.survivalinc.season.calendar;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public interface SeasonCalendar {
	public List<? extends CalendarBoundSeason> getSeasons();

	@Nullable
	public CalendarBoundSeason findSeason(ResourceLocation id);

	public int getYearLengthDays();

	public CalendarBoundSeason getSeasonDuring(int day);

	public int calendarHash();
}
