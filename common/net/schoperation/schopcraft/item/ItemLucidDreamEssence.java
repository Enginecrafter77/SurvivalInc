package net.schoperation.schopcraft.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.schoperation.schopcraft.SchopCraft;

public class ItemLucidDreamEssence extends Item {
	
	/*
	 * This stuff is crazy!
	 */
	
	public ItemLucidDreamEssence() {
		
		// names
		setRegistryName(new ResourceLocation(SchopCraft.MOD_ID, "lucid_dream_essence"));
		setUnlocalizedName(SchopCraft.RESOURCE_PREFIX + "lucid_dream_essence");
		
		// properties
		setMaxStackSize(64);
		setCreativeTab(SchopCraft.mainTab);
	}
}
