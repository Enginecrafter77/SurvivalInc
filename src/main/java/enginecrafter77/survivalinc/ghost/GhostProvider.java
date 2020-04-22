package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.StatManager;
import enginecrafter77.survivalinc.stats.modifier.ConditionalModifier;
import enginecrafter77.survivalinc.stats.modifier.FunctionalModifier;
import enginecrafter77.survivalinc.stats.modifier.OperationType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

// Provides ghost mechanic to the player.
public class GhostProvider implements ICapabilitySerializable<NBTBase> {

	private static final ResourceLocation identifier = new ResourceLocation(SurvivalInc.MOD_ID, "ghost");
	
	@CapabilityInject(Ghost.class)
	public static final Capability<Ghost> target = null;
	
	private final Ghost instance;

	public GhostProvider()
	{
		this.instance = target.getDefaultInstance();
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == target;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		return capability == target ? target.<T> cast(this.instance) : null;
	}

	@Override
	public NBTBase serializeNBT()
	{
		return target.getStorage().writeNBT(target, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt)
	{
		target.getStorage().readNBT(target, this.instance, null, nbt);
	}
	
	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
	{
		if(!(event.getObject() instanceof EntityPlayer)) return;
		event.addCapability(identifier, new GhostProvider());
	}
	
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerRespawnEvent event)
	{
		EntityPlayer player = event.player;
		if(!event.isEndConquered())
		{
			Ghost ghost = player.getCapability(GhostProvider.target, null);
			ghost.setStatus(true);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerUpdate(LivingUpdateEvent event)
	{
		Entity ent = event.getEntity();
		if(ent.world.isRemote) return;
		
		if(ent instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)ent;
			if(!player.isCreative() && !player.isSpectator())
			{
				Ghost ghost = player.getCapability(GhostProvider.target, null);
				ghost.update(player);
			}
		}
	}
	
	public static void register()
	{
		MinecraftForge.EVENT_BUS.register(GhostProvider.class);
		CapabilityManager.INSTANCE.register(Ghost.class, new GhostStorage(), GhostImpl::new);
		GhostEnergy.instance.add(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.isSprinting(), -0.2F), OperationType.OFFSET);
		GhostEnergy.instance.add(new FunctionalModifier<EntityPlayer>(GhostProvider::duringNight), OperationType.OFFSET);
		StatManager.providers.add(GhostEnergy.instance);
	}
	
	public static float duringNight(EntityPlayer player)
	{
		boolean night;
		if(player.world.isRemote)
		{
			float angle = player.world.getCelestialAngle(1F);
			night = angle < 0.75F && angle > 0.25F;
		}
		else night = !player.world.isDaytime();
		
		return night ? 0.05F : 0F;
	}
}