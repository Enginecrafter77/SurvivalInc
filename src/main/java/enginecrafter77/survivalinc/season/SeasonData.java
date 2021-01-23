package enginecrafter77.survivalinc.season;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class SeasonData extends WorldSavedData {
	
	public static final String datakey = SurvivalInc.MOD_ID + "_season";
	
	/** The current season */
	private final SeasonCalendarDate date;
	
	public SeasonData()
	{
		this(SeasonData.datakey);
	}
	
	public SeasonData(String id)
	{
		super(id);
		this.date = new SeasonCalendarDate(SeasonController.instance.calendar, 0, 0);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		this.date.deserializeNBT(nbt);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		return this.date.serializeNBT();
	}
	
	@Override
	public String toString()
	{
		return this.date.toString();
	}
	
	public SeasonCalendarDate getCurrentDate()
	{
		return this.date;
	}

	/**
	 * A simple season data loading wrapper.
	 * This wrapper takes care of loading
	 * the seasonal data, and if none are
	 * present, creates new instance with
	 * default values.
	 * @param world The world to get the data from
	 * @return Seasonal data present in the world
	 */
	public static SeasonData load(World world)
	{
		MapStorage storage = world.getMapStorage();
		SeasonData data = (SeasonData)storage.getOrLoadData(SeasonData.class, SeasonData.datakey);
		if(data == null)
		{
			SurvivalInc.logger.warn("No season data found in world.");
			data = new SeasonData(SeasonData.datakey);
			storage.setData(SeasonData.datakey, data);
		}
		return data;
	}
}
