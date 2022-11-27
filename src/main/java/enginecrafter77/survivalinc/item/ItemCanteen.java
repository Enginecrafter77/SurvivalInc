package enginecrafter77.survivalinc.item;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemCanteen extends Item {
	public static final String NBT_KEY_WATERVOLUME = "storage";
	public static final String NBT_KEY_REFILL = "refill";
	public static final String NBT_KEY_LASTUSE = "last_use";

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
		return Optional.ofNullable(this.getMode(stack)).orElse(CanteenMode.DRINK).getMetadata();
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity)
	{
		if(!this.canBeUsedAgain(stack, world))
			return stack;

		WaterVolume volume = this.getWaterVolume(stack);
		if(volume == null)
			return stack;

		if(Optional.ofNullable(this.getMode(stack)).orElse(CanteenMode.DRINK) == CanteenMode.REFILL)
		{
			Vec3d eyes = entity.getPositionEyes(1F);
			Vec3d look = entity.getLookVec();
			Vec3d target = eyes.add(look.add(look.x < 0 ? -0.5D : 0.5D, -1D, look.z < 0 ? -0.5D : 0.5D));
			RayTraceResult raytrace = world.rayTraceBlocks(eyes, target, true);

			if(raytrace != null)
			{
				int volumeUntilFull = ModConfig.HYDRATION.canteenCapacity - volume.getVolume();
				WaterVolume collected = WaterVolume.fromBlock(world, raytrace.getBlockPos(), volumeUntilFull);

				if(collected == null)
					volume.clear();
				else
					volume.mix(collected);

				if(!world.isRemote)
					world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.PLAYERS, 0.25F, 1.5F);
			}
		}
		else
		{
			volume.split(ModConfig.HYDRATION.sipVolume).consume(entity);
		}

		this.setWaterVolume(stack, volume);
		this.recordCanteenUse(stack, world);
		return stack;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack item = player.getHeldItem(hand);
		NBTTagCompound nbt = item.getTagCompound();

		if(!this.canBeUsedAgain(item, world))
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, item);

		// Fail prematurely if the stack has no tag compound
		if(nbt == null) return new ActionResult<ItemStack>(EnumActionResult.FAIL, item);

		// First check if the player just wants to switch modes
		if(player.isSneaking())
		{
			this.cycleMode(item);
			if(world.isRemote)
				((WorldClient)world).playSound(player.getPosition(), SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.PLAYERS, 0.5F, 1F, false);
			return new ActionResult<ItemStack>(EnumActionResult.PASS, item);
		}
		
		WaterVolume stored = this.getWaterVolume(item);
		if(stored == null)
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, item);

		boolean result;
		if(this.getMode(item) == CanteenMode.REFILL)
		{
			Vec3d eyes = player.getPositionEyes(1F);
			Vec3d look = player.getLookVec();
			Vec3d target = eyes.add(look.add(look.x < 0 ? -0.5D : 0.5D, -1D, look.z < 0 ? -0.5D : 0.5D));
			RayTraceResult raytrace = world.rayTraceBlocks(eyes, target, true);
			result = (raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK && world.getBlockState(raytrace.getBlockPos()).getMaterial() == Material.WATER && stored.getVolume() < ModConfig.HYDRATION.canteenCapacity) || stored.getVolume() > 0;
		}
		else result = stored.getVolume() > 0 && StatCapability.obtainRecord(SurvivalInc.hydration, player).map(SimpleStatRecord::getNormalizedValue).orElse(0F) < 1F;
		
		if(result) player.setActiveHand(hand);
		return new ActionResult<ItemStack>(result ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, item);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		// Ensure the NBT is correctly initialized
		if(!stack.hasTagCompound())
		{
			stack.setTagCompound(new NBTTagCompound());
			this.setWaterVolume(stack, WaterVolume.empty());
			this.setMode(stack, CanteenMode.DRINK);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null)
			return;

		tooltip.add(String.format("Mode: %s", Optional.ofNullable(this.getMode(stack)).map(CanteenMode::name).orElse("null")));

		WaterVolume volume = this.getWaterVolume(stack);
		if(volume != null)
		{
			tooltip.add(String.format("Stored: %d/%d mb", volume.getVolume(), ModConfig.HYDRATION.canteenCapacity));
			tooltip.add(String.format("Salinity: %.02f %%", volume.getSalinity()));
			tooltip.add(String.format("Temperature: %.02f °C", volume.getTemperature() * 20F));
			if(volume.isDirty()) tooltip.add(TextFormatting.RED + "DIRTY");
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return Optional.ofNullable(this.getMode(stack)).orElse(CanteenMode.DRINK).getItemAction();
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return Optional.ofNullable(this.getMode(stack)).orElse(CanteenMode.DRINK).getUseDuration();
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return stack.hasTagCompound();
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		WaterVolume volume = this.getWaterVolume(stack);
		if(volume == null)
			return 0D;
		return 1D - (double)volume.getVolume() / (double)ModConfig.HYDRATION.canteenCapacity;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		return ItemCanteen.waterBarColor;
	}

	@Nullable
	public WaterVolume getWaterVolume(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)
			return null;
		return WaterVolume.fromNBT(tag.getCompoundTag(NBT_KEY_WATERVOLUME));
	}

	public void setWaterVolume(ItemStack stack, WaterVolume volume)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)
			return;
		tag.setTag(NBT_KEY_WATERVOLUME, volume.serializeNBT());
	}

	@Nullable
	public CanteenMode getMode(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)
			return null;
		byte modeIndex = tag.getByte(NBT_KEY_REFILL);
		return CanteenMode.values()[modeIndex];
	}

	public void setMode(ItemStack stack, CanteenMode mode)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)
			return;
		tag.setByte(NBT_KEY_REFILL, (byte)mode.ordinal());
	}

	public void cycleMode(ItemStack stack)
	{
		CanteenMode mode = this.getMode(stack);
		if(mode == null)
			mode = CanteenMode.DRINK;
		CanteenMode[] modes = CanteenMode.values();
		mode = modes[(mode.ordinal() + 1) % modes.length];
		this.setMode(stack, mode);
	}

	public boolean canBeUsedAgain(ItemStack stack, World worldIn)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)
			return true;
		long time = tag.getLong(NBT_KEY_LASTUSE);
		time += this.getMaxItemUseDuration(stack);
		return time < worldIn.getTotalWorldTime();
	}

	public void recordCanteenUse(ItemStack stack, World usedIn)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)
			return;
		tag.setLong(NBT_KEY_LASTUSE, usedIn.getTotalWorldTime());
	}

	public static enum CanteenMode {
		DRINK(0, EnumAction.DRINK, 32),
		REFILL(1, EnumAction.BOW, 16);

		private final EnumAction itemAction;
		private final int useDuration;
		private final int meta;

		private CanteenMode(int meta, EnumAction itemAction, int useDuration)
		{
			this.meta = meta;
			this.itemAction = itemAction;
			this.useDuration = useDuration;
		}

		public EnumAction getItemAction()
		{
			return this.itemAction;
		}

		public int getUseDuration()
		{
			return this.useDuration;
		}

		public int getMetadata()
		{
			return this.meta;
		}
	}
}
