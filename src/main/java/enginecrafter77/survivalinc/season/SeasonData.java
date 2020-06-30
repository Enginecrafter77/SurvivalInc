package enginecrafter77.survivalinc.season;

import enginecrafter77.survivalinc.SurvivalInc;
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
		this.season = Season.SPRING; // Start in spring
		this.day = 0;
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
	
	public void update(World world)
	{
		this.day++;
		
		if(day > season.getLength())
		{
			this.season = season.getFollowing(1);
			this.day = 0;
		}
	}

	// Easy loading (Do SchopWorldData data = SchopWorldData.load(world);)
	public static SeasonData load(World world)
	{
		MapStorage storage = world.getMapStorage();
		SeasonData data = (SeasonData)storage.getOrLoadData(SeasonData.class, SeasonData.datakey);
		// Does it not exist?
		if(data == null)
		{
			SurvivalInc.logger.warn("No season data found in world.");
			data = new SeasonData(SeasonData.datakey);
			storage.setData(SeasonData.datakey, data);
		}
		return data;
	}
}
