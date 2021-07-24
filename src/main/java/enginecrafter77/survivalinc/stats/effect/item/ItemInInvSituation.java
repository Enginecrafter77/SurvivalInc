package enginecrafter77.survivalinc.stats.effect.item;

import java.util.Properties;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class ItemInInvSituation extends ItemSituation<PlayerTickEvent> {

	public final ItemStack stack;
	
	public ItemInInvSituation(Item item, Properties properties)
	{
		super(item, properties);
		this.stack = new ItemStack(item);
	}
	
	@Override
	public boolean isTriggeredBy(PlayerTickEvent event)
	{
		return this.getPlayer(event).inventory.hasItemStack(this.stack);
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
