package enginecrafter77.survivalinc.season;

import com.google.common.collect.ImmutableSet;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.net.MessageHandler;
import enginecrafter77.survivalinc.season.calendar.CalendarBoundSeason;
import enginecrafter77.survivalinc.season.calendar.ImmutableSeasonCalendarDate;
import enginecrafter77.survivalinc.season.calendar.SeasonCalendar;
import enginecrafter77.survivalinc.season.calendar.SeasonCalendarDate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class SeasonController {
	
	private static final Predicate<Integer> P_IS_DIM_OVERWORLD = (Integer dimID) -> dimID == DimensionType.OVERWORLD.getId();
	private static final Predicate<World> P_IS_WORLD_OVERWORLD = (World world) -> P_IS_DIM_OVERWORLD.test(world.provider.getDimension());
	
	/** The length of one full minecraft day/night cycle */
	public static final int DAY_LENGTH_TICKS = 24000;
	
	/** The biome temperature controller instance */
	protected final BiomeTemperatureInjector biomeTemp;
	protected final SeasonCalendar calendar;

	private final Set<Class<? extends Biome>> excludedBiomes;

	public SeasonController(SeasonCalendar calendar, BiomeTemperatureInjector biomeTemp, @Nullable Set<Class<? extends Biome>> excludedBiomes)
	{
		this.excludedBiomes = Optional.ofNullable(excludedBiomes).orElse(ImmutableSet.of());
		this.calendar = calendar;
		this.biomeTemp = biomeTemp;
	}

	public SeasonController(SeasonCalendar calendar, BiomeTemperatureInjector biomeTemp)
	{
		this(calendar, biomeTemp, null);
	}

	public Set<Class<? extends Biome>> getExcludedBiomes()
	{
		return this.excludedBiomes;
	}

	public BiomeTemperatureInjector getBiomeTemperatureInjector()
	{
		return this.biomeTemp;
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	@MessageHandler(messageType = SeasonSyncMessage.class)
	public IMessage onSyncDelivered(SeasonSyncMessage message, MessageContext ctx)
	{
		WorldClient world = Minecraft.getMinecraft().world;
		if(world == null)
		{
			SurvivalInc.logger.error("Minecraft remote world tracking instance is null. This is NOT a good thing. Season sync packet dropped...");
		}
		else if(SeasonController.P_IS_WORLD_OVERWORLD.negate().test(world))
		{
			throw new RuntimeException("Received a stat update message from outside overworld.");
		}
		else
		{
			SurvivalInc.logger.info("Updating client's world season data to {}...", message.data.toString());
			world.setData(SeasonData.WSD_KEY, message.data);
			MinecraftForge.EVENT_BUS.post(new SeasonChangedEvent(world, message.data.getCurrentDate()));
			SurvivalInc.logger.info("Client-side season update to {} completed.", message.data.toString());
		}
		return null;
	}

	@MessageHandler(messageType = SeasonSyncRequest.class)
	public SeasonSyncMessage onSyncRequest(SeasonSyncRequest message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;
		SeasonData data = SeasonData.load(player.world);
		SurvivalInc.logger.info("Sending season data ({}) to player \"{}\" entering overworld.", data.toString(), player.getDisplayNameString());
		return new SeasonSyncMessage(data);
	}
	
	/**
	 * Sends the season data to new players in overworld
	 * @param event The player logged in event
	 */
	@SubscribeEvent
	public void onPlayerJoined(EntityJoinWorldEvent event)
	{
		World world = event.getWorld();
		Entity ent = event.getEntity();
		if(ent instanceof EntityPlayer && world.isRemote && P_IS_WORLD_OVERWORLD.test(world))
		{
			SurvivalInc.logger.info("Requesting season data from server...");
			SurvivalInc.net.sendToServer(new SeasonSyncRequest());
		}
	}
	
	/**
	 * Load the season data from disk on server side.
	 * @param event The world load event
	 */
	@SubscribeEvent
	public void loadSeasonData(WorldEvent.Load event)
	{
		World world = event.getWorld();
		if(!world.isRemote && SeasonController.P_IS_WORLD_OVERWORLD.test(world)) MinecraftForge.EVENT_BUS.post(new SeasonChangedEvent(world));
	}
	
	/**
	 * Updates the season data on server. Unfortunately,
	 * this method won't work for clients.
	 * @param event The world tick event
	 */
	@SubscribeEvent
	public void onUpdate(TickEvent.WorldTickEvent event)
	{
		if(event.side == Side.SERVER && event.phase == TickEvent.Phase.START && SeasonController.P_IS_WORLD_OVERWORLD.test(event.world))
		{
			// Is it early morning? Also, before the player really joins, some time passes. Give the player some time to actually receive the update.
			if(event.world.getWorldTime() % SeasonController.DAY_LENGTH_TICKS == 1200)
			{
				SurvivalInc.logger.info("Season update triggered on {}", event.side.name());
				SeasonData data = SeasonData.load(event.world);
				data.getCurrentDate().advance(1);
				data.markDirty();
				
				SurvivalInc.net.sendToDimension(new SeasonSyncMessage(data), DimensionType.OVERWORLD.getId()); // Synchronize the data with clients, so they begin processing on their side
				MinecraftForge.EVENT_BUS.post(new SeasonChangedEvent(event.world, data.getCurrentDate())); // Broadcast and process the event on server side
			}
		}
	}
	
	/**
	 * Capture the season update event and apply biome temperatures
	 * @param event The season changed event
	 */
	@SubscribeEvent
	public void applySeasonData(SeasonChangedEvent event)
	{
		event.date.getCalendarBoundSeason().getSeason().applySeason(event.getWorld(), event.date.getDay());
		this.updateBiomeTemperaturesOn(event.date);
		SurvivalInc.logger.info("Applied biome temperatures for season {}", event.date.toString());
	}

	/**
	 * A method used to calculate a new base temperature
	 * for the given biome type. Override this method
	 * if you want to use a different type of calculation.
	 * @param biome The biome to calculate new base temperature for
	 * @param date The current season date
	 * @param offset The calculated standard universal temperature offset
	 * @return A new base temperature for the provided biome
	 */
	public float calculateNewBiomeTemperature(Biome biome, SeasonCalendarDate date, float offset)
	{
		return this.biomeTemp.getOriginalBiomeTemperature(biome) + offset;
	}

	/**
	 * This actually changes the biome temperatures every morning... Using reflection!
	 * Yeah, it's very hacky, hackish, whatever you want to call it, but it works!
	 * @param date The date to apply
	 */
	public void updateBiomeTemperaturesOn(SeasonCalendarDate date)
	{
		long time = System.currentTimeMillis();
		int processed = 0;

		float offset = this.getSeasonalTemperatureOffset(date);
		for(Biome biome : ForgeRegistries.BIOMES.getValuesCollection())
		{
			if(this.getExcludedBiomes().contains(biome.getClass()))
				continue;

			this.biomeTemp.setAbsoluteBiomeTemperature(biome, this.calculateNewBiomeTemperature(biome, date, offset));
			processed++;
		}

		time = System.currentTimeMillis() - time;
		SurvivalInc.logger.info("Processed {} biomes in {} ms", processed, time);
	}

	/**
	 * Gets the peak point for the specified calendar season.
	 * @param season The season to get the peak for.
	 * @return The date the temperature reaches its extreme in the specified season.
	 */
	private SeasonCalendarDate getPeak(CalendarBoundSeason season)
	{
		return new ImmutableSeasonCalendarDate(season, season.getSeason().getPeakTemperatureDay());
	}

	/**
	 * Returns the nearest peak point to the specified date.
	 * @param date The date to find the nearest peak to
	 * @return The nearest peak point in the specified direction.
	 */
	private SeasonCalendarDate findNextPeak(SeasonCalendarDate date)
	{
		CalendarBoundSeason season = date.getCalendarBoundSeason();
		SeasonCalendarDate peak = this.getPeak(season);

		// Test if date is already past the local peak. If so, advance the season in the specified way
		if(date.compareTo(peak) > 0)
			peak = this.getPeak(season.getFollowingSeason());
		return peak;
	}

	private SeasonCalendarDate findPreviousPeak(SeasonCalendarDate date)
	{
		CalendarBoundSeason season = date.getCalendarBoundSeason();
		SeasonCalendarDate peak = this.getPeak(season);

		// Test if date is already past the local peak. If so, advance the season in the specified way
		if(date.compareTo(peak) < 0)
			peak = this.getPeak(season.getPrecedingSeason());
		return peak;
	}

	/**
	 * Calculates uniform temperature offset on the specified date in calendar year.
	 * Generally, this offset is applied to every biome's base temperature except
	 * ones defined in {@link #getExcludedBiomes()}.
	 * @param date The current date in calendar year
	 * @return The current temperature offset.
	 */
	public float getSeasonalTemperatureOffset(SeasonCalendarDate date)
	{
		CalendarBoundSeason season = date.getCalendarBoundSeason();
		// If the date is exactly on the peak date
		if(this.getPeak(season).compareTo(date) == 0)
			return season.getSeason().getPeakTemperature();

		// The absolute day of half of this season (where temperature has achieved the last peak)
		SeasonCalendarDate previous_peak = this.findPreviousPeak(date);

		// The absolute day of half of the target season (where temperature is supposed to achieve the specified value)
		SeasonCalendarDate next_peak = this.findNextPeak(date);

		int year = season.getOwningCalendar().getYearLengthDays();

		// The current absolute day of year
		int a_day = date.getDayInYear();
		int pp_day = previous_peak.getDayInYear();
		int np_day = next_peak.getDayInYear();

		// Store the temperatures inside variables for easier access
		float pp_temp = previous_peak.getCalendarBoundSeason().getSeason().getPeakTemperature();
		float np_temp = next_peak.getCalendarBoundSeason().getSeason().getPeakTemperature();

		/*
		 * We always assume that the target date is in the direction
		 * specified by "way" ahead of local. If this is not the case,
		 * we are most probably flipping around the year boundary. If
		 * so, we need to adjust the date accordingly so that the interpolation
		 * works reliably.
		 *
		 * When we are wrapping positively across the
		 * year edge (way = 1), then the target absolute
		 * day will be lower than the local absolute.
		 * This assures that for current calculations,
		 * the absolute day is shifted by the whole year
		 * length. Imagine it as if the season is logically
		 * following this (like in perpetual loop), but in
		 * fact it gets wrapped around.
		 * Consider this example:
		 * 				|			#
		 * -----------------------------------------
		 * 	1	2	3	4	5	6	7	8	[9]	[10]
		 *
		 * In this example, we are currently at day 7,
		 * which is beyond the temperature amplitude day(4).
		 * We are reaching the end of the year. So the absolute
		 * days 1 and 2 are re-mapped to absolute days 9 and 10
		 * respectively. Likewise, this happens at the very
		 * beginning of new year.
		 * 		#		|
		 * -------------------------------
		 * 	1	2	3	4	5	6	7	8
		 *
		 * Here, the values lesser than 4 are subtracted by
		 * the length of the year. Why? Because it puts
		 * the numbers behind 0, which signifies their meaning
		 * that they are beyond the edge of the year, and are
		 * as far away from the edge as the original number.
		 * It may be hard to imagine, but it works perfectly.
		 */
		if(np_day < pp_day) np_day += year;
		if(a_day < pp_day) a_day += year;

		float value = SeasonController.interpolateTemperature(a_day, pp_day, np_day, pp_temp, np_temp);
		SurvivalInc.logger.info("STI {}@{} --[{}]--> {}@{} => {}", previous_peak.toString(), pp_temp, date.toString(), next_peak.toString(), np_temp, value);
		return value;
	}

	/**
	 * <p>Calculates the current temperature offset by utilizing
	 * simple linear interpolation method.
	 * </p><p>
	 * Mathematically, this function is a simple linear
	 * equation that, when plotted, creates a nice slope
	 * crossing points A[d0,a] and B[d1,b]. In fact, the
	 * line connecting these points in Cartesian plane
	 * is a part of the function used by this method.
	 * </p><p>
	 * So how it works? The call to this method is equivalent to
	 * <pre>
	 *  		   x - d0
	 * 	y = (b-a)--------- + a
	 *  		  d1 - d0
	 * </pre>
	 * </p><p>
	 * This method basically computes the difference between the temperatures,
	 * and applies it to the fraction of the days elapsed from the total days.
	 * Finally, the temperature correction equal to <i>a</i> is applied.</p>
	 * @param x The current day
	 * @param d0 The day of point A
	 * @param d1 The day of point B
	 * @param a The temperature of point A
	 * @param b The temperature of point B
	 * @return The value linearly scaled between points A and B
	 */
	public static float interpolateTemperature(float x, float d0, float d1, float a, float b)
	{
		return (b - a) * ((x - d0) / (d1 - d0)) + a;
	}
	
	/*
	 * Determines the color of the grass in the said biome
	 * @param event The grass color event
	 *
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void biomeGrass(BiomeEvent.GetGrassColor event)
	{
		Range<Float> temperature_range = this.calendar.getTemperatureRange();
		float deftemp = event.getBiome().getDefaultTemperature();
		
		// If the current biome is permafrost
		if((deftemp + temperature_range.upperEndpoint()) < 0F)
			event.setNewColor(0x5BAE92);
		else if((deftemp + temperature_range.lowerEndpoint()) > 1F)
			event.setNewColor(0xCAE24E);
		
	}*/

	/*
	 * Make nothing grow in winter, and let crops grow faster in summer
	 * @param event
	 *
	@SubscribeEvent
	public void affectGrowth(BlockEvent.CropGrowEvent.Pre event)
	{		
		World world = event.getWorld();
		if(world.isRemote) return; // We don't want to check client side
		
		SeasonData data = SeasonData.load(world);
		Event.Result result = Event.Result.DEFAULT;
		if(!data.getCurrentDate().getCalendarEntry().getSeason().allowCropGrowth())
			result = Event.Result.DENY;
		event.setResult(result);
	}*/
}
