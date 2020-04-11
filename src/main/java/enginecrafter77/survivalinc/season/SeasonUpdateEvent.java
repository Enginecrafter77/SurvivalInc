package enginecrafter77.survivalinc.season;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public class SeasonUpdateEvent extends WorldEvent {

	public final SeasonData data;
	
	public SeasonUpdateEvent(World world, SeasonData data)
	{
		super(world);
		this.data = data;
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
