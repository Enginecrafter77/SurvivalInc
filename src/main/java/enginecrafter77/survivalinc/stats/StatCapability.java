package enginecrafter77.survivalinc.stats;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import enginecrafter77.survivalinc.net.StatSyncRequestMessage;
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
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class StatCapability implements ICapabilitySerializable<NBTBase> {
	private static ResourceLocation identificator = new ResourceLocation(SurvivalInc.MOD_ID, "stats");

	@Nonnull
	@CapabilityInject(StatTracker.class)
	public static Capability<StatTracker> target;
	
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
	public static void onPlayerEnterDimension(PlayerEvent.PlayerChangedDimensionEvent event)
	{
		if(!event.player.world.isRemote)
		{
			SurvivalInc.logger.info("Player {}({}) changed dimensions. Sending StatSyncMessage...", event.player.getName(),  event.player.getUniqueID().toString());
			SurvivalInc.proxy.net.sendTo(new StatSyncMessage().addAllPlayers(event.player.world), (EntityPlayerMP)event.player);
		}
	}
	
	@SubscribeEvent
	public static void onClientJoin(EntityJoinWorldEvent event)
	{
		Entity ent = event.getEntity();
		if(ent instanceof EntityPlayer && ent.world.isRemote)
		{
			SurvivalInc.logger.info("Sending stat sync request...");
			SurvivalInc.proxy.net.sendToServer(new StatSyncRequestMessage());
		}
	}
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		StatTracker stat = event.player.getCapability(StatCapability.target, null);
		stat.update(event.player);
		
		if(event.side == Side.SERVER && event.player.ticksExisted % ModConfig.GENERAL.serverSyncDelay == (ModConfig.GENERAL.serverSyncDelay - 1))
		{
			// Send update to all players about the currently processed player's stats
			SurvivalInc.proxy.net.sendToAll(new StatSyncMessage().addPlayer(event.player));
		}
	}

	public static <T extends StatRecord> Optional<T> obtainRecord(@Nullable StatProvider<T> provider, @Nonnull Entity entity)
	{
		if(provider == null)
			return Optional.empty();
		return Optional.ofNullable(entity.getCapability(StatCapability.target, null)).map((StatTracker tracker) -> tracker.getRecord(provider));
	}
}
