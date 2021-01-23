package enginecrafter77.survivalinc.season;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class SeasonCalendarDate implements INBTSerializable<NBTTagCompound>, Comparable<SeasonCalendarDate> {
	public final SeasonCalendar calendar;
	
	private int season;
	private int day;
	
	public SeasonCalendarDate(SeasonCalendar calendar, int season, int day)
	{
		this.calendar = calendar;
		this.season = season;
		this.day = day;
	}
	
	public SeasonProvider getSeason()
	{
		return this.calendar.valueOf(season);
	}
	
	public int getDay()
	{
		return this.day;
	}
	
	public int getDayInYear()
	{
		int day = this.day;
		for(SeasonProvider season : this.calendar.getPreceding(this.season))
			day += season.getLength();
		return day;
	}
	
	public void setSeason(SeasonProvider season)
	{
		this.season = this.calendar.indexOf(season);
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
		int traversed = 0;
		
		this.day += days;
		
		SeasonProvider current = this.getSeason();
		while(this.day >= current.getLength());
		{
			this.day -= current.getLength();
			this.season = (this.season + 1) % this.calendar.getSeasonCount();
			current = this.getSeason();
			traversed++;
		}
		return traversed;
	}
	
	public SeasonProvider getNextSeason(int steps)
	{
		int season = this.season + steps;
		return this.calendar.valueOf(season % this.calendar.getSeasonCount());
	}
	
	@Override
	public SeasonCalendarDate clone()
	{
		return new SeasonCalendarDate(this.calendar, this.season, this.day);
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("season", this.season);
		tag.setInteger("day", this.day);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		this.season = nbt.getInteger("season");
		this.day = nbt.getInteger("day");
	}

	@Override
	public String toString()
	{
		return String.format("%s(%s/%d)", this.getClass().getSimpleName(), this.getSeason().getLocalizedName(), this.day);
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