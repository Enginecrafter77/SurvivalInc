package net.schoperation.schopcraft.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.schoperation.schopcraft.SchopCraft;

public class ItemCharcoalFilter extends Item {
	
	/*
	 * Filter da water. That's it. Just used in crafting filtered water bottles. 
	 */
	
	public ItemCharcoalFilter() {
		
		// Set registry and unlocalized name.
		setRegistryName(new ResourceLocation(SchopCraft.MOD_ID, "charcoal_filter"));
		setUnlocalizedName(SchopCraft.RESOURCE_PREFIX + "charcoal_filter");
		
		// Basic properties.
		setMaxStackSize(16);
		setCreativeTab(SchopCraft.mainTab);
	}
}