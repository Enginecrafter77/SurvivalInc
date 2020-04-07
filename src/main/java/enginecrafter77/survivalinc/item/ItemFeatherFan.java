package enginecrafter77.survivalinc.item;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.util.SchopServerSounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemFeatherFan extends Item {

	/*
	 * A nice handheld fan that instantly cools the player down.
	 */

	public ItemFeatherFan()
	{

		// Set registry and unlocalized name.
		setRegistryName(SurvivalInc.MOD_ID, "feather_fan");
		setUnlocalizedName(SurvivalInc.RESOURCE_PREFIX + "feather_fan");

		// Basic properties.
		setMaxStackSize(1);
		setCreativeTab(SurvivalInc.mainTab);
		setMaxDamage(6);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		// Server-side.
		if (!world.isRemote)
		{
			//VitalStat stats = player.getCapability(VitalStatProvider.VITAL_CAP, null);

			// Being in water or lava won't allow use of the fan.
			if (!player.isInWater() && !player.isInLava())
			{
				// Cool down player.
				//temperature.decrease(20.0f);

				// If on fire, put out the fire.
				if (player.isBurning())
				{

					player.extinguish();
				}

				// Play sound
				SchopServerSounds.playSound(player.getCachedUniqueIdString(), "FanWhooshSound", player.posX,
						player.posY, player.posZ);

				// Damage item
				player.getHeldItem(hand).damageItem(1, player);
			}
		}

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
}