package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.StatRegisterEvent;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.effect.ConstantStatEffect;
import enginecrafter77.survivalinc.stats.effect.FilteredEffectApplicator;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffect;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffectFilter;
import enginecrafter77.survivalinc.stats.impl.DefaultStats;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class GhostProvider implements StatProvider {
	private static final long serialVersionUID = -2088047893866334112L;
	
	public static final ResourceLocation identifier = new ResourceLocation(SurvivalInc.MOD_ID, "ghostenergy");
	public static final GhostProvider instance = new GhostProvider();
	
	public final FilteredEffectApplicator applicator;
	
	private GhostProvider()
	{
		this.applicator = new FilteredEffectApplicator();
		
		this.applicator.addEffect(new ConstantStatEffect(ConstantStatEffect.Operation.OFFSET, -0.2F), new FunctionalEffectFilter((EntityPlayer player, Float value) -> player.isSprinting()));
		this.applicator.addEffect(new FunctionalEffect(GhostProvider::duringNight));
	}
	
	@Override
	public float updateValue(EntityPlayer target, float current)
	{		
		return DefaultStats.capValue(this, this.applicator.apply(target, current));
	}

	@Override
	public ResourceLocation getStatID()
	{
		return GhostProvider.identifier;
	}

	@Override
	public float getMaximum()
	{
		return 100F;
	}

	@Override
	public float getMinimum()
	{
		return 0F;
	}

	@Override
	public StatRecord createNewRecord()
	{
		return new GhostEnergyRecord();
	}

	@Override
	public boolean isAcitve(EntityPlayer player)
	{
		StatTracker ghost = player.getCapability(StatCapability.target, null);
		GhostEnergyRecord stat = (GhostEnergyRecord)ghost.getRecord(this);
		return stat.isActive();
	}
	
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
	
	public static float duringNight(EntityPlayer player, float value)
	{
		boolean night;
		if(player.world.isRemote)
		{
			float angle = player.world.getCelestialAngle(1F);
			night = angle < 0.75F && angle > 0.25F;
		}
		else night = !player.world.isDaytime();
		
		if(night) value += 0.05F;
		return value;
	}
}
