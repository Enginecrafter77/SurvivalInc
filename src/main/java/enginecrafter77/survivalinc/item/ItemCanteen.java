package enginecrafter77.survivalinc.item;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.impl.HydrationModifier;
import enginecrafter77.survivalinc.stats.impl.WaterVolume;
import net.minecraft.block.material.Material;
import net.minecraft.client.multiplayer.WorldClient;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCanteen extends Item {
	
	public static final int waterBarColor = 0x23C4FF;
	
	public ItemCanteen()
	{
		this.setRegistryName(new ResourceLocation(SurvivalInc.MOD_ID, "canteen"));
		this.setTranslationKey("water_canteen");
		this.setCreativeTab(SurvivalInc.mainTab);
		this.setMaxStackSize(1);
		this.setNoRepair();
	}
	
	@Override
	public int getMetadata(ItemStack stack)
	{
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean("refill") ? 1 : 0;
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity)
	{
		if(stack.hasTagCompound())
		{
			NBTTagCompound nbt = stack.getTagCompound();
			WaterVolume volume = new WaterVolume(nbt.getCompoundTag("storage"));
			
			if(nbt.getBoolean("refill"))
			{
				Vec3d look = entity.getPositionEyes(1.0f).add(entity.getLookVec().add(entity.getLookVec().x < 0 ? -0.5 : 0.5, -1, entity.getLookVec().z < 0 ? -0.5 : 0.5));
				RayTraceResult raytrace = world.rayTraceBlocks(entity.getPositionEyes(1.0f), look, true);
				WaterVolume collected = null;
				
				if(raytrace != null) collected = WaterVolume.fromBlock(world, raytrace.getBlockPos(), ModConfig.HYDRATION.canteenCapacity);
				if(collected == null) volume.empty();
				else
				{
					volume.mix(collected);
					volume.setVolume(ModConfig.HYDRATION.canteenCapacity);
				}
				
				if(!world.isRemote) ((WorldServer)world).playSound(null, entity.getPosition(), SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.PLAYERS, 0.25F, 1.5F);
			}
			else
			{
				StatTracker stats = entity.getCapability(StatCapability.target, null);
				if(stats != null) volume.remove((float)ModConfig.HYDRATION.sipVolume).apply(stats.getRecord(HydrationModifier.instance), (EntityPlayer)entity);
			}
			
			nbt.setTag("storage", volume.serializeNBT());
		}
		return stack;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack item = player.getHeldItem(hand);
		
		// Fail prematurely if the stack has no tag compound
		if(!item.hasTagCompound()) return new ActionResult<ItemStack>(EnumActionResult.FAIL, item);
		
		NBTTagCompound nbt = item.getTagCompound();
		
		// First check if the player just wants to switch modes
		if(player.isSneaking())
		{
			nbt.setBoolean("refill", !nbt.getBoolean("refill"));
			if(world.isRemote) ((WorldClient)world).playSound(player.getPosition(), SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.PLAYERS, 0.5F, 1F, false);
			return new ActionResult<ItemStack>(EnumActionResult.PASS, item);
		}
		
		WaterVolume stored = new WaterVolume(nbt.getCompoundTag("storage"));
		boolean result = false;
		if(nbt.getBoolean("refill"))
		{
			Vec3d look = player.getPositionEyes(1.0f).add(player.getLookVec().add(player.getLookVec().x < 0 ? -0.5 : 0.5, -1, player.getLookVec().z < 0 ? -0.5 : 0.5));
			RayTraceResult raytrace = player.world.rayTraceBlocks(player.getPositionEyes(1.0f), look, true);
			result = (raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK && world.getBlockState(raytrace.getBlockPos()).getMaterial() == Material.WATER && stored.getVolume() < ModConfig.HYDRATION.canteenCapacity) || stored.getVolume() > 0;
		}
		else result = stored.getVolume() > 0;
		
		if(result) player.setActiveHand(hand);
		return new ActionResult<ItemStack>(result ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, item);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		// Ensure the NBT is correctly initialized
		if(!stack.hasTagCompound())
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("refill", false);
			nbt.setTag("storage", new WaterVolume(0F, 0F, 0F, false).serializeNBT());
			stack.setTagCompound(nbt);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		if(!stack.hasTagCompound()) return;
		
		NBTTagCompound nbt = stack.getTagCompound();
		WaterVolume volume = new WaterVolume(nbt.getCompoundTag("storage"));
		
		tooltip.add(String.format("Mode: %s", nbt.getBoolean("refill") ? "REFILL" : "DRAIN"));
		tooltip.add(String.format("Stored: %.01f/%d mb", volume.getVolume() * 8, ModConfig.HYDRATION.canteenCapacity * 8));
		tooltip.add(String.format("Salinity: %.02f %%", volume.getSalinity()));
		tooltip.add(String.format("Temperature: %.02f �C", volume.getTemperature() * 20F));
		if(volume.isDirty()) tooltip.add(TextFormatting.RED + "DIRTY");
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean("refill") ? EnumAction.BOW : EnumAction.DRINK;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		int duration = 32;
		if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("refill")) duration /= 2;
		return duration;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return stack.hasTagCompound();
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		double damage = 1;
		if(stack.hasTagCompound())
		{
			WaterVolume volume = new WaterVolume(stack.getTagCompound().getCompoundTag("storage"));
			damage -= volume.getVolume() / (double)ModConfig.HYDRATION.canteenCapacity;
		}
		return damage;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		return ItemCanteen.waterBarColor;
	}
}
