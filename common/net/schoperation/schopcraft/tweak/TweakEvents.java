package net.schoperation.schopcraft.tweak;

import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/*
* "Tweaks" as I'm calling them are (usually) tiny, miscellaneous changes to the game, that don't fit with anything else.
* This class will make sure they're called.
*/

@Mod.EventBusSubscriber
public class TweakEvents {
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event) {
		
		// nothing yet
	}
}
