package enginecrafter77.survivalinc.item;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.impl.HeatModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class ItemFeatherFan extends Item {
	
	public static final SoundEvent WHOOSH = new SoundEvent(new ResourceLocation(SurvivalInc.MOD_ID, "fan_whoosh")).setRegistryName(SurvivalInc.MOD_ID, "fan_whoosh");
	
	public ItemFeatherFan()
	{
		this.setRegistryName(SurvivalInc.MOD_ID, "feather_fan");
		this.setUnlocalizedName(SurvivalInc.RESOURCE_PREFIX + "feather_fan");
		this.setCreativeTab(SurvivalInc.mainTab);
		this.setMaxStackSize(1);
		this.setMaxDamage(6);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		if(!world.isRemote)
		{
			StatTracker stats = player.getCapability(StatRegister.CAPABILITY, null);
			if(!player.isInWater() && !player.isInLava())
			{
				stats.modifyStat(HeatModifier.instance, -20F);
				if(player.isBurning()) player.extinguish();
				player.world.playSound(null, player.getPosition(), WHOOSH, SoundCategory.PLAYERS, 0.2f, 1.25f);
			}
			player.getHeldItem(hand).damageItem(1, player);
		}

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
}