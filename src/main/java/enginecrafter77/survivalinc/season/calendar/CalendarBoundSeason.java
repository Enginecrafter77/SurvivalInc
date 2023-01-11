package enginecrafter77.survivalinc.season.calendar;

import enginecrafter77.survivalinc.season.AbstractSeason;
import net.minecraft.util.ResourceLocation;

public interface CalendarBoundSeason {
	public SeasonCalendar getOwningCalendar();

	public AbstractSeason getSeason();

	public ResourceLocation getIdentifier();

	public int getStartingDay();

	public CalendarBoundSeason getPrecedingSeason();

	public CalendarBoundSeason getFollowingSeason();
}
