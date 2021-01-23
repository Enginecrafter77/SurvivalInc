package enginecrafter77.survivalinc.season;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public class SeasonChangedEvent extends WorldEvent {
	public final SeasonCalendarDate date;
	
	public SeasonChangedEvent(World world, SeasonCalendarDate date)
	{
		super(world);
		this.date = date;
	}
	
	public SeasonChangedEvent(World world)
	{
		this(world, SeasonData.load(world).getCurrentDate());
	}
	
	/**
	 * @return True if the season was changed, or false if only the day has been advanced. 
	 */
	public boolean hasSeasonAdvanced()
	{
		return date.getDay() == 0;
	}
}
