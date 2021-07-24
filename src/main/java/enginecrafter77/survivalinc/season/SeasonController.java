package enginecrafter77.survivalinc.season;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeEnd;
import net.minecraft.world.biome.BiomeHell;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Predicate;

import enginecrafter77.survivalinc.SurvivalInc;

public class SeasonController {
	
	private static final Predicate<Integer> isDimOverworld = (Integer dimID) -> dimID == DimensionType.OVERWORLD.getId();
	private static final Predicate<World> isOverworld = (World world) -> isDimOverworld.test(world.provider.getDimension());
	
	/** The length of one full minecraft day/night cycle */
	public static final int minecraftDayLength = 24000;
	
	/** The season controller singleton */
	public static final SeasonController instance = new SeasonController();
	
	/** The biome temperature controller instance */
	public BiomeTempController biomeTemp;
	
	public SeasonCalendar calendar;
	
	private SeasonController()
	{
		try
		{
			this.calendar = new SeasonCalendar();
			this.biomeTemp = new BiomeTempController();
			this.biomeTemp.excluded.add(BiomeOcean.class);
			this.biomeTemp.excluded.add(BiomeHell.class);
			this.biomeTemp.excluded.add(BiomeEnd.class);
		}
		catch(NoSuchFieldException exc)
		{
			RuntimeException nexc = new RuntimeException();
			nexc.initCause(exc);
			throw nexc;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static IMessage onSyncDelivered(SeasonSyncMessage message, MessageContext ctx)
	{
		WorldClient world = Minecraft.getMinecraft().world;
		if(world == null) SurvivalInc.logger.error("Minecraft remote world tracking instance is null. This is NOT a good thing. Season sync packet dropped...");
		else if(SeasonController.isOverworld.negate().test(world)) throw new RuntimeException("Received a stat update message from outside overworld.");
		else
		{
			SurvivalInc.logger.info("Updating client's world season data to {}...", message.data.toString());
			world.setData(SeasonData.datakey, message.data);
			MinecraftForge.EVENT_BUS.post(new SeasonChangedEvent(world, message.data.getCurrentDate()));
			SurvivalInc.logger.info("Client-side season update to {} completed.", message.data.toString());
		}
		return null;
	}
	
	public static SeasonSyncMessage onSyncRequest(SeasonSyncRequest message, MessageContext ctx)
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
		if(ent instanceof EntityPlayer && world.isRemote && isOverworld.test(world))
		{
			SurvivalInc.logger.info("Requesting season data from server...");
			SurvivalInc.proxy.net.sendToServer(new SeasonSyncRequest());
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
		if(!world.isRemote && SeasonController.isOverworld.test(world)) MinecraftForge.EVENT_BUS.post(new SeasonChangedEvent(world));
	}
	
	/**
	 * Updates the season data on server. Unfortunately,
	 * this method won't work for clients.
	 * @param event The world tick event
	 */
	@SubscribeEvent
	public void onUpdate(TickEvent.WorldTickEvent event)
	{
		if(event.side == Side.SERVER && event.phase == TickEvent.Phase.START && SeasonController.isOverworld.test(event.world))
		{
			// Is it early morning? Also, before the player really joins, some time passes. Give the player some time to actually receive the update.
			if(event.world.getWorldTime() % SeasonController.minecraftDayLength == 1200)
			{
				SurvivalInc.logger.info("Season update triggered on {}", event.side.name());
				SeasonData data = SeasonData.load(event.world);
				data.getCurrentDate().advance(1);
				data.markDirty();
				
				SurvivalInc.proxy.net.sendToDimension(new SeasonSyncMessage(data), DimensionType.OVERWORLD.getId()); // Synchronize the data with clients, so they begin processing on their side
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
		event.date.getCalendarEntry().getSeason().applySeason(event.getWorld(), event.date.getDay());
		this.biomeTemp.applySeason(event.date);
		SurvivalInc.logger.info("Applied biome temperatures for season {}", event.date.toString());
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