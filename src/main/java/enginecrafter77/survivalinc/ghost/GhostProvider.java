package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.StatRegisterEvent;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.effect.EffectApplicator;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffectFilter;
import enginecrafter77.survivalinc.stats.effect.ValueStatEffect;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class GhostProvider implements StatProvider {
	private static final long serialVersionUID = -2088047893866334112L;
	
	public final EffectApplicator<GhostEnergyRecord> applicator;
	
	public GhostProvider()
	{
		this.applicator = new EffectApplicator<GhostEnergyRecord>();
		
		this.applicator.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, -0.2F)).addFilter(FunctionalEffectFilter.byPlayer(EntityPlayer::isSprinting));
		this.applicator.add(new ValueStatEffect(ValueStatEffect.Operation.OFFSET, 0.05F)).addFilter(FunctionalEffectFilter.byPlayer(GhostProvider::duringNight));
		this.applicator.add(GhostProvider::onGhostUpdate);
	}
	
	@Override
	public void update(EntityPlayer target, StatRecord record)
	{
		GhostEnergyRecord ghost = (GhostEnergyRecord)record;
		
		if(ghost.shouldReceiveTicks())
		{
			this.applicator.apply(ghost, target);
			ghost.checkoutValueChange();
			
			FoodStats food = target.getFoodStats();
			food.setFoodLevel(this.energyToFood(ghost));
		}
	}
	
	public int energyToFood(GhostEnergyRecord record)
	{
		return Math.round(20F * (record.getValue() / record.valuerange.upperEndpoint()));
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
	
	@SubscribeEvent
	public static void registerStat(StatRegisterEvent event)
	{
		event.register(SurvivalInc.proxy.ghost);
	}
	
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerRespawnEvent event)
	{
		EntityPlayer player = event.player;
		if(!event.isEndConquered())
		{
			StatTracker tracker = player.getCapability(StatCapability.target, null);
			GhostEnergyRecord record = (GhostEnergyRecord)tracker.getRecord(SurvivalInc.proxy.ghost);
			record.setActive(true);
			
			SurvivalInc.proxy.net.sendToAll(new StatSyncMessage(player));
		}
	}
	
	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent event)
	{
		StatTracker tracker = event.getEntityPlayer().getCapability(StatCapability.target, null);
		GhostEnergyRecord record = (GhostEnergyRecord)tracker.getRecord(SurvivalInc.proxy.ghost);
		if(record.isActive())
		{
			if(event.isCancelable()) event.setCanceled(true);
			event.setResult(Result.DENY);
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
	
	public static void disableSprinting(GhostEnergyRecord record, EntityPlayer player)
	{
		if(player.isSprinting())
		{
			player.setSprinting(false); //RenderHandEvent
		}
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
