package enginecrafter77.survivalinc.season;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
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
		this.date = new SeasonCalendarDate(SeasonController.instance.calendar.getSeason(new ResourceLocation(ModConfig.SEASONS.startingSeason)), ModConfig.SEASONS.startingDay);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTBase tag = nbt.getTag("season");
		SeasonCalendar.SeasonCalendarEntry season = null;
		if(tag instanceof NBTTagString)
		{
			NBTTagString strtag = (NBTTagString)tag;
			ResourceLocation id = new ResourceLocation(strtag.getString());		
			season = SeasonController.instance.calendar.getSeason(id);
		}
		else if(tag instanceof NBTTagInt)
		{
			// For compatibility reasons
			NBTTagInt inttag = (NBTTagInt)tag;
			season = SeasonController.instance.calendar.getSeasons().get(inttag.getInt());
			SurvivalInc.logger.warn("Legacy season data found! Legacy season ID {} will be mapped to season {}.", inttag.getInt(), season.toString());
		}
		else throw new UnsupportedOperationException("Invalid season data found!");
		
		this.date.setSeason(season);
		this.date.setDay(nbt.getInteger("day"));
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("season", this.date.getCalendarEntry().getIdentifier().toString());
		nbt.setInteger("day", this.date.getDay());
		return nbt;
	}
	
	@Override
	public String toString()
	{
		return String.format("%s/%d", this.date.getCalendarEntry().getSeason().getName(), this.date.getDay());
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
