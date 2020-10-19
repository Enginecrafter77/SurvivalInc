package enginecrafter77.survivalinc.season;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class SeasonData extends WorldSavedData {
	
	public static final String datakey = SurvivalInc.MOD_ID + "_season";
	
	/** The current season */
	public Season season;
	
	/** Days elapsed in the current season */
	public int day;
	
	public SeasonData()
	{
		this(SeasonData.datakey);
	}
	
	public SeasonData(String id)
	{
		super(id);
		/*
		 * Values set here are the default values.
		 * These are either overwritten by #readFromNBT
		 * or in case there is no NBT data present (e.g
		 * new world is created or the mod is installed
		 * for the first time in a world), then these
		 * data persist. Essentially, these values indicate
		 * the season and day the player starts in.
		 */
		this.season = ModConfig.SEASONS.startingSeason;
		this.day = ModConfig.SEASONS.startingDay;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		int ordinal = nbt.getInteger("season");
		this.season = Season.values()[ordinal];
		this.day = nbt.getInteger("day");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound.setInteger("season", this.season.ordinal());
		compound.setInteger("day", this.day);
		return compound;
	}
	
	@Override
	public String toString()
	{
		return String.format("%s (Day %d)", season.name(), day);
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
		int seasons_advanced = 0;
		
		while((days + this.day) >= this.season.getLength())
		{
			days -= this.season.getLength() - this.day;
			this.season = this.season.getFollowing(1);
			seasons_advanced++;
			this.day = 0;
		}
		this.day += days;
		
		return seasons_advanced;
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
