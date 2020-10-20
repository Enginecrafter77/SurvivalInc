package enginecrafter77.survivalinc.season;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public class SeasonChangedEvent extends WorldEvent {
	public final SeasonData data;
	
	public SeasonChangedEvent(World world, SeasonData data)
	{
		super(world);
		this.data = data;
	}
	
	public SeasonChangedEvent(World world)
	{
		this(world, SeasonData.load(world));
	}
	
	/**
	 * @return True if the season was changed, or false if only the day has been advanced. 
	 */
	public boolean hasSeasonAdvanced()
	{
		return data.day == 0;
	}
	
	public Season getSeason()
	{
		return data.season;
	}
	
	public int getDay()
	{
		return data.day;
	}
}
