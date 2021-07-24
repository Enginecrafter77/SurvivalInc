package enginecrafter77.survivalinc.stats.effect.item;

import java.util.LinkedList;
import java.util.List;

import enginecrafter77.survivalinc.stats.StatCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemSituationContainer {
	
	public final List<ItemSituation<?>> effects;
	
	public ItemSituationContainer()
	{
		this.effects = new LinkedList<ItemSituation<?>>();
	}
	
	@SubscribeEvent
	public void invoke(Event event)
	{
		this.effects.forEach((ItemSituation<?> effect) -> applyTo(effect, event));
	}
	
	private static <TYPE extends Event> void applyTo(ItemSituation<TYPE> situation, Event event)
	{
		Class<TYPE> specclass = situation.getEventClass();
		if(!specclass.isInstance(event)) return;
		
		TYPE specevent = specclass.cast(event);
		if(situation.isTriggeredBy(specevent))
		{
			EntityPlayer player = situation.getPlayer(specevent);
			situation.apply(player.getCapability(StatCapability.target, null));
		}
	}
	
}
