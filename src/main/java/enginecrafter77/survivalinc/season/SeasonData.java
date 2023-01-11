package enginecrafter77.survivalinc.season;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.season.calendar.CalendarBoundSeason;
import enginecrafter77.survivalinc.season.calendar.ImmutableSeasonCalendarDate;
import enginecrafter77.survivalinc.season.calendar.MutableSeasonCalendarDate;
import enginecrafter77.survivalinc.season.calendar.SeasonCalendarDate;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;

public class SeasonData extends WorldSavedData {
	public static final String WSD_KEY = SurvivalInc.MOD_ID + "_season";

	protected static final String NBT_KEY_SEASON = "season";
	protected static final String NBT_KEY_DAY = "day";
	protected static final String NBT_KEY_HASH = "calendarHash";

	/** The current season */
	private final MutableSeasonCalendarDate date;

	public SeasonData()
	{
		this(SeasonData.WSD_KEY);
	}
	
	public SeasonData(String id)
	{
		super(id);
		this.date = MutableSeasonCalendarDate.shareOrCopy(SeasonData.getSurvivalIncStartingDate());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		int calendarHash = nbt.getInteger(NBT_KEY_HASH);
		if(calendarHash == 0)
		{
			calendarHash = SurvivalInc.seasonCalendar.hashCode();
			SurvivalInc.logger.info("Season calendar hash does not exist. Assuming calendar matches.");
		}

		if(calendarHash != SurvivalInc.seasonCalendar.hashCode())
		{
			SurvivalInc.logger.warn("Season calendar hash code does not match. Using starting date.");
			return;
		}

		NBTBase tag = nbt.getTag(NBT_KEY_SEASON);
		CalendarBoundSeason season;
		if(tag instanceof NBTTagString)
		{
			NBTTagString strtag = (NBTTagString)tag;
			ResourceLocation id = new ResourceLocation(strtag.getString());
			season = SurvivalInc.seasonCalendar.findSeason(id);

			if(season == null)
			{
				season = SeasonData.getSurvivalIncStartingSeason();
				SurvivalInc.logger.warn("Season {} not found in calendar. Using default season {}.", id, season.getSeason().getId());
			}
		}
		else if(tag instanceof NBTTagInt)
		{
			// For compatibility reasons
			NBTTagInt inttag = (NBTTagInt)tag;
			season = SurvivalInc.seasonCalendar.getSeasons().get(inttag.getInt());
			SurvivalInc.logger.warn("Legacy season data found! Legacy season ID {} will be mapped to season {}.", inttag.getInt(), season.toString());
		}
		else
		{
			throw new UnsupportedOperationException("Invalid season data found!");
		}

		this.date.setSeason(season);
		this.date.setDay(nbt.getInteger(NBT_KEY_DAY));
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString(NBT_KEY_SEASON, this.date.getCalendarBoundSeason().getIdentifier().toString());
		nbt.setInteger(NBT_KEY_DAY, this.date.getDay());
		nbt.setInteger(NBT_KEY_HASH, SurvivalInc.seasonCalendar.hashCode());
		return nbt;
	}
	
	@Override
	public String toString()
	{
		return String.format("%s(%s)", this.getClass().getSimpleName(), this.getCurrentDate());
	}
	
	public MutableSeasonCalendarDate getCurrentDate()
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
	@Nonnull
	public static SeasonData load(World world)
	{
		MapStorage storage = world.getMapStorage();
		if(storage == null)
		{
			SurvivalInc.logger.error("Map storage is null. Using default season data.");
			return new SeasonData();
		}

		SeasonData data = (SeasonData)storage.getOrLoadData(SeasonData.class, SeasonData.WSD_KEY);
		if(data == null)
		{
			SurvivalInc.logger.warn("No season data found in world.");
			data = new SeasonData(SeasonData.WSD_KEY);
			storage.setData(SeasonData.WSD_KEY, data);
		}
		return data;
	}

	@Nonnull
	public static CalendarBoundSeason getSurvivalIncStartingSeason()
	{
		CalendarBoundSeason season = SurvivalInc.seasonCalendar.findSeason(new ResourceLocation(ModConfig.SEASONS.startingSeason));
		if(season == null)
			season = SurvivalInc.seasonCalendar.getSeasons().stream().findFirst().orElseThrow(NoSuchElementException::new);
		return season;
	}

	public static SeasonCalendarDate getSurvivalIncStartingDate()
	{
		return new ImmutableSeasonCalendarDate(SeasonData.getSurvivalIncStartingSeason(), ModConfig.SEASONS.startingDay);
	}
}
