package net.schoperation.schopcraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.schoperation.schopcraft.SchopCraft;
import net.schoperation.schopcraft.cap.temperature.ITemperature;
import net.schoperation.schopcraft.cap.temperature.TemperatureProvider;
import net.schoperation.schopcraft.util.SchopServerSounds;

public class ItemFeatherFan extends Item {
	
	/*
	 * A nice handheld fan that instantly cools the player down.
	 */
	
	public ItemFeatherFan() {
		
		// name crap
		setRegistryName(SchopCraft.MOD_ID, "feather_fan");
		setUnlocalizedName(SchopCraft.RESOURCE_PREFIX + "feather_fan");
		
		// basic properties
		setMaxStackSize(1);
		setCreativeTab(SchopCraft.mainTab);
		setMaxDamage(7);
		
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		
		// server-side
		if (!world.isRemote) {
			
			// get player capability
			ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
			
			// being in water or lava won't allow use of the fan
			if (!player.isInWater() && !player.isInLava()) {
				
				// cool down player
				temperature.decrease(20.0f);
				
				// if on fire, put out the fire
				if (player.isBurning()) {
					
					player.extinguish();
				}
				
				// play sound
				SchopServerSounds.playSound(player.getCachedUniqueIdString(), "FanWhooshSound", player.posX, player.posY, player.posZ);
				
				// damage item
				player.getHeldItem(hand).damageItem(1, player);
			}
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
}
