package net.schoperation.schopcraft.lib;

import net.minecraft.item.Item;
import net.schoperation.schopcraft.item.ItemCanteen;
import net.schoperation.schopcraft.item.ItemCharcoalFilter;
import net.schoperation.schopcraft.item.ItemFeatherFan;
import net.schoperation.schopcraft.item.ItemHydroPouch;
import net.schoperation.schopcraft.item.ItemIceCream;
import net.schoperation.schopcraft.item.ItemLucidDreamEssence;
import net.schoperation.schopcraft.item.ItemTabIcon;
import net.schoperation.schopcraft.item.ItemTowel;

public class ModItems {
	
	/*
	 *  A list of all items in the game, used to quickly register and render everything. kek
	 */
	
	// This list is here for easy referencing. In Alphabetical order.
	
	public static final Item CANTEEN = new ItemCanteen();
	public static final Item CHARCOAL_FILTER = new ItemCharcoalFilter();
	public static final Item FEATHER_FAN = new ItemFeatherFan();
	public static final Item HYDROPOUCH = new ItemHydroPouch();
	public static final Item ICE_CREAM = new ItemIceCream(4, 0.4f, false);
	public static final Item LUCID_DREAM_ESSENCE = new ItemLucidDreamEssence();
	public static final Item TAB_ICON = new ItemTabIcon();
	public static final Item TOWEL = new ItemTowel();
	
	// This list is used to actually register the items.
	public static final Item[] ITEMS = {
				
				TAB_ICON,
				CANTEEN,
				CHARCOAL_FILTER,
				LUCID_DREAM_ESSENCE,
				ICE_CREAM,
				FEATHER_FAN,
				HYDROPOUCH,
				TOWEL
				
	};
}
