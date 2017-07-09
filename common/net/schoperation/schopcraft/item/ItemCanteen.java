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
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.biome.BiomeSwamp;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.schoperation.schopcraft.SchopCraft;
import net.schoperation.schopcraft.cap.thirst.IThirst;
import net.schoperation.schopcraft.cap.thirst.ThirstProvider;
import net.schoperation.schopcraft.packet.SchopPackets;
import net.schoperation.schopcraft.packet.ThirstPacket;
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
		setMaxDamage(103);
		setNoRepair();
		setHasSubtypes(true);
		
	}
	
	// drinking from the canteen
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
		
		if (entityLiving instanceof EntityPlayerMP && !world.isRemote) {
			
			// basic variables
			EntityPlayerMP player = (EntityPlayerMP) entityLiving;
			String canteenType = stack.getUnlocalizedName();
			String uuid = player.getCachedUniqueIdString();
			
			IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
			
			// determine type of water, and quench thirst accordingly
			// fresh water
			if (canteenType.equals("item." + SchopCraft.RESOURCE_PREFIX + "fresh_water_canteen")) {
				
				thirst.increase(20f);
			}
			
			// dirty water
			else if (canteenType.equals("item." + SchopCraft.RESOURCE_PREFIX + "dirty_water_canteen")) {
				
				thirst.increase(15f);
				SchopServerEffects.affectPlayer(uuid, "poison", 48, 1, false, false);
			}
			
			// salt water
			else {
				
				thirst.decrease(20f);
			}
			
			// decrease durability
			stack.damageItem(33, player);
			
			// send thirst packet to client to render correctly
			IMessage msg = new ThirstPacket.ThirstMessage(player.getCachedUniqueIdString(), thirst.getThirst());
			SchopPackets.net.sendTo(msg, (EntityPlayerMP)player);
		}
		
		return stack;
	}
	
	
	// right click to get water. If the player is holding an empty canteen, it will attempt to fill it up with water.
	// if holding a canteen partially filled with water, it will refill it with water.
	// if holding a full canteen with the same type of water it's going to get, it'll empty itself, becoming an empty canteen.
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		
		// server-side. unfortunately player.rayTrace is client-only. Will replace with server-side equivalent later. Stay in bed kids.
		if (!world.isRemote) {
			
			// ray trace result
			RayTraceResult raytrace = player.rayTrace(2, 1.0f);
			
			// held item
			ItemStack heldItem = player.getHeldItem(hand);
			
			// Did they right click on air/liquid?
			if (raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.MISS) {
				
				// position of the raytrace
				BlockPos pos = raytrace.getBlockPos();
				
				// was this water?
				if (world.getBlockState(pos).getMaterial() == Material.WATER) {
					
					// now figure out the biome of the block
					Biome biome = world.getBiome(pos);
					
					// Ocean biome
					if (biome instanceof BiomeOcean) {
						
						// what is the player holding?
						// empty canteen
						if (heldItem.getUnlocalizedName().equals("item." + SchopCraft.RESOURCE_PREFIX + "empty_canteen")) { heldItem.setItemDamage(3); }
						
						// already full of salt water
						else if (heldItem.getItemDamage() == 3) { heldItem.setItemDamage(0); }
						
						// otherwise just refill it with salt water
						else { heldItem.setItemDamage(3); }
					}
					
					// Swamp biome
					else if (biome instanceof BiomeSwamp) {
						
						// empty canteen
						if (heldItem.getUnlocalizedName().equals("item." + SchopCraft.RESOURCE_PREFIX + "empty_canteen")) { heldItem.setItemDamage(2); }
					
						// already full of dirty water
						else if (heldItem.getItemDamage() == 2) { heldItem.setItemDamage(0); }
						
						// otherwise just refill it with dirty water
						else { heldItem.setItemDamage(2); }
					}
					
					// other biomes (may add snow biome soon for temp... maybe cold water canteen?
					else {
						
						// random chance to give dirty water opposed to fresh water.
						double randomNum = Math.random();
						
						// empty canteen
						if (heldItem.getUnlocalizedName().equals("item." + SchopCraft.RESOURCE_PREFIX + "empty_canteen")) { if (randomNum < 0.40) { heldItem.setItemDamage(2); } else { heldItem.setItemDamage(1); } }
						
						// full canteen (of any type)
						else if (heldItem.getItemDamage() == 1 || heldItem.getItemDamage() == 2 || heldItem.getItemDamage() == 3) { heldItem.setItemDamage(0); }
						
						// otherwise fill it with dirty water or fresh water
						else { if (randomNum < 0.40) { heldItem.setItemDamage(2); } else { heldItem.setItemDamage(1); } }
						
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
		
		else if (stack.getItemDamage() == 67 || stack.getItemDamage() == 34) { return "item." + SchopCraft.RESOURCE_PREFIX + "fresh_water_canteen"; }
		else if (stack.getItemDamage() == 68 || stack.getItemDamage() == 35) { return "item." + SchopCraft.RESOURCE_PREFIX + "dirty_water_canteen"; }
		else if (stack.getItemDamage() == 69 || stack.getItemDamage() == 36) { return "item." + SchopCraft.RESOURCE_PREFIX + "salt_water_canteen"; }
		else { return "item." + SchopCraft.RESOURCE_PREFIX + "empty_canteen"; }
	}
	
	// create sub items
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		
		if (this.isInCreativeTab(tab)) {
            
			for (int i = 0; i < 4; i++) {
				
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
		
		if (stack.getItemDamage() < 4) {
			
			return false;
		}
		else {
			
			return true;
		}
	}
}
