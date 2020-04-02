package schoperation.schopcraft.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import schoperation.schopcraft.SchopCraft;

public class ItemLucidDreamEssence extends Item {

	/*
	 * This stuff is crazy!
	 */

	public ItemLucidDreamEssence()
	{

		// Set registry and unlocalized names.
		setRegistryName(new ResourceLocation(SchopCraft.MOD_ID, "lucid_dream_essence"));
		setUnlocalizedName(SchopCraft.RESOURCE_PREFIX + "lucid_dream_essence");

		// Basic properties.
		setMaxStackSize(64);
		setCreativeTab(SchopCraft.mainTab);
	}
}