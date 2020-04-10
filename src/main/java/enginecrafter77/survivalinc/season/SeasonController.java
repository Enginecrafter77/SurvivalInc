package enginecrafter77.survivalinc.season;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;

/**
 * The main class that controls the seasons and the universe. Alright, I
 * exaggerated on the universe part. This does not affect temperature.
 * That's another file.
 */
@Mod.EventBusSubscriber
public class SeasonController implements IMessageHandler<SeasonData, IMessage> {
	
	public static final int minecraftDayLength = 24000;
	
	private static final CycleController cycleController = new CycleController();
	private static final BiomeTempController biomeTemp = new BiomeTempController();
	private static final SnowMelter melter = new SnowMelter();
	
	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(SeasonData message, MessageContext ctx)
	{
		// I hope I know what I am doing
		Minecraft.getMinecraft().world.setData(SeasonData.datakey, message);
		System.out.println("Received season data [" + message.toString() + "] from server");
		return null;
	}
	
	@SubscribeEvent
	public static void onPlayerLogsIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if(!event.player.world.isRemote)
		{
			SeasonData data = SeasonData.load(event.player.world);
			SurvivalInc.proxy.net.sendTo(data, (EntityPlayerMP)event.player);
			System.out.println("Sending season data [" + data.toString() + "] to " + event.player.getDisplayNameString());
			cycleController.applySeason(data.season);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase ent = event.getEntityLiving();
		if(ent instanceof EntityPlayer && !ent.world.isRemote)
		{
			EntityPlayer player = (EntityPlayer)ent;
			SeasonData data = SeasonData.load(player.world);
			WorldInfo info = player.world.getWorldInfo();
			
			long worldTime = player.world.getWorldTime();
			
			// Is it early morning? It's not exactly 0 because of beds. And it's an odd number because CycleController.
			if(worldTime % SeasonController.minecraftDayLength == 41)
			{
				data.update(player.world);
				data.markDirty();
				
				// Send new season data to client
				SurvivalInc.proxy.net.sendTo(data, (EntityPlayerMP)player);
				biomeTemp.changeBiomeTemperatures(data.season, data.day);

				// Determine the weather. The season is the main factor.
				float randWeather = (float) Math.random();
				if(randWeather < data.season.rainfallchance && info.isRaining())
					info.setRaining(true);
				
				cycleController.applySeason(data.season);
				SurvivalInc.logger.info(data.toString());
			}
			
			// Affect daytime
			if(ModConfig.SEASONS.aenableDayLength) cycleController.alter(player.world);
			
			// We need to melt snow and ice manually in the spring.
			// Summer has a different melting method.
			if(data.season == Season.SPRING && player.dimension == 0)
			{
				melter.melt(player.world, player, data.day);
			}
		}
	}

	// Helps to melt snow in summer. Where there shouldn't be any snow.
	// Also does the leaves.
	@SubscribeEvent
	public static void onChunkWalkIn(EntityEvent.EnteringChunk event)
	{
		Entity ent = event.getEntity();
		if(ent instanceof EntityPlayer && !ent.world.isRemote)
		{
			EntityPlayer player = (EntityPlayer)ent;
			SeasonData data = SeasonData.load(player.world);
			
			// Is it summer? Then let's try to remove some snow and ice.
			if(data.season == Season.SUMMER && player.dimension == 0)
			{
				int chunkCoordX = event.getNewChunkX();
				int chunkCoordZ = event.getNewChunkZ();
				melter.meltCompletely(chunkCoordX, chunkCoordZ, player.world);
			}
		}
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

	// Add bonus harvest drops from crops in the Autumn.
	@SubscribeEvent
	public static void harvestDrops(BlockEvent.HarvestDropsEvent event)
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