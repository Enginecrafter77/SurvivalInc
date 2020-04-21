package enginecrafter77.survivalinc.item;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.impl.DefaultStats;
import enginecrafter77.survivalinc.stats.impl.HeatModifier;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemFeatherFan extends Item {	
	public ItemFeatherFan()
	{
		this.setRegistryName(SurvivalInc.MOD_ID, "feather_fan");
		this.setTranslationKey("feather_fan");
		this.setCreativeTab(SurvivalInc.mainTab);
		this.setMaxStackSize(1);
		this.setMaxDamage(6);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		if(!player.isInWater() && !player.isInLava())
		{
			if(world.isRemote) ((WorldClient)world).playSound(player.getPosition(), SoundEvents.ENTITY_PARROT_FLY, SoundCategory.PLAYERS, 0.2F, 1.25F, false);
			else
			{
				StatTracker stats = player.getCapability(StatCapability.target, null);
				stats.modifyStat(HeatModifier.instance, -20F);
				stats.modifyStat(DefaultStats.WETNESS, -5F);
				if(player.isBurning()) player.extinguish();
				player.getHeldItem(hand).damageItem(1, player);
			}
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
}