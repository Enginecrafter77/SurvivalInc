package enginecrafter77.survivalinc.stats.effect.item;

import java.util.Properties;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

public class ItemConsumedSituation extends ItemSituation<LivingEntityUseItemEvent.Finish> {

	public ItemConsumedSituation(Item item, Properties props)
	{
		super(item, props);
	}

	@Override
	public boolean isTriggeredBy(LivingEntityUseItemEvent.Finish event)
	{
		return event.getEntityLiving() instanceof EntityPlayer ? event.getItem().getItem() == this.item : false;
	}

	@Override
	public EntityPlayer getPlayer(LivingEntityUseItemEvent.Finish event)
	{
		return (EntityPlayer)event.getEntityLiving();
	}

	@Override
	public Class<LivingEntityUseItemEvent.Finish> getEventClass()
	{
		return LivingEntityUseItemEvent.Finish.class;
	}

}
