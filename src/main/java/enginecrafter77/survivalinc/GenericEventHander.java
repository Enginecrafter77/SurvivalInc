package enginecrafter77.survivalinc;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import enginecrafter77.survivalinc.item.ItemFeatherFan;

/*
 * This is the event handler regarding capabilities and changes to individual stats.
 * Most of the actual code is stored in the modifier classes of each stat, and fired here.
 */
@Mod.EventBusSubscriber
public class GenericEventHander {	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		event.getRegistry().register(ItemFeatherFan.WHOOSH);
	}
}