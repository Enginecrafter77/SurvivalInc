package net.schoperation.schopcraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeBeach;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.biome.BiomeSnow;
import net.minecraft.world.biome.BiomeSwamp;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.schoperation.schopcraft.SchopCraft;
import net.schoperation.schopcraft.cap.sanity.ISanity;
import net.schoperation.schopcraft.cap.sanity.SanityProvider;
import net.schoperation.schopcraft.cap.temperature.ITemperature;
import net.schoperation.schopcraft.cap.temperature.TemperatureProvider;
import net.schoperation.schopcraft.cap.thirst.IThirst;
import net.schoperation.schopcraft.cap.thirst.ThirstProvider;
import net.schoperation.schopcraft.util.SchopServerEffects;
import net.schoperation.schopcraft.util.SchopServerParticles;
import net.schoperation.schopcraft.util.SchopServerSounds;

public class ItemCanteen extends Item {
	
	/*
	 * A simple canteen for all your thirst needs.
	 */
	
	public ItemCanteen() {
		
		// setting registry name and crap
		setRegistryName(new ResourceLocation(SchopCraft.MOD_ID, "canteen"));
		
		// properties
		setMaxStackSize(1);
		setCreativeTab(SchopCraft.mainTab);
		setMaxDamage(105);
		setNoRepair();
		setHasSubtypes(true);
		
	}
	
	// drinking from the canteen
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
		
		// server-side
		if (entityLiving instanceof EntityPlayerMP && !world.isRemote) {
			
			// basic variables
			EntityPlayerMP player = (EntityPlayerMP) entityLiving;
			String canteenType = stack.getUnlocalizedName();
			String uuid = player.getCachedUniqueIdString();
			
			// capabilities
			IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
			ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
			ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
			
			// determine type of water, and quench thirst accordingly
			// fresh water
			if (canteenType.equals("item." + SchopCraft.RESOURCE_PREFIX + "fresh_water_canteen")) {
				
				thirst.increase(20f);
				temperature.decrease(10f);
				sanity.increase(15f);
			}
			
			// dirty water
			else if (canteenType.equals("item." + SchopCraft.RESOURCE_PREFIX + "dirty_water_canteen")) {
				
				thirst.increase(10f);
				sanity.decrease(5f);
				SchopServerEffects.affectPlayer(uuid, "poison", 50, 2, false, false);
			}
			
			// filtered water
			else if (canteenType.equals("item." + SchopCraft.RESOURCE_PREFIX + "filtered_water_canteen")) {
				
				thirst.increase(15f);
				sanity.increase(10f);
				double randChanceOfPoison = Math.random();
				if (randChanceOfPoison < 0.30) { SchopServerEffects.affectPlayer(uuid, "poison", 50, 0, false, false); }
			}
			
			// cold water
			else if (canteenType.equals("item." + SchopCraft.RESOURCE_PREFIX + "cold_water_canteen")) {
				
				thirst.increase(15f);
				temperature.decrease(15f);
				sanity.increase(10f);
				double randChanceOfPoison = Math.random();
				if (randChanceOfPoison < 0.15) { SchopServerEffects.affectPlayer(uuid, "poison", 50, 0, false, false); }
			}
			
			// salt water
			else {
				
				thirst.decrease(20f);
				sanity.decrease(15f);
			}
			
			// decrease durability
			stack.damageItem(33, player);
		}
		
		return stack;
	}
	
	
	// right click to get water. If the player is holding an empty canteen, it will attempt to fill it up with water.
	// if holding a canteen partially filled with water, it will refill it with water.
	// if holding a full canteen with the same type of water it's going to get, it'll empty itself, becoming an empty canteen.
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		
		// server-side.
		if (!world.isRemote) {
			
			// ray trace result
			// first, some "boosts" to the vector
			double vecX = 0;
			double vecZ = 0;
			if (player.getLookVec().x < 0) { vecX = -0.5; }
			else if (player.getLookVec().x > 0) { vecX = 0.5; }
			if (player.getLookVec().z < 0) { vecZ = -0.5; }
			else if (player.getLookVec().z > 0) { vecZ = 0.5; }
			
			// now the actual raytrace
			RayTraceResult raytrace = world.rayTraceBlocks(player.getPositionEyes(1.0f), player.getPositionEyes(1.0f).add(player.getLookVec().addVector(vecX, -1, vecZ)), true);
			
			// held item
			ItemStack heldItem = player.getHeldItem(hand);
			
			// Did they right click on a block?
			if (raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK) {
				
				// position of the raytrace
				BlockPos pos = raytrace.getBlockPos();
				
				// was this water?
				if (world.getBlockState(pos).getMaterial() == Material.WATER) {
					
					// now figure out the biome of the block
					Biome biome = world.getBiome(pos);
					
					// Ocean biome
					if (biome instanceof BiomeOcean || biome instanceof BiomeBeach) {
						
						// what is the player holding?
						// empty canteen
						if (heldItem.getUnlocalizedName().equals("item." + SchopCraft.RESOURCE_PREFIX + "empty_canteen")) { heldItem.setItemDamage(3); }
						
						// full canteen (of any type)
						else if (heldItem.getItemDamage() < 6 && heldItem.getItemDamage() > 0) { heldItem.setItemDamage(0); }
						
						// otherwise just refill it with salt water
						else { heldItem.setItemDamage(3); }
					}
					
					// Swamp biome
					else if (biome instanceof BiomeSwamp) {
						
						// empty canteen
						if (heldItem.getUnlocalizedName().equals("item." + SchopCraft.RESOURCE_PREFIX + "empty_canteen")) { heldItem.setItemDamage(2); }
					
						// full canteen (of any type)
						else if (heldItem.getItemDamage() < 6 && heldItem.getItemDamage() > 0) { heldItem.setItemDamage(0); }
						
						// otherwise just refill it with dirty water
						else { heldItem.setItemDamage(2); }
					}
					
					// snow biome
					else if (biome instanceof BiomeSnow) {
						
						// random chance to give cold water opposed to dirty water.
						double randomNum = Math.random();
						
						// empty canteen
						if (heldItem.getUnlocalizedName().equals("item." + SchopCraft.RESOURCE_PREFIX + "empty_canteen")) { if (randomNum < 0.80) { heldItem.setItemDamage(2); } else { heldItem.setItemDamage(5); } }
						
						// full canteen (of any type)
						else if (heldItem.getItemDamage() < 6 && heldItem.getItemDamage() > 0) { heldItem.setItemDamage(0); }
						
						// otherwise fill it with dirty water or cold water, according to what's already in it
						else if (heldItem.getUnlocalizedName().equals("item." + SchopCraft.RESOURCE_PREFIX + "dirty_water_canteen") || heldItem.getUnlocalizedName().equals("item." + SchopCraft.RESOURCE_PREFIX + "filtered_water_canteen")) { heldItem.setItemDamage(2); }
						else { if (randomNum < 0.80) { heldItem.setItemDamage(2); } else { heldItem.setItemDamage(5); } }
					}
					
					// other biomes
					else {
						
						// random chance to give fresh water opposed to dirty water.
						double randomNum = Math.random();
						
						// empty canteen
						if (heldItem.getUnlocalizedName().equals("item." + SchopCraft.RESOURCE_PREFIX + "empty_canteen")) { if (randomNum < 0.90) { heldItem.setItemDamage(2); } else { heldItem.setItemDamage(1); } }
						
						// full canteen (of any type)
						else if (heldItem.getItemDamage() < 6 && heldItem.getItemDamage() > 0) { heldItem.setItemDamage(0); }
						
						// otherwise fill it with dirty water or fresh water, according to what's already in it
						else if (heldItem.getUnlocalizedName().equals("item." + SchopCraft.RESOURCE_PREFIX + "dirty_water_canteen") || heldItem.getUnlocalizedName().equals("item." + SchopCraft.RESOURCE_PREFIX + "filtered_water_canteen")) { heldItem.setItemDamage(2); }
						else { if (randomNum < 0.90) { heldItem.setItemDamage(2); } else { heldItem.setItemDamage(1); } }
						
					}
					
					// play sounds and particles directly (as this is already server-side)
					SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "DrinkWaterParticles", pos.getX(), pos.getY(), pos.getZ());
					SchopServerSounds.playSound(player.getCachedUniqueIdString(), "WaterSound", pos.getX(), pos.getY(), pos.getZ());
					
					return new ActionResult<ItemStack>(EnumActionResult.PASS, heldItem);
				}
				
				// all of this crap is to ensure that the player can drink from the canteen if it isn't empty.
				else if (heldItem.getUnlocalizedName().equals("item." + SchopCraft.RESOURCE_PREFIX + "empty_canteen")) {
					
					return new ActionResult<ItemStack>(EnumActionResult.PASS, heldItem);
				}
				else {
					
					player.setActiveHand(hand);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, heldItem);
				}
			}
			else if (heldItem.getUnlocalizedName().equals("item." + SchopCraft.RESOURCE_PREFIX + "empty_canteen")) {
				
				return new ActionResult<ItemStack>(EnumActionResult.PASS, heldItem);
			}
			else {
				
				player.setActiveHand(hand);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, heldItem);
			}
		}
		else {
			
			player.setActiveHand(hand);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}    
	}
	
	
	// different unlocalized names
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		
		// initial states
		if (stack.getMetadata() == 0) { return "item." + SchopCraft.RESOURCE_PREFIX + "empty_canteen"; }
		else if (stack.getMetadata() == 1) { return "item." + SchopCraft.RESOURCE_PREFIX + "fresh_water_canteen"; }
		else if (stack.getMetadata() == 2) { return "item." + SchopCraft.RESOURCE_PREFIX + "dirty_water_canteen"; }
		else if (stack.getMetadata() == 3) { return "item." + SchopCraft.RESOURCE_PREFIX + "salt_water_canteen"; }
		else if (stack.getMetadata() == 4) { return "item." + SchopCraft.RESOURCE_PREFIX + "filtered_water_canteen"; }
		else if (stack.getMetadata() == 5) { return "item." + SchopCraft.RESOURCE_PREFIX + "cold_water_canteen"; }
		
		else if (stack.getItemDamage() == 67 || stack.getItemDamage() == 34) { return "item." + SchopCraft.RESOURCE_PREFIX + "fresh_water_canteen"; }
		else if (stack.getItemDamage() == 68 || stack.getItemDamage() == 35) { return "item." + SchopCraft.RESOURCE_PREFIX + "dirty_water_canteen"; }
		else if (stack.getItemDamage() == 69 || stack.getItemDamage() == 36) { return "item." + SchopCraft.RESOURCE_PREFIX + "salt_water_canteen"; }
		else if (stack.getItemDamage() == 70 || stack.getItemDamage() == 37) { return "item." + SchopCraft.RESOURCE_PREFIX + "filtered_water_canteen"; }
		else if (stack.getItemDamage() == 71 || stack.getItemDamage() == 38) { return "item." + SchopCraft.RESOURCE_PREFIX + "cold_water_canteen"; }
		else { return "item." + SchopCraft.RESOURCE_PREFIX + "empty_canteen"; }
	}
	
	// create sub items
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		
		if (this.isInCreativeTab(tab)) {
            
			for (int i = 0; i < 6; i++) {
				
                items.add(new ItemStack(this, 1, i));
            }
        }
	}
	
	// makes it a drink
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		
		return EnumAction.DRINK;
	}
	
	// how long it takes to drink it (how long to show the animation)
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		
		return 32;
	}
	
	// figure out when to show durability
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		
		if (stack.getItemDamage() < 6) {
			
			return false;
		}
		else {
			
			return true;
		}
	}
}
