package enginecrafter77.survivalinc.item;

import enginecrafter77.survivalinc.SurvivalInc;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemLucidDreamEssence extends Item {

	/*
	 * This stuff is crazy!
	 */

	public ItemLucidDreamEssence()
	{

		// Set registry and unlocalized names.
		setRegistryName(new ResourceLocation(SurvivalInc.MOD_ID, "lucid_dream_essence"));
		setUnlocalizedName(SurvivalInc.RESOURCE_PREFIX + "lucid_dream_essence");

		// Basic properties.
		setMaxStackSize(64);
		setCreativeTab(SurvivalInc.mainTab);
	}
}