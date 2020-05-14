package enginecrafter77.survivalinc.stats;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber
public class StatCapability implements ICapabilitySerializable<NBTBase> {
	private static ResourceLocation identificator = new ResourceLocation(SurvivalInc.MOD_ID, "stats");
	
	@CapabilityInject(StatTracker.class)
	public static final Capability<StatTracker> target = null;
	
	private final StatTracker tracker;
	
	public StatCapability()
	{
		this.tracker = target.getDefaultInstance();
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == target;
	}

	@Override
	public <TARGET> TARGET getCapability(Capability<TARGET> capability, EnumFacing facing)
	{
		return this.hasCapability(capability, facing) ? target.cast(this.tracker) : null;
	}

	@Override
	public NBTBase serializeNBT()
	{
		return target.getStorage().writeNBT(target, this.tracker, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt)
	{
		target.getStorage().readNBT(target, this.tracker, null, nbt);
	}
	
	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
	{
		if(!(event.getObject() instanceof EntityPlayer)) return;
		event.addCapability(StatCapability.identificator, new StatCapability());
	}
	
	@SubscribeEvent
	public static void onPlayerLogsIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		// Do this only on server side
		if(!event.player.world.isRemote)
		{
			StatTracker stat = event.player.getCapability(StatCapability.target, null);
			SurvivalInc.proxy.net.sendTo(new StatSyncMessage(stat), (EntityPlayerMP)event.player);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerUpdate(LivingUpdateEvent event)
	{
		Entity ent = event.getEntity();
		
		if(ent instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)ent;
			StatTracker stat = player.getCapability(StatCapability.target, null);
			stat.update(player);
			
			if(!player.world.isRemote && player.world.getWorldTime() % 200 == 0)
			{
				// Send update
				SurvivalInc.proxy.net.sendTo(new StatSyncMessage(stat), (EntityPlayerMP)player);
			}
		}
	}
}