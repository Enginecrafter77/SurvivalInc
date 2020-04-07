package enginecrafter77.survivalinc.item;

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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.cap.wetness.IWetness;
import enginecrafter77.survivalinc.cap.wetness.WetnessProvider;
import enginecrafter77.survivalinc.util.ProximityDetect;
import enginecrafter77.survivalinc.util.SchopServerParticles;
import enginecrafter77.survivalinc.util.SchopServerSounds;

import java.util.List;

public class ItemTowel extends Item {

	/*
	 * A simple towel used to dry one's self or drench one's self. Whichever
	 * fits the situation. Also full of code. What the crap.
	 */

	public ItemTowel()
	{

		// Set registry name.
		setRegistryName(new ResourceLocation(SurvivalInc.MOD_ID, "towel"));

		// Basic properties.
		setMaxStackSize(1);
		setCreativeTab(SurvivalInc.mainTab);
		setHasSubtypes(true);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving)
	{

		// Server side.
		if (!world.isRemote && entityLiving instanceof EntityPlayerMP)
		{

			// Basic variables.
			EntityPlayerMP player = (EntityPlayerMP) entityLiving;
			NBTTagCompound nbt = stack.getTagCompound();

			// Capability
			IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);

			// If they're not sneaking, dry the player off.
			if (!player.isSneaking())
			{

				// Take one wetness at a time and transfer it from the player to
				// the towel.
				// If the towel reaches 100%, stop the process.
				for (int c = 0; c < 100; c++)
				{

					if (nbt.getFloat("wetness") < 100.0f && wetness.getWetness() > 0.0f)
					{

						wetness.decrease(1f);
						nbt.setFloat("wetness", nbt.getFloat("wetness") + 1f);
						stack.setTagCompound(nbt);
					}

					else
					{

						break;
					}
				}

				// Remove durability.
				nbt.setInteger("durability", nbt.getInteger("durability") - 1);

				stack.setTagCompound(nbt);

				// Play some sound.
				SchopServerSounds.playSound(player.getCachedUniqueIdString(), "TowelDrySound", player.posX, player.posY,
						player.posZ);
			}

			// If they are, drench the player.
			else
			{

				// Do the opposite of the drying process.
				for (int c = 0; c < 100; c++)
				{

					if (nbt.getFloat("wetness") > 0.0f && wetness.getWetness() < 100.0f)
					{

						wetness.increase(1f);
						nbt.setFloat("wetness", nbt.getFloat("wetness") - 1f);
						stack.setTagCompound(nbt);
					}

					else
					{

						break;
					}
				}

				// Remove durability.
				nbt.setInteger("durability", nbt.getInteger("durability") - 1);

				stack.setTagCompound(nbt);

				// Play water splash sound.
				SchopServerSounds.playSound(player.getCachedUniqueIdString(), "WaterSound", player.posX, player.posY,
						player.posZ);
			}

		}

		return stack;
	}

	// Using the towel to get water (or try to).
	// If right clicked on water or a filled cauldron, it'll be fully drenched
	// in water.
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{

		// Server side.
		if (!world.isRemote)
		{

			// RayTrace result.
			// First, some "boosts" to the vector.
			double vecX = 0;
			double vecZ = 0;
			if (player.getLookVec().x < 0)
			{
				vecX = -0.5;
			}
			else if (player.getLookVec().x > 0)
			{
				vecX = 0.5;
			}
			if (player.getLookVec().z < 0)
			{
				vecZ = -0.5;
			}
			else if (player.getLookVec().z > 0)
			{
				vecZ = 0.5;
			}

			// Now the actual Raytrace.
			RayTraceResult raytrace = world.rayTraceBlocks(player.getPositionEyes(1.0f),
					player.getPositionEyes(1.0f).add(player.getLookVec().addVector(vecX, -1, vecZ)), true);

			// Held Item
			ItemStack heldItem = player.getHeldItem(hand);

			// NBT Tag of towel (it should have NBT data by now).
			NBTTagCompound nbt = heldItem.getTagCompound();

			// Did they right click on a block?
			if (raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK)
			{

				// Position of the raytrace.
				BlockPos pos = raytrace.getBlockPos();

				// Was this water?
				if (world.getBlockState(pos).getBlock() == Blocks.WATER
						|| world.getBlockState(pos).getBlock() == Blocks.FLOWING_WATER)
				{

					// Now drench the towel in water.
					nbt.setFloat("wetness", 100.0f);

					// Remove durability.
					nbt.setInteger("durability", nbt.getInteger("durability") - 1);

					heldItem.setTagCompound(nbt);

					// Play water splash sound.
					SchopServerSounds.playSound(player.getCachedUniqueIdString(), "WaterSound", player.posX,
							player.posY, player.posZ);

					return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
				}

				// Was this a cauldron?
				else if (world.getBlockState(pos).getBlock() == Blocks.CAULDRON)
				{

					// Cauldron block
					BlockCauldron cauldron = (BlockCauldron) world.getBlockState(pos).getBlock();

					// Amount of water in the cauldron.
					int cauldronLevel = world.getBlockState(pos).getValue(BlockCauldron.LEVEL);

					// Only drench the towel if the cauldron has water.
					if (cauldronLevel > 0)
					{

						// Drench towel in water.
						nbt.setFloat("wetness", 100.0f);

						// Remove durability.
						nbt.setInteger("durability", nbt.getInteger("durability") - 1);

						heldItem.setTagCompound(nbt);

						cauldron.setWaterLevel(world, pos, world.getBlockState(pos), cauldronLevel - 1);

						// Play water splash sound.
						SchopServerSounds.playSound(player.getCachedUniqueIdString(), "WaterSound", player.posX,
								player.posY, player.posZ);

						return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
					}

					else
					{

						player.setActiveHand(hand);
						return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
					}
				}

				else
				{

					player.setActiveHand(hand);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
				}
			}

			else
			{

				player.setActiveHand(hand);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
			}
		}

		else
		{

			player.setActiveHand(hand);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{

		if (stack.getMetadata() == 0)
		{
			return "item." + SurvivalInc.RESOURCE_PREFIX + "dry_towel";
		}
		else if (stack.getMetadata() == 1)
		{
			return "item." + SurvivalInc.RESOURCE_PREFIX + "partially_wet_towel";
		}
		else if (stack.getMetadata() == 2)
		{
			return "item." + SurvivalInc.RESOURCE_PREFIX + "wet_towel";
		}
		else
		{
			stack.setItemDamage(0);
			return "item." + SurvivalInc.RESOURCE_PREFIX + "dry_towel";
		}
	}

	// When crafted, give this item an NBT tag to store its wetness.
	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn)
	{

		if (!stack.hasTagCompound())
		{

			// Making a new NBT tag compound.
			NBTTagCompound nbt = new NBTTagCompound();

			if (stack.getMetadata() == 0)
			{

				nbt.setFloat("wetness", 0.0f);
			}

			else if (stack.getMetadata() == 1)
			{

				nbt.setFloat("wetness", 50.0f);
			}

			else
			{

				nbt.setFloat("wetness", 100.0f);
			}

			// Add durability NBT tag.
			nbt.setInteger("durability", 100);

			stack.setTagCompound(nbt);
		}
	}

	// This is treated as the main loop.
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{

		// Server-side.
		if (!worldIn.isRemote && entityIn instanceof EntityPlayer)
		{

			// Player
			EntityPlayer player = (EntityPlayer) entityIn;

			// NBT tag
			NBTTagCompound nbt = stack.getTagCompound();

			// If it somehow doesn't have an NBT tag yet, give it one.
			if (nbt == null)
			{

				// Making a new NBT tag with an amount of wetness, then tacking
				// it onto the towel.
				nbt = new NBTTagCompound();

				if (stack.getMetadata() == 0)
				{

					nbt.setFloat("wetness", 0.0f);
				}

				else if (stack.getMetadata() == 1)
				{

					nbt.setFloat("wetness", 50.0f);
				}

				else
				{

					nbt.setFloat("wetness", 100.0f);
				}

				// Add durability NBT tag.
				nbt.setInteger("durability", 100);

				stack.setTagCompound(nbt);
			}

			else
			{

				// If there is little wetness, make it a dry towel.
				if (nbt.getFloat("wetness") <= 5.0f)
				{

					stack.setItemDamage(0);
				}

				// Some wetness? Partially-wet towel.
				else if (nbt.getFloat("wetness") <= 50.0f)
				{

					stack.setItemDamage(1);
				}

				// Otherwise it's just a regular old towel.
				else
				{

					stack.setItemDamage(2);
				}

				// If it's above 100 wetness, set it back to 100.
				if (nbt.getFloat("wetness") > 100.0f)
				{

					nbt.setFloat("wetness", 100.0f);
				}

				// If it's below 0 wetness, set it back to 0.
				else if (nbt.getFloat("wetness") < 0.0f)
				{

					nbt.setFloat("wetness", 0.0f);
				}

				// If durability is 0, destroy the towel.
				if (nbt.getInteger("durability") <= 0)
				{

					stack.shrink(1);
				}
			}

			// =========================
			// Now onto the legit stuff
			// =========================

			// Spawn water splash/drip particles while held.
			if (isSelected && stack.getItemDamage() != 0)
			{

				// This is used to help spawn the particles right where the
				// player is holding the towel. Yay trig. Pretty broken, but
				// cool.
				double angleOffset = 0;
				double particlePosY = player.posY + 0.75;
				double particlePosX = (-0.5 * MathHelper.sin((float) (player.rotationYaw + angleOffset))) + player.posX;
				double particlePosZ = (0.5 * MathHelper.cos((float) (player.rotationYaw + angleOffset))) + player.posZ;

				// Wet towel only
				if (stack.getItemDamage() == 2)
				{

					SchopServerParticles.summonParticle(player.getCachedUniqueIdString(), "TowelWaterParticles",
							particlePosX, particlePosY, particlePosZ);
				}
			}

			// The towel will dry or drench depending on this crap below. Copied
			// and pasted here from WetnessModifier.class.
			// BlockPos of player
			BlockPos pos = player.getPosition();

			// Check if the player is in lava.
			if (player.isInLava())
			{

				nbt.setFloat("wetness", nbt.getFloat("wetness") - 5f);
			}
			// Check if the player is in the nether.
			else if (player.dimension == -1)
			{

				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.08f);
			}
			// Check if the player is in water, whether that be rain or water.
			else if (player.isWet())
			{

				// In water?
				if (player.isInWater())
				{

					nbt.setFloat("wetness", nbt.getFloat("wetness") + 3f);
				}

				// In rain?
				if (player.world.isRainingAt(pos))
				{

					nbt.setFloat("wetness", nbt.getFloat("wetness") + 0.015f);
				}
			}

			// Otherwise, allow for natural drying off (very slow).
			else
			{

				// Figure out the conditions of the world, then dry off
				// naturally accordingly.
				if (player.world.isDaytime() && player.world.canBlockSeeSky(pos))
				{
					nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.02f);
				}
				else
				{
					nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.01f);
				}
			}

			// ==============================================================
			// PROXIMITY DETECTION
			// ==============================================================

			// These if-statement blocks is for stuff that directly doesn't have
			// to do with water bombardment.
			if (ProximityDetect.isBlockNextToPlayer(pos.getX(), pos.getY(), pos.getZ(), Blocks.FIRE, player))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.35f);
			}
			else if (ProximityDetect.isBlockNearPlayer2(pos.getX(), pos.getY(), pos.getZ(), Blocks.FIRE, player, false))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.20f);
			}
			else if (ProximityDetect.isBlockUnderPlayer(pos.getX(), pos.getY(), pos.getZ(), Blocks.FIRE, player))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.25f);
			}
			else if (ProximityDetect.isBlockUnderPlayer2(pos.getX(), pos.getY(), pos.getZ(), Blocks.FIRE, player,
					false))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.15f);
			}
			else if (ProximityDetect.isBlockAtPlayerFace(pos.getX(), pos.getY(), pos.getZ(), Blocks.FIRE, player))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.35f);
			}
			else if (ProximityDetect.isBlockAtPlayerFace2(pos.getX(), pos.getY(), pos.getZ(), Blocks.FIRE, player,
					false))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.20f);
			}
			if (ProximityDetect.isBlockNextToPlayer(pos.getX(), pos.getY(), pos.getZ(), Blocks.LAVA, player))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.75f);
			}
			else if (ProximityDetect.isBlockNearPlayer2(pos.getX(), pos.getY(), pos.getZ(), Blocks.LAVA, player, false))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.35f);
			}
			else if (ProximityDetect.isBlockUnderPlayer(pos.getX(), pos.getY(), pos.getZ(), Blocks.LAVA, player))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.75f);
			}
			else if (ProximityDetect.isBlockUnderPlayer2(pos.getX(), pos.getY(), pos.getZ(), Blocks.LAVA, player,
					false))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.35f);
			}
			else if (ProximityDetect.isBlockNextToPlayer(pos.getX(), pos.getY(), pos.getZ(), Blocks.FLOWING_LAVA,
					player))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.75f);
			}
			else if (ProximityDetect.isBlockNearPlayer2(pos.getX(), pos.getY(), pos.getZ(), Blocks.FLOWING_LAVA, player,
					false))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.35f);
			}
			else if (ProximityDetect.isBlockUnderPlayer(pos.getX(), pos.getY(), pos.getZ(), Blocks.FLOWING_LAVA,
					player))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.75f);
			}
			else if (ProximityDetect.isBlockUnderPlayer2(pos.getX(), pos.getY(), pos.getZ(), Blocks.FLOWING_LAVA,
					player, false))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.35f);
			}
			if (ProximityDetect.isBlockNextToPlayer(pos.getX(), pos.getY(), pos.getZ(), Blocks.LIT_FURNACE, player))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.25f);
			}
			else if (ProximityDetect.isBlockNearPlayer2(pos.getX(), pos.getY(), pos.getZ(), Blocks.LIT_FURNACE, player,
					false))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.12f);
			}
			if (ProximityDetect.isBlockUnderPlayer(pos.getX(), pos.getY(), pos.getZ(), Blocks.MAGMA, player))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.25f);
			}
			else if (ProximityDetect.isBlockUnderPlayer2(pos.getX(), pos.getY(), pos.getZ(), Blocks.MAGMA, player,
					false))
			{
				nbt.setFloat("wetness", nbt.getFloat("wetness") - 0.12f);
			}

			// Set NBT tag
			stack.setTagCompound(nbt);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{

		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt != null)
		{

			// Round wetness to nearest tenth.
			double roundedWetness = (double) (Math.round(nbt.getFloat("wetness") * 10)) / 10;

			if (roundedWetness > 0)
			{

				tooltip.add(TextFormatting.AQUA + Double.toString(roundedWetness) + "% Wet");
			}

			else
			{

				tooltip.add(TextFormatting.AQUA + "Relatively Dry");
			}
		}

		else
		{

			if (stack.getItemDamage() == 0)
			{

				tooltip.add(TextFormatting.AQUA + "Relatively Dry");
			}

			else if (stack.getItemDamage() == 1)
			{

				tooltip.add(TextFormatting.AQUA + "Partially Wet");
			}

			else
			{

				tooltip.add(TextFormatting.AQUA + "Wet");
			}
		}
	}

	// Create sub items.
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{

		if (this.isInCreativeTab(tab))
		{

			for (int i = 0; i < 3; i++)
			{

				items.add(new ItemStack(this, 1, i));
			}
		}
	}

	// Add animation for using towel.
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{

		return EnumAction.BOW;
	}

	// How long it takes to use it (how long to show the animation).
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{

		return 20;
	}

	// Show durability?
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{

		// Get durability (NBT durability).
		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt != null)
		{

			if (nbt.getInteger("durability") < 100)
			{

				return true;
			}

			else
			{

				return false;
			}
		}

		else
		{

			return false;
		}
	}

	// Showing actual durability.
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{

		// NBT tag
		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt != null)
		{

			double percentLeft = (double) nbt.getInteger("durability") / 100;
			double durabilityToShow = 1 - percentLeft;
			return durabilityToShow;
		}

		else
		{

			return 1;
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{

		return slotChanged;
	}
}