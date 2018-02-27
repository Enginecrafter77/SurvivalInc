package net.schoperation.schopcraft.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockCauldron;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
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
import net.schoperation.schopcraft.config.SchopConfig;
import net.schoperation.schopcraft.util.SchopServerEffects;
import net.schoperation.schopcraft.util.SchopServerParticles;
import net.schoperation.schopcraft.util.SchopServerSounds;

public class ItemCanteen extends Item {
	
	/*
	 * A simple canteen for all your thirst needs. Ends up being full of code. Go figure.
	 */
	
	public ItemCanteen() {
		
		// Set registry name.
		setRegistryName(new ResourceLocation(SchopCraft.MOD_ID, "canteen"));
		
		// Basic properties.
		setMaxStackSize(1);
		setCreativeTab(SchopCraft.mainTab);
		setNoRepair();
		setHasSubtypes(true);
	}
	
	// Number of sips.
	private final int canteenSips = 3;
	
	// Durability
	private final int canteenDurability = 100;
	
	// Drinking from the canteen.
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
		
		// Server-side.
		if (entityLiving instanceof EntityPlayerMP && !world.isRemote) {
			
			// Basic variables.
			EntityPlayerMP player = (EntityPlayerMP) entityLiving;
			String uuid = player.getCachedUniqueIdString();
			int canteenType = stack.getMetadata();
			
			// Capabilities
			IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
			ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
			ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
			
			// Determine type of water, and quench thirst accordingly.
			// Fresh water
			if (canteenType == 1) {
				
				thirst.increase(20f);
				temperature.decrease(10f);
				sanity.increase(10f);
			}
			
			// Dirty water
			else if (canteenType == 2) {
				
				thirst.increase(10f);
				sanity.decrease(5f);
				SchopServerEffects.affectPlayer(uuid, "poison", 50, 2, false, false);
			}
			
			// Salt water
			else if (canteenType == 3) {
				
				thirst.decrease(20f);
				sanity.decrease(15f);
			}
			
			// Filtered water
			else if (canteenType == 4) {
				
				thirst.increase(15f);
				sanity.increase(5f);
				double randChanceOfPoison = Math.random();
				if (randChanceOfPoison < 0.25) { SchopServerEffects.affectPlayer(uuid, "poison", 50, 0, false, false); }
			}
			
			// Cold water
			else if (canteenType == 5) {
				
				thirst.increase(15f);
				temperature.decrease(15f);
				sanity.increase(5f);
				double randChanceOfPoison = Math.random();
				if (randChanceOfPoison < 0.15) { SchopServerEffects.affectPlayer(uuid, "poison", 50, 0, false, false); }
			}
			
			// Decrease durability and set sips.
			NBTTagCompound nbt = stack.getTagCompound();
			
			if (nbt != null) {
				
				if (nbt.getInteger("sips") > 0) {
					
					nbt.setInteger("sips", nbt.getInteger("sips") - 1);
				}
				
				else {
					
					nbt.setInteger("sips", nbt.getInteger("sips") - 1);
					stack.setItemDamage(0);
				}
				
				nbt.setInteger("durability", nbt.getInteger("durability") - 1);
				stack.setTagCompound(nbt);
			}
		}
		
		return stack;
	}
	
	
	// Right click to get water. If the player is holding an empty canteen, it will attempt to fill it up with water.
	// If holding a canteen partially filled with water, it will refill it with water.
	// If holding a full canteen with the same type of water it's going to get, it'll empty itself, becoming an empty canteen.
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		
		// Server-side.
		if (!world.isRemote) {
			
			// Ray trace result.
			// First, some "boosts" to the vector.
			double vecX = 0;
			double vecZ = 0;
			if (player.getLookVec().x < 0) { vecX = -0.5; }
			else if (player.getLookVec().x > 0) { vecX = 0.5; }
			if (player.getLookVec().z < 0) { vecZ = -0.5; }
			else if (player.getLookVec().z > 0) { vecZ = 0.5; }
			
			// Now the actual raytrace.
			RayTraceResult raytrace = world.rayTraceBlocks(player.getPositionEyes(1.0f), player.getPositionEyes(1.0f).add(player.getLookVec().addVector(vecX, -1, vecZ)), true);
			
			// Held item.
			ItemStack heldItem = player.getHeldItem(hand);
			
			// NBT Tag of canteen (which shouldn't be null by now).
			NBTTagCompound nbt = heldItem.getTagCompound();
			int sips = nbt.getInteger("sips");
			
			// Did they right click on a block?
			if (raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK) {
				
				// Block position of the raytrace.
				BlockPos pos = raytrace.getBlockPos();
				
				// Was this water?
				if (world.getBlockState(pos).getBlock() == Blocks.WATER || world.getBlockState(pos).getBlock() == Blocks.FLOWING_WATER) {
					
					// Now figure out the biome of the block.
					Biome biome = world.getBiome(pos);
					
					// Ocean biome
					if (biome instanceof BiomeOcean || biome instanceof BiomeBeach) {
						
						// What is the player holding?
						// Empty canteen
						if (heldItem.getMetadata() == 0) { heldItem.setItemDamage(3); sips = canteenSips; }
						
						// Full canteen (of any type)
						else if (sips == canteenSips) { heldItem.setItemDamage(0); sips = 0; }
						
						// Otherwise just refill it with salt water
						else { heldItem.setItemDamage(3); sips = canteenSips; }
					}
					
					// Swamp biome
					else if (biome instanceof BiomeSwamp) {
						
						// Empty canteen
						if (heldItem.getMetadata() == 0) { heldItem.setItemDamage(2); sips = canteenSips; }
					
						// Full canteen (of any type)
						else if (sips == canteenSips) { heldItem.setItemDamage(0); sips = 0; }
						
						// Otherwise just refill it with dirty water
						else { heldItem.setItemDamage(2); sips = canteenSips; }
					}
					
					// snow biome
					else if (biome instanceof BiomeSnow) {
						
						// Random chance to give cold water opposed to dirty water.
						double randomNum = Math.random();
						
						// Empty canteen
						if (heldItem.getMetadata() == 0) { if (randomNum < 0.80) { heldItem.setItemDamage(2); } else { heldItem.setItemDamage(5); } sips = canteenSips; }
						
						// Full canteen (of any type)
						else if (sips == canteenSips) { heldItem.setItemDamage(0); sips = 0; }
						
						// Otherwise fill it with dirty water or cold water, according to what's already in it.
						else if (heldItem.getMetadata() == 2 || heldItem.getMetadata() == 4) { heldItem.setItemDamage(2); sips = canteenSips; }
						else { if (randomNum < 0.80) { heldItem.setItemDamage(2); } else { heldItem.setItemDamage(5); } sips = canteenSips; }
					}
					
					// Other biomes
					else {
						
						// Random chance to give fresh water opposed to dirty water.
						double randomNum = Math.random();
						
						// Empty canteen
						if (heldItem.getMetadata() == 0) { if (randomNum < 0.98) { heldItem.setItemDamage(2); } else { heldItem.setItemDamage(1); } sips = canteenSips; }
						
						// Full canteen (of any type)
						else if (sips == canteenSips) { heldItem.setItemDamage(0); sips = 0; }
						
						// Otherwise fill it with dirty water or fresh water, according to what's already in it.
						else if (heldItem.getMetadata() == 2 || heldItem.getMetadata() == 4) { heldItem.setItemDamage(2); sips = canteenSips; }
						else { if (randomNum < 0.98) { heldItem.setItemDamage(2); } else { heldItem.setItemDamage(1); } sips = canteenSips; }
						
					}
					
					// Play sounds and particles directly.
					SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "DrinkWaterParticles", pos.getX(), pos.getY(), pos.getZ());
					SchopServerSounds.playSound(player.getCachedUniqueIdString(), "WaterSound", pos.getX(), pos.getY(), pos.getZ());
					
					// Set NBT properly.
					nbt.setInteger("sips", sips);
					nbt.setInteger("durability", nbt.getInteger("durability") - 1);
					heldItem.setTagCompound(nbt);
					
					return new ActionResult<ItemStack>(EnumActionResult.PASS, heldItem);
				}
				
				// ======================================================
				// A cauldron will act as a rain collector in this mod.
				// ======================================================
				else if (world.getBlockState(pos).getBlock() == Blocks.CAULDRON) {
					
					// The amount of water in the cauldron.
					int cauldronLevel = world.getBlockState(pos).getValue(BlockCauldron.LEVEL);
					
					// The cauldron itself.
					BlockCauldron cauldron = (BlockCauldron) world.getBlockState(pos).getBlock();
					
					// Only allow it to be filled when the cauldron...
					// 1. Has at least one level of water.
					// 2. It's raining and the cauldron can see the sky.
					if (cauldronLevel > 0 && world.isRainingAt(new BlockPos(pos.getX(), pos.getY()+1, pos.getZ()))) {
						
						// Empty canteen... give it some nice, fresh rain water.
						if (heldItem.getMetadata() == 0) {
							
							heldItem.setItemDamage(1);
							sips = canteenSips;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel-1);
						}
						
						// Full canteen (of any type)
						else if (sips == canteenSips) { 
							
							heldItem.setItemDamage(0);
							sips = 0;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel+1);
						}
						
						// Fresh water canteen (not full)
						else if (heldItem.getMetadata() == 1) {
							
							heldItem.setItemDamage(1);
							sips = canteenSips;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel-1);
						}
						
						// Cold water (not full)
						else if (heldItem.getMetadata() == 5) {
							
							heldItem.setItemDamage(1);
							sips = canteenSips;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel-1);
						}
						
						// Otherwise do dirty water
						else {
							
							heldItem.setItemDamage(2);
							sips = canteenSips;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel-1);
						}
						
						// Play sounds and particles directly.
						SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "DrinkWaterParticles", pos.getX(), pos.getY(), pos.getZ());
						SchopServerSounds.playSound(player.getCachedUniqueIdString(), "WaterSound", pos.getX(), pos.getY(), pos.getZ());
					}
					
					// If it's not raining... but there's water...
					else if (cauldronLevel > 0) {
						
						// Empty canteen... dirty water
						if (heldItem.getMetadata() == 0) {
							
							heldItem.setItemDamage(2);
							sips = canteenSips;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel-1);
						}
						
						// Full canteen (of any type)
						else if (sips == canteenSips) { 
							
							heldItem.setItemDamage(0);
							sips = 0;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel+1);
						}
						
						// Otherwise do dirty water or salt water
						else {
							
							double saltOrDirt = Math.random();
							if (saltOrDirt < 0.50) { heldItem.setItemDamage(3); }
							else { heldItem.setItemDamage(2); }
							sips = canteenSips;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel-1);
						}
						
						// Play sounds and particles directly.
						SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "DrinkWaterParticles", pos.getX(), pos.getY(), pos.getZ());
						SchopServerSounds.playSound(player.getCachedUniqueIdString(), "WaterSound", pos.getX(), pos.getY(), pos.getZ());
					}
					
					// If the cauldron is empty...
					else {
						
						// Anything but an empty canteen
						if (heldItem.getMetadata() != 0 && sips == canteenSips) {
							
							heldItem.setItemDamage(0);
							sips = 0;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel+1);
							
							// Play sounds and particles directly.
							SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "DrinkWaterParticles", pos.getX(), pos.getY(), pos.getZ());
							SchopServerSounds.playSound(player.getCachedUniqueIdString(), "WaterSound", pos.getX(), pos.getY(), pos.getZ());
						}
					}
					
					// Set NBT properly.
					nbt.setInteger("sips", sips);
					nbt.setInteger("durability", nbt.getInteger("durability") - 1);
					heldItem.setTagCompound(nbt);
					
					return new ActionResult<ItemStack>(EnumActionResult.PASS, heldItem);
				}
				
				// ============================ END OF CAULDRON CODE ======================================================
				
				// All of this crap is to ensure that the player can drink from the canteen if it isn't empty.
				else if (heldItem.getMetadata() == 0) {
					
					return new ActionResult<ItemStack>(EnumActionResult.PASS, heldItem);
				}
				else {
					
					player.setActiveHand(hand);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, heldItem);
				}
			}
			else if (heldItem.getMetadata() == 0) {
				
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
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
	
		if (stack.getMetadata() == 0) { return "item." + SchopCraft.RESOURCE_PREFIX + "empty_canteen"; }
		else if (stack.getMetadata() == 1) { return "item." + SchopCraft.RESOURCE_PREFIX + "fresh_water_canteen"; }
		else if (stack.getMetadata() == 2) { return "item." + SchopCraft.RESOURCE_PREFIX + "dirty_water_canteen"; }
		else if (stack.getMetadata() == 3) { return "item." + SchopCraft.RESOURCE_PREFIX + "salt_water_canteen"; }
		else if (stack.getMetadata() == 4) { return "item." + SchopCraft.RESOURCE_PREFIX + "filtered_water_canteen"; }
		else if (stack.getMetadata() == 5) { return "item." + SchopCraft.RESOURCE_PREFIX + "cold_water_canteen"; }
		else { stack.setItemDamage(0); return "item." + SchopCraft.RESOURCE_PREFIX + "empty_canteen";  }
	}
	
	// When crafted, give this item an NBT tag to store its number of sips available, along with durability.
	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		
		if (!stack.hasTagCompound()) {
			
			// Making a new NBT tag compound.
			NBTTagCompound nbt = new NBTTagCompound();
			
			if (stack.getMetadata() == 0) {
				
				nbt.setInteger("sips", 0);
			}
			
			else {
				
				nbt.setInteger("sips", canteenSips);
			}
			
			// Add durability tag.
			nbt.setInteger("durability", canteenDurability);
			stack.setTagCompound(nbt);
		}
    }
	
	// This is to ensure the canteen always has NBT.
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		
		if (!stack.hasTagCompound()) {
			
			// Making a new NBT tag compound.
			NBTTagCompound nbt = new NBTTagCompound();
			
			if (stack.getMetadata() == 0) {
				
				nbt.setInteger("sips", 0);
			}
			
			else {
				
				nbt.setInteger("sips", canteenSips);
			}
			
			// Add durability tag.
			nbt.setInteger("durability", canteenDurability);
			stack.setTagCompound(nbt);
		}
		
		else {
			
			// If there are zero sips left, make it an empty canteen.
			NBTTagCompound nbt = stack.getTagCompound();
			
			if (nbt.getInteger("sips") <= 0) {
				
				stack.setItemDamage(0);
			}
			
			// If durability is 0, destroy the canteen.
			if (nbt.getInteger("durability") <= 0) {
				
				stack.shrink(1);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		
		// NBT Tag and percentLeft float to determine color.
		NBTTagCompound nbt = stack.getTagCompound();
		float percentLeft;
		
		if (nbt != null) {
			
			percentLeft = (int) Math.round((nbt.getInteger("sips") * 100) / canteenSips);
			
			if (percentLeft == 100 || percentLeft >= 80) { 
				
				tooltip.add(TextFormatting.GREEN + Integer.toString(nbt.getInteger("sips")) + " Sips Left"); 
			}
			
			else if ((percentLeft <= 80 || percentLeft >= 20) && nbt.getInteger ("sips") > 1) { 
				
				tooltip.add(TextFormatting.YELLOW + Integer.toString(nbt.getInteger("sips")) + " Sips Left"); 
			}
			
			else if (nbt.getInteger("sips") == 1) {
				
				tooltip.add(TextFormatting.RED + Integer.toString(nbt.getInteger("sips")) + " Sip Left"); 
			}
			
			else if (percentLeft <= 20 || percentLeft >= 0) { 
				
				tooltip.add(TextFormatting.RED + Integer.toString(nbt.getInteger("sips")) + " Sips Left"); 
			}
			
			else { 
				
				tooltip.add(TextFormatting.WHITE + Integer.toString(nbt.getInteger("sips")) + " Sips Left"); 
			}
			
			// Durability info. In an if-statement just in case.
			if (nbt.hasKey("durability")) {
				
				tooltip.add(TextFormatting.GOLD + Integer.toString(nbt.getInteger("durability")) + " Durability");
			}	
		}
		
		else {
			
			tooltip.add(TextFormatting.BLUE + "New Canteen");
		}
	}
	
	// Create sub items
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		
		if (this.isInCreativeTab(tab)) {
            
			for (int i = 0; i < 6; i++) {
				
                items.add(new ItemStack(this, 1, i));
            }
        }
	}
	
	// Makes it a drink.
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		
		return EnumAction.DRINK;
	}
	
	// How long it takes to drink it (how long to show the animation).
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		
		return 32;
	}
	
	// Figure out when to show durability.
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		
		// NBT
		NBTTagCompound nbt = stack.getTagCompound();
		
		if (nbt != null) {
			
			// What is configured?
			if (SchopConfig.client.showSipsInDurabilityBar) {
				
				// Show sips
				if (nbt.getInteger("sips") > 0) {
					
					return true;
				}
				
				else {
					
					return false;
				}
			}
			
			else {
				
				// Show durability
				if (nbt.getInteger("durability") >= 0) {
					
					return true;
				}
				
				else {
					
					return false;
				}
			}
		}
		
		else {
			
			return false;
		}
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		
		// NBT Tag and necessary variables.
		NBTTagCompound nbt = stack.getTagCompound();
		int percentLeft = 0;
		double durabilityToShow = 0;
		
		if (nbt != null) {
			
			if (SchopConfig.client.showSipsInDurabilityBar) {
				
				percentLeft = Math.round((nbt.getInteger("sips") * 100) / canteenSips);
				durabilityToShow = 1.0 - ((double) percentLeft / 100);
			}
			
			else {
				
				percentLeft = Math.round((nbt.getInteger("durability") * 100) / canteenDurability);
				durabilityToShow = 1.0 - ((double) percentLeft / 100);
			}
			
			return durabilityToShow;
		}
		
		else {
			
			return 1;
		}
    }
}