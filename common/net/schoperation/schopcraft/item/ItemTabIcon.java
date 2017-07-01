package net.schoperation.schopcraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.schoperation.schopcraft.SchopCraft;

public class ItemTabIcon extends Item {
	
	/*
	 * This is the tab icon. Not meant to be an actual part of the experience. But, I've put in some fun crap with it anyway.
	 */
	
	public ItemTabIcon() {
		
		// setting registry name and crap
		setRegistryName(new ResourceLocation(SchopCraft.MOD_ID, "tabicon"));
		setUnlocalizedName(SchopCraft.RESOURCE_PREFIX + "tabicon");
		
		// additional properties
		setMaxStackSize(1);
		setCreativeTab(SchopCraft.mainTab);
		
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		
		if (world.isRemote) 
			player.sendMessage(new TextComponentString("§6Congratulations you found the creative tab icon, the first thing ever added to this mod."));
		
		return super.onItemRightClick(world, player, hand);
	}
}
