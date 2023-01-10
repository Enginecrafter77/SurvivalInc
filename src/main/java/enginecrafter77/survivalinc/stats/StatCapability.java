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
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class StatCapability implements ICapabilitySerializable<NBTBase> {
	private static final ResourceLocation CAPABILITY_ID = new ResourceLocation(SurvivalInc.MOD_ID, "stats");

	@CapabilityInject(StatTracker.class)
	private static final Capability<StatTracker> target = null;
	
	private final StatTracker tracker;
	
	public StatCapability()
	{
		this.tracker = StatCapability.getInstance().getDefaultInstance();
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == StatCapability.getInstance();
	}

	@Override
	public <TARGET> TARGET getCapability(Capability<TARGET> capability, @Nullable EnumFacing facing)
	{
		return this.hasCapability(capability, facing) ? StatCapability.getInstance().cast(this.tracker) : null;
	}

	@Nullable
	@Override
	public NBTBase serializeNBT()
	{
		return StatCapability.getInstance().getStorage().writeNBT(target, this.tracker, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt)
	{
		StatCapability.getInstance().getStorage().readNBT(target, this.tracker, null, nbt);
	}
	
	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
	{
		if(!(event.getObject() instanceof EntityPlayer)) return;
		event.addCapability(StatCapability.CAPABILITY_ID, new StatCapability());
	}
	
	@SubscribeEvent
	public static void onPlayerEnterDimension(PlayerEvent.PlayerChangedDimensionEvent event)
	{
		if(!event.player.world.isRemote)
		{
			SurvivalInc.logger.info("Player {}({}) changed dimensions. Sending StatSyncMessage...", event.player.getName(),  event.player.getUniqueID().toString());
			SurvivalInc.net.sendTo(new StatSyncMessage().addAllPlayers(event.player.world), (EntityPlayerMP)event.player);
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawned(PlayerEvent.PlayerRespawnEvent event)
	{
		if(event.isEndConquered())
			return;
		StatCapability.resetStatsFor(event.player);
	}
	
	@SubscribeEvent
	public static void onClientJoin(EntityJoinWorldEvent event)
	{
		Entity ent = event.getEntity();
		if(ent instanceof EntityPlayer && ent.world.isRemote)
		{
			SurvivalInc.logger.info("Sending stat sync request...");
			SurvivalInc.net.sendToServer(new StatSyncRequestMessage());
		}
	}
	
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		StatCapability.obtainTracker(event.player).ifPresent((StatTracker tracker) -> {
			tracker.update(event.player);

			if(event.side == Side.SERVER && event.player.ticksExisted % ModConfig.GENERAL.serverSyncDelay == (ModConfig.GENERAL.serverSyncDelay - 1))
			{
				// Send update to all players about the currently processed player's stats
				SurvivalInc.net.sendToAll(new StatSyncMessage().addPlayer(event.player));
			}
		});
	}

	@SideOnly(Side.CLIENT)
	public static void requestSync()
	{
		SurvivalInc.net.sendToServer(new StatSyncRequestMessage());
	}

	public static void synchronizeStats(StatSyncMessage message)
	{
		SurvivalInc.net.sendToAll(message);
	}

	public static void resetStatsFor(EntityPlayer player)
	{
		StatCapability.obtainTracker(player).ifPresent((StatTracker tracker) -> {
			for(StatProvider<?> provider : tracker.getRegisteredProviders())
				resetRecord(tracker, provider);
		});
	}

	private static <T extends StatRecord> void resetRecord(StatTracker tracker, StatProvider<T> provider)
	{
		Optional.ofNullable(tracker.getRecord(provider)).ifPresent(provider::resetRecord);
	}

	public static <T extends StatRecord> Optional<T> obtainRecord(@Nullable StatProvider<T> provider, @Nonnull Entity entity)
	{
		if(provider == null)
			return Optional.empty();
		return StatCapability.obtainTracker(entity).map((StatTracker tracker) -> tracker.getRecord(provider));
	}

	@Nonnull
	@SuppressWarnings("ConstantValue") // It's not really constant...
	public static Capability<StatTracker> getInstance()
	{
		if(StatCapability.target == null)
			throw new IllegalStateException("Capability not available");
		return StatCapability.target;
	}

	public static Optional<StatTracker> obtainTracker(Entity entity)
	{
		return Optional.ofNullable(entity.getCapability(StatCapability.getInstance(), null));
	}
}
