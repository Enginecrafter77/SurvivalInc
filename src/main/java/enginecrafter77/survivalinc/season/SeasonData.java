package enginecrafter77.survivalinc.season;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

//TODO reimplement
public class SeasonData extends WorldSavedData {
	
	// Stuff to save
	// Season data
	// 1 = winter, 2 = spring, 3 = summer, 4 = autumn
	public int season = 0; //TODO store using emums
	public int daysIntoSeason = 0;

	// Put anymore data here whenever necessary

	// Constructors
	public SeasonData()
	{
		this(SurvivalInc.MOD_ID);
	}

	public SeasonData(String id)
	{
		super(id);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		daysIntoSeason = nbt.getInteger("daysIntoSeason");
		season = nbt.getInteger("season");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		compound.setInteger("season", season);
		compound.setInteger("daysIntoSeason", daysIntoSeason);
		
		return compound;
	}

	// Easy loading (Do SchopWorldData data = SchopWorldData.load(world);)
	public static SeasonData load(World world)
	{
		SeasonData data = (SeasonData) world.getMapStorage().getOrLoadData(SeasonData.class, SurvivalInc.MOD_ID);

		// Does it not exist?
		if (data == null)
		{
			SurvivalInc.logger.warn("No world data found for survivalinc. Creating new file.");

			data = new SeasonData();

			// Predetermine some values if necessary

			// Seasons
			// Determine starting season and daysIntoSeason
			double springOrFall = Math.random();

			if (springOrFall < 0.50)
			{

				data.season = 2;
			}

			else
			{

				data.season = 4;
			}

			if (data.season == 2)
			{

				data.daysIntoSeason = (ModConfig.SEASONS.springLength / 2) - 1;
			}

			else
			{

				data.daysIntoSeason = 0;
			}

			data.markDirty();
			world.getMapStorage().setData(SurvivalInc.MOD_ID, data);
		}

		return data;
	}
	
	public static int seasonToInt(Season season)
	{
		return season.ordinal() + 1;
	}

	public static Season intToSeason(int seasonInt)
	{
		return Season.values()[seasonInt - 1];
	}

	public Season getSeasonFromData()
	{
		return intToSeason(this.season);
	}
}
