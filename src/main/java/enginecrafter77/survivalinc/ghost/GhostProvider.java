package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.StatRegisterEvent;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.effect.EffectApplicator;
import enginecrafter77.survivalinc.stats.effect.EffectFilter;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffectFilter;
import enginecrafter77.survivalinc.stats.effect.SideEffectFilter;
import enginecrafter77.survivalinc.stats.effect.ValueStatEffect;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class GhostProvider implements StatProvider {
	private static final long serialVersionUID = -2088047893866334112L;
	
	public static final GhostProvider instance = new GhostProvider();
	public final EffectApplicator<GhostEnergyRecord> applicator;
	public final InteractionProcessor interactor;
	
	public GhostProvider()
	{
		this.applicator = new EffectApplicator<GhostEnergyRecord>();
		this.interactor = new InteractionProcessor(PlayerInteractEvent.RightClickBlock.class, (float)ModConfig.GHOST.interactionCost);
	}
	
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(GhostProvider.class);
		
		EffectFilter<Object> playerSprinting = FunctionalEffectFilter.byPlayer(EntityPlayer::isSprinting);
		this.applicator.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, -0.2F)).addFilter(playerSprinting);
		this.applicator.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, 0.05F)).addFilter(FunctionalEffectFilter.byPlayer(GhostProvider::duringNight));
		this.applicator.add(GhostProvider::sprintingParticles).addFilter(playerSprinting).addFilter(SideEffectFilter.CLIENT);
		this.applicator.add(GhostProvider::synchronizeFood);
		this.applicator.add(GhostProvider::onGhostUpdate);
		
		this.interactor.disable(Blocks.BED);
		this.interactor.disable(Blocks.FURNACE);
		this.interactor.disable(Blocks.LIT_FURNACE);
		this.interactor.disable(Blocks.CHEST);
		this.interactor.disable(Blocks.TRAPPED_CHEST);
		this.interactor.disable(Blocks.DISPENSER);
		this.interactor.disable(Blocks.DROPPER);
		this.interactor.disable(Blocks.CRAFTING_TABLE);
		this.interactor.disable(Blocks.BREWING_STAND);
		
		this.interactor.mapCase(Blocks.LEVER, 0.75F, 0F);
		this.interactor.mapCase(Blocks.STONE_BUTTON, 0.5F, 0F);
		this.interactor.mapCase(Blocks.TRAPDOOR, 0.9F, 0F);
	}
	
	@Override
	public void update(EntityPlayer target, StatRecord record)
	{
		GhostEnergyRecord ghost = (GhostEnergyRecord)record;
		
		if(ghost.shouldReceiveTicks())
		{
			this.applicator.apply(ghost, target);
			ghost.checkoutValueChange();
		}
	}
	
	@Override
	public ResourceLocation getStatID()
	{
		return new ResourceLocation(SurvivalInc.MOD_ID, "ghostenergy");
	}

	@Override
	public StatRecord createNewRecord()
	{
		return new GhostEnergyRecord();
	}
	
	public int energyToFood(GhostEnergyRecord record)
	{
		return Math.round(4F + 16F * (record.getValue() / record.valuerange.upperEndpoint()));
	}
	
	public float getInteractionCost(PlayerInteractEvent event)
	{
		Float value = this.interactor.apply(event);
		if(value == null)
		{
			SimpleStatRecord record = (SimpleStatRecord)GhostProvider.instance.createNewRecord();
			value = record.valuerange.upperEndpoint() + 1;
		}
		return value;
	}
	
	//==================================
	//=========[Event Handling]=========
	//==================================
	
	@SubscribeEvent
	public static void registerStat(StatRegisterEvent event)
	{
		event.register(GhostProvider.instance);
	}
	
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerRespawnEvent event)
	{
		EntityPlayer player = event.player;
		if(!event.isEndConquered())
		{
			StatTracker tracker = player.getCapability(StatCapability.target, null);
			GhostEnergyRecord record = (GhostEnergyRecord)tracker.getRecord(GhostProvider.instance);
			record.setActive(true);
			
			SurvivalInc.proxy.net.sendToAll(new StatSyncMessage(player));
		}
	}
	
	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent event)
	{
		StatTracker tracker = event.getEntityPlayer().getCapability(StatCapability.target, null);
		GhostEnergyRecord record = (GhostEnergyRecord)tracker.getRecord(GhostProvider.instance);
		if(record.isActive())
		{
			float cost = GhostProvider.instance.getInteractionCost(event);
			if(cost <= record.getValue())
			{
				record.addToValue(-cost);
			}
			else
			{
				if(event.isCancelable()) event.setCanceled(true);
				if(event.hasResult()) event.setResult(Result.DENY);
			}
		}
	}
	
	@SubscribeEvent
	public static void modifyVisibility(PlayerEvent.Visibility event)
	{
		StatTracker tracker = event.getEntityPlayer().getCapability(StatCapability.target, null);
		GhostEnergyRecord record = (GhostEnergyRecord)tracker.getRecord(GhostProvider.instance);
		if(record.isActive()) event.modifyVisibility(0D);
	}
	
	//==================================
	//=======[Functional Effects]=======
	//==================================
	
	public static void synchronizeFood(GhostEnergyRecord record, EntityPlayer player)
	{
		if(record.isActive())
		{
			FoodStats food = player.getFoodStats();
			food.setFoodLevel(GhostProvider.instance.energyToFood(record));
		}
	}
	
	public static void onGhostUpdate(GhostEnergyRecord record, EntityPlayer player)
	{		
		if(record.hasPendingChange())
		{
			boolean isGhost = record.isActive();
			player.capabilities.disableDamage = isGhost;
			player.capabilities.allowEdit = !isGhost;
			record.acceptChange();
		}
	}
	
	public static void sprintingParticles(GhostEnergyRecord record, EntityPlayer player)
	{
		WorldClient world = (WorldClient)player.world;
		world.spawnParticle(EnumParticleTypes.CLOUD, player.lastTickPosX, player.lastTickPosY + (player.height / 2), player.lastTickPosZ, -player.motionX, 0D, -player.motionZ);
	}
	
	public static boolean duringNight(EntityPlayer player)
	{
		boolean night;
		if(player.world.isRemote)
		{
			float angle = player.world.getCelestialAngle(1F);
			night = angle < 0.75F && angle > 0.25F;
		}
		else night = !player.world.isDaytime();
		return night;
	}
}
