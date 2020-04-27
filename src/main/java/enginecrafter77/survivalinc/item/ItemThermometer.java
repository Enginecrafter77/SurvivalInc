package enginecrafter77.survivalinc.item;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemThermometer extends Item {
	public ItemThermometer()
	{
		this.setRegistryName(SurvivalInc.MOD_ID, "thermometer");
		this.setTranslationKey("thermometer");
		this.setCreativeTab(SurvivalInc.mainTab);
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		if(!world.isRemote) // Server has more accurate data
		{
			float target = world.getBiome(player.getPosition()).getTemperature(player.getPosition());
			player.sendMessage(new TextComponentString("Current temperature: " + target));
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
}
