package enginecrafter77.survivalinc.stats.effect.item;

import java.util.Properties;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class ItemInHandSituation extends ItemInInvSituation {
	
	public ItemInHandSituation(Item item, Properties props)
	{
		super(item, props);
	}
	
	@Override
	public boolean isTriggeredBy(PlayerTickEvent event)
	{
		return this.getPlayer(event).getHeldItemMainhand().getItem() == this.item;
	}

}
