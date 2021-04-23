package enginecrafter77.survivalinc.stats.effect.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public interface EffectiveSituation<EVENT extends Event> {
	
	public boolean isTriggered(EVENT event);
	
	public EntityPlayer getPlayer(EVENT event);
	
	public Class<EVENT> getEventClass();
	
}
