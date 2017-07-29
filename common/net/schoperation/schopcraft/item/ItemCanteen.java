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
import net.schoperation.schopcraft.config.ModConfig;
import net.schoperation.schopcraft.util.SchopServerEffects;
import net.schoperation.schopcraft.util.SchopServerParticles;
import net.schoperation.schopcraft.util.SchopServerSounds;

public class ItemCanteen extends Item {
	
	/*
	 * A simple canteen for all your thirst needs. Ends up being full of code. Go figure.
	 */
	
	public ItemCanteen() {
		
		// setting registry name and crap
		setRegistryName(new ResourceLocation(SchopCraft.MOD_ID, "canteen"));
		
		// properties
		setMaxStackSize(1);
		setCreativeTab(SchopCraft.mainTab);
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
			String uuid = player.getCachedUniqueIdString();
			int canteenType = stack.getMetadata();
			
			// capabilities
			IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
			ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
			ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
			
			// Determine type of water, and quench thirst accordingly
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
			
			// salt water
			else if (canteenType == 3) {
				
				thirst.decrease(20f);
				sanity.decrease(15f);
			}
			
			// filtered water
			else if (canteenType == 4) {
				
				thirst.increase(15f);
				sanity.increase(5f);
				double randChanceOfPoison = Math.random();
				if (randChanceOfPoison < 0.25) { SchopServerEffects.affectPlayer(uuid, "poison", 50, 0, false, false); }
			}
			
			// cold water
			else if (canteenType == 5) {
				
				thirst.increase(15f);
				temperature.decrease(15f);
				sanity.increase(5f);
				double randChanceOfPoison = Math.random();
				if (randChanceOfPoison < 0.15) { SchopServerEffects.affectPlayer(uuid, "poison", 50, 0, false, false); }
			}
			
			// decrease durability
			NBTTagCompound nbt = stack.getTagCompound();
			
			if (nbt != null) {
				
				if (nbt.getInteger("sips") > 0) {
					
					nbt.setInteger("sips", nbt.getInteger("sips") - 1);
				}
				else {
					
					nbt.setInteger("sips", nbt.getInteger("sips") - 1);
					stack.setItemDamage(0);
				}
			}
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
			
			// NBT Tag of canteen (which it should have by now)
			NBTTagCompound nbt = heldItem.getTagCompound();
			int sips = nbt.getInteger("sips");
			
			// Did they right click on a block?
			if (raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK) {
				
				// position of the raytrace
				BlockPos pos = raytrace.getBlockPos();
				
				// was this water?
				if (world.getBlockState(pos).getBlock() == Blocks.WATER || world.getBlockState(pos).getBlock() == Blocks.FLOWING_WATER) {
					
					// now figure out the biome of the block
					Biome biome = world.getBiome(pos);
					
					// Ocean biome
					if (biome instanceof BiomeOcean || biome instanceof BiomeBeach) {
						
						// what is the player holding?
						// empty canteen
						if (heldItem.getMetadata() == 0) { heldItem.setItemDamage(3); sips = ModConfig.canteenSips; }
						
						// full canteen (of any type)
						else if (sips == ModConfig.canteenSips) { heldItem.setItemDamage(0); sips = 0; }
						
						// otherwise just refill it with salt water
						else { heldItem.setItemDamage(3); sips = ModConfig.canteenSips; }
					}
					
					// Swamp biome
					else if (biome instanceof BiomeSwamp) {
						
						// empty canteen
						if (heldItem.getMetadata() == 0) { heldItem.setItemDamage(2); sips = ModConfig.canteenSips; }
					
						// full canteen (of any type)
						else if (sips == ModConfig.canteenSips) { heldItem.setItemDamage(0); sips = 0; }
						
						// otherwise just refill it with dirty water
						else { heldItem.setItemDamage(2); sips = ModConfig.canteenSips; }
					}
					
					// snow biome
					else if (biome instanceof BiomeSnow) {
						
						// random chance to give cold water opposed to dirty water.
						double randomNum = Math.random();
						
						// empty canteen
						if (heldItem.getMetadata() == 0) { if (randomNum < 0.80) { heldItem.setItemDamage(2); } else { heldItem.setItemDamage(5); } sips = ModConfig.canteenSips; }
						
						// full canteen (of any type)
						else if (sips == ModConfig.canteenSips) { heldItem.setItemDamage(0); sips = 0; }
						
						// otherwise fill it with dirty water or cold water, according to what's already in it
						else if (heldItem.getMetadata() == 2 || heldItem.getMetadata() == 4) { heldItem.setItemDamage(2); sips = ModConfig.canteenSips; }
						else { if (randomNum < 0.80) { heldItem.setItemDamage(2); } else { heldItem.setItemDamage(5); } sips = ModConfig.canteenSips; }
					}
					
					// other biomes
					else {
						
						// random chance to give fresh water opposed to dirty water.
						double randomNum = Math.random();
						
						// empty canteen
						if (heldItem.getMetadata() == 0) { if (randomNum < 0.98) { heldItem.setItemDamage(2); } else { heldItem.setItemDamage(1); } sips = ModConfig.canteenSips; }
						
						// full canteen (of any type)
						else if (sips == ModConfig.canteenSips) { heldItem.setItemDamage(0); sips = 0; }
						
						// otherwise fill it with dirty water or fresh water, according to what's already in it
						else if (heldItem.getMetadata() == 2 || heldItem.getMetadata() == 4) { heldItem.setItemDamage(2); sips = ModConfig.canteenSips; }
						else { if (randomNum < 0.98) { heldItem.setItemDamage(2); } else { heldItem.setItemDamage(1); } sips = ModConfig.canteenSips; }
						
					}
					
					// play sounds and particles directly (as this is already server-side)
					SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "DrinkWaterParticles", pos.getX(), pos.getY(), pos.getZ());
					SchopServerSounds.playSound(player.getCachedUniqueIdString(), "WaterSound", pos.getX(), pos.getY(), pos.getZ());
					
					// set nbt properly
					nbt.setInteger("sips", sips);
					heldItem.setTagCompound(nbt);
					
					return new ActionResult<ItemStack>(EnumActionResult.PASS, heldItem);
				}
				
				// a cauldron will act as a rain collector in this mod.
				// ============================ START OF CAULDRON CODE ===========================================================================
				else if (world.getBlockState(pos).getBlock() == Blocks.CAULDRON) {
					
					// the amount of water in the cauldron
					int cauldronLevel = world.getBlockState(pos).getValue(BlockCauldron.LEVEL);
					
					// the block itself
					BlockCauldron cauldron = (BlockCauldron) world.getBlockState(pos).getBlock();
					
					// only allow it to be filled when the cauldron...
					// 1. Has at least one level of water
					// 2. It's raining and the cauldron can see the sky.
					if (cauldronLevel > 0 && world.isRainingAt(new BlockPos(pos.getX(), pos.getY()+1, pos.getZ()))) {
						
						// empty canteen... give it some nice, fresh rain water
						if (heldItem.getMetadata() == 0) {
							
							heldItem.setItemDamage(1);
							sips = ModConfig.canteenSips;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel-1);
						}
						
						// full canteen (of any type)
						else if (sips == ModConfig.canteenSips) { 
							
							heldItem.setItemDamage(0);
							sips = 0;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel+1);
						}
						
						// fresh water canteen (not full)
						else if (heldItem.getMetadata() == 1) {
							
							heldItem.setItemDamage(1);
							sips = ModConfig.canteenSips;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel-1);
						}
						
						// cold water (not full)
						else if (heldItem.getMetadata() == 5) {
							
							heldItem.setItemDamage(1);
							sips = ModConfig.canteenSips;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel-1);
						}
						
						// otherwise do dirty water
						else {
							
							heldItem.setItemDamage(2);
							sips = ModConfig.canteenSips;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel-1);
						}
						
						// play sounds and particles directly (as this is already server-side)
						SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "DrinkWaterParticles", pos.getX(), pos.getY(), pos.getZ());
						SchopServerSounds.playSound(player.getCachedUniqueIdString(), "WaterSound", pos.getX(), pos.getY(), pos.getZ());
					}
					
					// if it's not raining... but there's water...
					else if (cauldronLevel > 0) {
						
						// empty canteen... dirty water
						if (heldItem.getMetadata() == 0) {
							
							heldItem.setItemDamage(2);
							sips = ModConfig.canteenSips;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel-1);
						}
						
						// full canteen (of any type)
						else if (sips == ModConfig.canteenSips) { 
							
							heldItem.setItemDamage(0);
							sips = 0;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel+1);
						}
						
						// otherwise do dirty water or salt water
						else {
							
							double saltOrDirt = Math.random();
							if (saltOrDirt < 0.50) { heldItem.setItemDamage(3); }
							else { heldItem.setItemDamage(2); }
							sips = ModConfig.canteenSips;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel-1);
						}
						
						// play sounds and particles directly (as this is already server-side)
						SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "DrinkWaterParticles", pos.getX(), pos.getY(), pos.getZ());
						SchopServerSounds.playSound(player.getCachedUniqueIdString(), "WaterSound", pos.getX(), pos.getY(), pos.getZ());
					}
					
					// if the cauldron is empty...
					else {
						
						// anything but an empty canteen
						if (heldItem.getMetadata() != 0) {
							
							heldItem.setItemDamage(0);
							sips = 0;
							cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel+1);
							
							// play sounds and particles directly (as this is already server-side)
							SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "DrinkWaterParticles", pos.getX(), pos.getY(), pos.getZ());
							SchopServerSounds.playSound(player.getCachedUniqueIdString(), "WaterSound", pos.getX(), pos.getY(), pos.getZ());
						}
					}
					
					// set nbt properly
					nbt.setInteger("sips", sips);
					heldItem.setTagCompound(nbt);
					
					return new ActionResult<ItemStack>(EnumActionResult.PASS, heldItem);
				}
				
				// ============================ END OF CAULDRON CODE ======================================================
				
				// all of this crap is to ensure that the player can drink from the canteen if it isn't empty.
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
	
	
	// different unlocalized names
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
	
	// When crafted, give this item an NBT tag to store its number of sips available
	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		
		if (!stack.hasTagCompound()) {
			
			// Making a new NBT tag with a # of sips, and tacking it onto the canteen
			NBTTagCompound nbt = new NBTTagCompound();
			
			if (stack.getMetadata() == 0) {
				
				nbt.setInteger("sips", 0);
			}
			else {
				
				nbt.setInteger("sips", ModConfig.canteenSips);
			}
			
			stack.setTagCompound(nbt);
		}
    }
	// This is to ensure the canteen always has NBT
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		
		if (!stack.hasTagCompound()) {
			
			// Making a new NBT tag with a # of sips, and tacking it onto the canteen
			NBTTagCompound nbt = new NBTTagCompound();
			
			if (stack.getMetadata() == 0) {
				
				nbt.setInteger("sips", 0);
			}
			
			else {
				
				nbt.setInteger("sips", ModConfig.canteenSips);
			}
			
			stack.setTagCompound(nbt);
		}
		
		else {
			
			// If there are zero sips left, make it an empty canteen
			NBTTagCompound nbt = stack.getTagCompound();
			
			if (nbt.getInteger("sips") <= 0) {
				
				stack.setItemDamage(0);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		
		NBTTagCompound nbt = stack.getTagCompound();
		float percentLeft;
		
		if (nbt != null) {
			
			percentLeft = (int) Math.round((nbt.getInteger("sips") * 100) / ModConfig.canteenSips);
			
			if (percentLeft == 100 || percentLeft >= 80) { 
				
				tooltip.add(TextFormatting.GREEN + Integer.toString(nbt.getInteger("sips")) + " Sips Left"); 
			}
			
			else if ((percentLeft <= 80 || percentLeft >= 20) && nbt.getInteger ("sips") > 1) { 
				
				tooltip.add(TextFormatting.YELLOW + Integer.toString(nbt.getInteger("sips")) + " Sips Left"); 
			}
			
			else if (percentLeft <= 20 || percentLeft >= 0 || nbt.getInteger ("sips") == 1) { 
				
				tooltip.add(TextFormatting.RED + Integer.toString(nbt.getInteger("sips")) + " Sips Left"); 
			}
			
			else { 
				
				tooltip.add(TextFormatting.WHITE + Integer.toString(nbt.getInteger("sips")) + " Sips Left"); 
			}
		}
		
		else {
			
			tooltip.add(TextFormatting.BLUE + "New Canteen");
		}
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
		
		// get nbt
		NBTTagCompound nbt = stack.getTagCompound();
		
		if (nbt != null) {
			
			if (nbt.getInteger("sips") > 0) {
				
				return true;
			}
			else {
				
				return false;
			}
		}
		else {
			
			return false;
		}
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		
		// NBT Tag
		NBTTagCompound nbt = stack.getTagCompound();
		double percentLeft;
		
		if (nbt != null) {
			
			percentLeft = (int) Math.round((nbt.getInteger("sips") * 100) / ModConfig.canteenSips);
			percentLeft = 1.0 - ((double) percentLeft / 100);
			return percentLeft;
		}
		
		else {
			
			return 1;
		}
    }
}
