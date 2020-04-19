package enginecrafter77.survivalinc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import enginecrafter77.survivalinc.ghost.GhostProvider;
import enginecrafter77.survivalinc.item.ItemFeatherFan;
import enginecrafter77.survivalinc.ghost.Ghost;
import enginecrafter77.survivalinc.net.StatUpdateMessage;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;

/*
 * This is the event handler regarding capabilities and changes to individual stats.
 * Most of the actual code is stored in the modifier classes of each stat, and fired here.
 */
@Mod.EventBusSubscriber
public class GenericEventHander {
	
	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
	{
		if(!(event.getObject() instanceof EntityPlayer)) return;
		
		event.addCapability(new ResourceLocation(SurvivalInc.MOD_ID, "ghost"), new GhostProvider());
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		event.getRegistry().register(ItemFeatherFan.WHOOSH);
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
				StatTracker stat = player.getCapability(StatCapability.target, null);
				Ghost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
				stat.update(player);
				ghost.update(player);
				SurvivalInc.proxy.net.sendTo(new StatUpdateMessage(stat), (EntityPlayerMP)player);
			}
		}
	}
}