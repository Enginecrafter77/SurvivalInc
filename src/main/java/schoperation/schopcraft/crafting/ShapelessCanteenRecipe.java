package schoperation.schopcraft.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import schoperation.schopcraft.item.ItemCanteen;
import schoperation.schopcraft.item.ItemHydroPouch;

import javax.annotation.Nonnull;

/*
 * This is used so the nbt data of canteens carries on through crafting.
 */

public class ShapelessCanteenRecipe extends ShapelessOreRecipe {

	public ShapelessCanteenRecipe(ResourceLocation group, NonNullList<Ingredient> input, ItemStack result) {
		
		super(group, input, result);
	}
	
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		
		// Original crafting result.
		ItemStack output = super.getCraftingResult(inv);
		
		if (!output.isEmpty()) {
			
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				
				// Currently selected ingredient.
				ItemStack ingredient = inv.getStackInSlot(i);
				
				// Is this something?
				if (!ingredient.isEmpty()) {
					
					// Is this a canteen or HydroPouch?
					if (ingredient.getItem() instanceof ItemCanteen || ingredient.getItem() instanceof ItemHydroPouch) {
						
						// Clone item durability and the number of sips.
						NBTTagCompound nbt = ingredient.getTagCompound();
						
						final int sips = nbt.getInteger("sips");
						final int durability = nbt.getInteger("durability");
						
						NBTTagCompound newNBT = new NBTTagCompound();
						newNBT.setInteger("sips", sips);
						newNBT.setInteger("durability", durability);
						output.setTagCompound(newNBT);
						
						break;
					}
				}
			}
		}
		
		return output;
	}
	
	// This is used to create the new recipe type. This is mainly Forge/Mojang code, as it's good enough.
	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse(JsonContext context, JsonObject json) {
			
			String group = JsonUtils.getString(json, "group", "");

	        NonNullList<Ingredient> ings = NonNullList.create();
	        for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients")) {
	        	
	            ings.add(CraftingHelper.getIngredient(ele, context));
	        }

	        if (ings.isEmpty()) {
	        	
	            throw new JsonParseException("No ingredients for shapeless recipe");
	        }

	        ItemStack stack = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
	        
	        return new ShapelessCanteenRecipe(group.isEmpty() ? null : new ResourceLocation(group), ings, stack);
		}
	}
}