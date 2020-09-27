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
import net.minecraft.util.ResourceLocation;
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
	}
	
	@Override
	public void update(EntityPlayer target, StatRecord record)
	{
		GhostEnergyRecord ghost = (GhostEnergyRecord)record;
		
		if(ghost.isActive())
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
