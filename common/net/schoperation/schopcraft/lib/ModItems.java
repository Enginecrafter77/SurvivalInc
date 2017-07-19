package net.schoperation.schopcraft.lib;

import net.minecraft.item.Item;
import net.schoperation.schopcraft.item.ItemCanteen;
import net.schoperation.schopcraft.item.ItemCharcoalFilter;
import net.schoperation.schopcraft.item.ItemFeatherFan;
import net.schoperation.schopcraft.item.ItemIceCream;
import net.schoperation.schopcraft.item.ItemLucidDreamEssence;
import net.schoperation.schopcraft.item.ItemTabIcon;

public class ModItems {
	
	/*
	 *  A list of all items in the game, used to quickly register and render everything. kek
	 */
	
	public static final Item[] ITEMS = {
				
				new ItemTabIcon(),
				new ItemCanteen(),
				new ItemCharcoalFilter(),
				new ItemLucidDreamEssence(),
				new ItemIceCream(4, 0.4f, false),
				new ItemFeatherFan()
				
	};
}
