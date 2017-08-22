package net.schoperation.schopcraft.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.schoperation.schopcraft.SchopCraft;

public class ItemTowel extends Item {
	
	/*
	 * A simple towel used to dry one's self or drench one's self. Whichever fits the situation.
	 */
	
	public ItemTowel() {
		
		// Set registry + unlocalized names
		setRegistryName(new ResourceLocation(SchopCraft.MOD_ID, "towel"));
		
		// Properties
		setMaxStackSize(1);
		setCreativeTab(SchopCraft.mainTab);
		setNoRepair();
		setHasSubtypes(true);
	}
	
	// Using the towel on the PLAYER (so anywhere but water.)
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
		
		// Server side. TODO
		//if (!world.isRemote)
		return stack;
	}
	
	// Using the towel on some water source block.
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		
		// Server side. TODO
		//if (!world.isRemote)
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
	
	// Different unlocalized names
	@Override
	public String getUnlocalizedName(ItemStack stack) {
	
		if (stack.getMetadata() == 0) { return "item." + SchopCraft.RESOURCE_PREFIX + "dry_towel"; }
		else if (stack.getMetadata() == 1) { return "item." + SchopCraft.RESOURCE_PREFIX + "partially_wet_towel"; }
		else if (stack.getMetadata() == 2) { return "item." + SchopCraft.RESOURCE_PREFIX + "wet_towel"; }
		else { stack.setItemDamage(0); return "item." + SchopCraft.RESOURCE_PREFIX + "dry_towel";  }
	}
	
	// When crafted, give this item an NBT tag to store its wetness.
	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		
		if (!stack.hasTagCompound()) {
			
			// Making a new NBT tag with an amount of wetness, then tacking it onto the towel.
			NBTTagCompound nbt = new NBTTagCompound();
			
			if (stack.getMetadata() == 0) {
				
				nbt.setFloat("wetness", 0.0f);
			}
			
			else if (stack.getMetadata() == 1) {
				
				nbt.setFloat("wetness", 50.0f);
			}
			
			else {
				
				nbt.setFloat("wetness", 100.0f);
			}
			
			stack.setTagCompound(nbt);
		}
    }
	
	// This is treated as the main loop.
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		
		// Server-side.
		if (!worldIn.isRemote) {
			
			// If it somehow doesn't have an NBT tag yet, give it one.
			if (!stack.hasTagCompound()) {
				
				// Making a new NBT tag with an amount of wetness, then tacking it onto the towel.
				NBTTagCompound nbt = new NBTTagCompound();
				
				if (stack.getMetadata() == 0) {
					
					nbt.setFloat("wetness", 0.0f);
				}
				
				else if (stack.getMetadata() == 1) {
					
					nbt.setFloat("wetness", 50.0f);
				}
				
				else {
					
					nbt.setFloat("wetness", 100.0f);
				}
				
				stack.setTagCompound(nbt);
			}
			
			else {
				
				// If there is little wetness, make it a dry towel.
				NBTTagCompound nbt = stack.getTagCompound();
				
				if (nbt.getFloat("wetness") <= 5.0f) {
					
					stack.setItemDamage(0);
				}
				
				// Some wetness? Partially-wet towel.
				else if (nbt.getFloat("wetness") <= 50.0f) {
					
					stack.setItemDamage(1);
				}
				
				// Otherwise it's just a regular old towel.
				else {
					
					stack.setItemDamage(2);
				}
			}
			
			// =========================
			// Now onto the legit stuff
			// =========================
			
			// Spawn water splash/drip particles while held.
			if (isSelected && stack.getItemDamage() != 0) {
				
				// this'll be scaled. TODO
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		
		NBTTagCompound nbt = stack.getTagCompound();
		
		if (nbt != null) {
			
			// Round wetness to nearest tenth.
			float roundedWetness = (Math.round(nbt.getFloat("wetness")) * 10) / 10;
			
			if (roundedWetness > 0) {
				
				tooltip.add(TextFormatting.AQUA + Float.toString(roundedWetness) + "% Wet");
			}
			
			else {
				
				tooltip.add(TextFormatting.AQUA + "Completely Dry");
			}
		}
		
		else {
			
			if (stack.getItemDamage() == 0) {
				
				tooltip.add(TextFormatting.AQUA + "Completely Dry");
			}
			
			else if (stack.getItemDamage() == 1) {
				
				tooltip.add(TextFormatting.AQUA + "Partially Wet");
			}
			
			else {
				
				tooltip.add(TextFormatting.AQUA + "Wet");
			}
		}
	}
	
	// Create sub items
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		
		if (this.isInCreativeTab(tab)) {
            
			for (int i = 0; i < 3; i++) {
				
                items.add(new ItemStack(this, 1, i));
            }
        }
	}
	
	// Make it something...
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		
		return EnumAction.BLOCK;
	}
	
	// How long to show the animation
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		
		return 20;
	}
	
	// Show durability?
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		
		return false;
	}
}
