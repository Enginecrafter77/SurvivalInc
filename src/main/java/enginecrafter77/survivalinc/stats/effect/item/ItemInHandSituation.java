package enginecrafter77.survivalinc.stats.effect.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class ItemInHandSituation implements EffectiveSituation<PlayerTickEvent> {

	public final Item item;
	
	public ItemInHandSituation(String[] parameters)
	{
		if(parameters.length < 1) throw new UnsupportedOperationException("At least 1 argument required!");
		this.item = Item.getByNameOrId(parameters[0]);
	}
	
	@Override
	public boolean isTriggered(PlayerTickEvent event)
	{
		return this.getPlayer(event).getHeldItemMainhand().getItem() == this.item;
	}

	@Override
	public EntityPlayer getPlayer(PlayerTickEvent event)
	{
		return event.player;
	}

	@Override
	public Class<PlayerTickEvent> getEventClass()
	{
		return PlayerTickEvent.class;
	}

}
