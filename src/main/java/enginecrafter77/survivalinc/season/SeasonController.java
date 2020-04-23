package enginecrafter77.survivalinc.season;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeEnd;
import net.minecraft.world.biome.BiomeHell;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import enginecrafter77.survivalinc.SurvivalInc;

public class SeasonController implements IMessageHandler<SeasonUpdateEvent, IMessage> {
	
	public static final int minecraftDayLength = 24000;
	
	public static BiomeTempController biomeTemp;
	
	// Process the event and broadcast it on client
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(SeasonUpdateEvent message, MessageContext ctx)
	{
		World world = message.getWorld();
		if(world == null) SurvivalInc.logger.error("Minecraft remote world tracking instance is null. This is NOT a good thing. Skipping season update...");
		else
		{
			world.setData(SeasonData.datakey, message.data);
			MinecraftForge.EVENT_BUS.post(message);
			SurvivalInc.logger.info("Updated client's world season data to {}", message.toString());
		}
		return null;
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void biomeGrass(BiomeEvent.GetGrassColor event)
	{
		// Yes I know what I am doing when using MC.getMC()
		WorldClient world = Minecraft.getMinecraft().world;
		SeasonData data = SeasonData.load(world);
		
		if(data.season.grasscolor != 0)
			event.setNewColor(data.season.grasscolor);
	}
	
	@SubscribeEvent
	public static void onPlayerLogsIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if(!event.player.world.isRemote)
		{
			SeasonUpdateEvent update = new SeasonUpdateEvent(event.player.world);
			SurvivalInc.logger.info("Synchronizing client's ({}) season data: {}", event.player.getDisplayNameString(), update.data.toString());
			SurvivalInc.proxy.net.sendTo(update, (EntityPlayerMP)event.player);
		}
	}
	
	// Apply the season data on world load
	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event)
	{
		try
		{
			SeasonController.biomeTemp = new BiomeTempController();
			SeasonController.biomeTemp.excluded.add(BiomeOcean.class);
			SeasonController.biomeTemp.excluded.add(BiomeHell.class);
			SeasonController.biomeTemp.excluded.add(BiomeEnd.class);
		}
		catch(NoSuchFieldException exc)
		{
			RuntimeException nexc = new RuntimeException();
			nexc.initCause(exc); //ReportedException
			throw nexc;
		}
		
		SeasonData data = SeasonData.load(event.getWorld());
		MinecraftForge.EVENT_BUS.post(new SeasonUpdateEvent(event.getWorld(), data));
	}
	
	// Used to fire SeasonUpdateEvent when 
	@SubscribeEvent
	public static void onUpdate(TickEvent.WorldTickEvent event)
	{
		DimensionType dimension = event.world.provider.getDimensionType();
		if(!event.world.isRemote && event.phase == TickEvent.Phase.START && dimension == DimensionType.OVERWORLD)
		{
			// Is it early morning? Also, before the player really joins, some time passes. Give the player some time to actually receive the update.
			if(event.world.getWorldTime() % SeasonController.minecraftDayLength == 200)
			{
				SurvivalInc.logger.info("Day advance. Event: Side: {}, Phase: {}, Type: {}, Dim: {}", event.side.name(), event.phase.name(), event.type.name(), dimension.name());
				SeasonData data = SeasonData.load(event.world);
				data.update(event.world);
				
				MinecraftForge.EVENT_BUS.post(new SeasonUpdateEvent(event.world, data)); // Broadcast the event on server side
				
				data.markDirty();
			}
		}
	}
	
	// Capture the season update event and share it with clients
	@SubscribeEvent
	public static void relayToClients(SeasonUpdateEvent event)
	{		
		if(!event.getWorld().isRemote)
		{
			SurvivalInc.logger.info("Broadcasting season data to clients");
			SurvivalInc.proxy.net.sendToAll(event); // Send new event to client side
		}
	}
	
	// Capture the season update event and apply biome temperatures
	@SubscribeEvent
	public static void applySeasonData(SeasonUpdateEvent event)
	{
		WorldInfo info = event.getWorld().getWorldInfo();
		biomeTemp.applySeason(event.data);
		
		// Determine the weather. The season is the main factor.
		float randWeather = (float) Math.random();
		if(randWeather < event.getSeason().rainfallchance && info.isRaining())
			info.setRaining(true);
		
		SurvivalInc.logger.info("Season update ({}) applied.", event.data.toString());
	}

	// Add bonus harvest drops from crops in the Autumn.
	@SubscribeEvent
	public static void onCropHarvest(BlockEvent.HarvestDropsEvent event)
	{
		World world = event.getWorld();
		IBlockState state = event.getState();
		
		// Is (was?) the harvested block really a crop?
		if(state.getBlock() instanceof BlockCrops && !world.isRemote)
		{
			SeasonData data = SeasonData.load(event.getWorld());
			BlockCrops crop = (BlockCrops)state.getBlock();
			List<ItemStack> drops = event.getDrops();
			if(!crop.isMaxAge(event.getState())) drops.clear();
			else if(data.season == Season.AUTUMN)
			{
				for(ItemStack drop : drops) // Double the outcome
					drop.grow(drop.getCount());
			}
		}
	}

	// Make nothing grow in winter, and more grow in summer
	@SubscribeEvent
	public static void affectGrowth(BlockEvent.CropGrowEvent.Pre event)
	{		
		World world = event.getWorld();
		if(world.isRemote) return; // We don't want to check client side
		
		SeasonData data = SeasonData.load(world);
		Event.Result result = Event.Result.DEFAULT;
		if(data.season == Season.WINTER)
			result = Event.Result.DENY;
		event.setResult(result);
	}
}