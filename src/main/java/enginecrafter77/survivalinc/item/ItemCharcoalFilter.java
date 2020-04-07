package enginecrafter77.survivalinc.item;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemCharcoalFilter extends Item {

	/*
	 * Filter da water. That's it. Just used in crafting filtered water bottles.
	 */

	public ItemCharcoalFilter()
	{

		// Set registry and unlocalized name.
		setRegistryName(new ResourceLocation(SurvivalInc.MOD_ID, "charcoal_filter"));
		setUnlocalizedName(SurvivalInc.RESOURCE_PREFIX + "charcoal_filter");

		// Basic properties.
		setMaxStackSize(16);
		setCreativeTab(SurvivalInc.mainTab);
	}
}