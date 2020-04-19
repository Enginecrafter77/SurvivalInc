package enginecrafter77.survivalinc.item;

import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeBeach;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.biome.BiomeSwamp;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.impl.DefaultStats;

import java.util.List;

public class ItemCanteen extends Item {
	
	public static final int waterBarColor = 0x23C4FF;
	
	public ItemCanteen()
	{
		this.setRegistryName(new ResourceLocation(SurvivalInc.MOD_ID, "canteen"));
		this.setTranslationKey("water_canteen");
		this.setCreativeTab(SurvivalInc.mainTab);
		this.setMaxStackSize(1);
		this.setNoRepair();//ItemFood
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving)
	{
		if(entityLiving instanceof EntityPlayer && !world.isRemote)
		{
			EntityPlayer player = (EntityPlayer)entityLiving;
			StatTracker stats = player.getCapability(StatCapability.target, null);
			NBTTagCompound nbt = stack.getTagCompound();
			int stored = nbt.getInteger("stored");
			nbt.setInteger("stored", stored - 32);
			stats.modifyStat(DefaultStats.HYDRATION, 10F);
		}
		return stack;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack item = player.getHeldItem(hand);
		if(world.isRemote) return new ActionResult<ItemStack>(EnumActionResult.PASS, item);
		NBTTagCompound nbt = item.getTagCompound();
		
		// First check if the player just wants to dispose of that water
		if(player.isSneaking())
		{
			nbt.setInteger("stored", 0);
			nbt.setFloat("quality", 1F);
			return new ActionResult<ItemStack>(EnumActionResult.PASS, item);
		}
		
		int stored = nbt.getInteger("stored");
		Vec3d look = player.getPositionEyes(1.0f).add(player.getLookVec().add(player.getLookVec().x < 0 ? -0.5 : 0.5, -1, player.getLookVec().z < 0 ? -0.5 : 0.5));
		RayTraceResult raytrace = player.world.rayTraceBlocks(player.getPositionEyes(1.0f), look, true);
		if(raytrace == null || raytrace.typeOfHit != RayTraceResult.Type.BLOCK || world.getBlockState(raytrace.getBlockPos()).getMaterial() != Material.WATER)
		{
			if(stored > 0)
			{
				player.setActiveHand(hand);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
			}
			else return new ActionResult<ItemStack>(EnumActionResult.FAIL, item);
		}
		// The player wants to fill the canteen (water raytrace succeeded)
		
		// If the canteen is full
		if(stored == nbt.getInteger("capacity")) return new ActionResult<ItemStack>(EnumActionResult.FAIL, item);
		
		float quality = 1F; 
		Biome biome = world.getBiome(raytrace.getBlockPos());
		if(biome instanceof BiomeBeach || biome instanceof BiomeOcean)
			quality *= 0.3F;
		else if(biome instanceof BiomeSwamp)
			quality *= 0.4F;
		
		// Quantitatively influenced quality
		float qiq = stored * nbt.getFloat("quality") + 32F * quality;
		stored += 32;
		qiq /= (int)stored;
		
		nbt.setFloat("quality", qiq);
		nbt.setInteger("stored", stored);
		((WorldServer)player.world).playSound(null, raytrace.getBlockPos(), SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.AMBIENT, 0.25F, 1.5F);
		
		return new ActionResult<ItemStack>(EnumActionResult.PASS, item);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		// Ensure the NBT is correctly initialized
		if(!stack.hasTagCompound())
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setFloat("quality", 1F);
			nbt.setInteger("stored", 0);
			nbt.setInteger("capacity", 512);
			stack.setTagCompound(nbt);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if(!stack.hasTagCompound()) return;
		
		NBTTagCompound nbt = stack.getTagCompound();
		int stored = nbt.getInteger("stored");
		tooltip.add(String.format("Stored: %d/%d mb", stored, nbt.getInteger("capacity")));
		if(stored > 0)
			tooltip.add(String.format("Quality: %f%%", nbt.getFloat("quality") * 100F));
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.DRINK;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 32;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return stack.hasTagCompound();
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();
		return 1 - (double)tag.getInteger("stored") / (double)tag.getInteger("capacity");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		return ItemCanteen.waterBarColor;
	}
}