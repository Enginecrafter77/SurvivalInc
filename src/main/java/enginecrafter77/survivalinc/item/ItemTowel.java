package enginecrafter77.survivalinc.item;

import com.google.common.collect.Range;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.net.EntityItemUpdateMessage;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemTowel extends Item {
	private static final Range<Long> RANGE_DAYTIME = Range.open(0L, 16000L);
	private static final Range<Float> MOSTLY_DECORATOR_RANGE = Range.closed(0.25F, 0.75F);

	public static final int META_DRY = 0;
	public static final int META_WET = 1;

	public static final String NBT_KEY_STOREDWATER = "stored";
	public static final String NBT_KEY_LASTUSE = "last_use";
	
	public ItemTowel()
	{
		this.setRegistryName(new ResourceLocation(SurvivalInc.MOD_ID, "towel"));
		this.setCreativeTab(SurvivalInc.mainTab);
		this.setTranslationKey("towel");
		this.setMaxStackSize(1);
	}
	
	public float getCapacity()
	{
		return 100F * (float)ModConfig.WETNESS.towelCapacity;
	}

	public float getAbsorptionFraction()
	{
		return 0.75F;
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity)
	{
		if(!this.canBeUsedAgain(stack, world))
			return stack;

		this.recordTowelUse(stack, world);
		// Wetness equalization
		StatCapability.obtainRecord(SurvivalInc.wetness, entity).ifPresent((SimpleStatRecord wetness) -> {
			float stored = this.getStoredWater(stack);
			float sum = wetness.getValue() + stored;

			float absorb = sum * this.getAbsorptionFraction();
			float leave = sum - absorb;

			SurvivalInc.logger.info("TOWEL| W: {}, S: {}, X: {}, A: {}, L: {}", wetness, stored, sum, absorb, leave);

			if(absorb > this.getCapacity())
			{
				float overflow = absorb - this.getCapacity();
				absorb -= overflow;
				leave += overflow;
				SurvivalInc.logger.debug("TOWEL| \\--> Capacity exceeded");
			}

			SurvivalInc.logger.debug("TOWEL| \\--> A: {}, L: {}", absorb, leave);

			wetness.setValue(leave);
			this.setStoredWater(stack, absorb);
		});
		return stack;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack item = player.getHeldItem(hand);

		if(!this.canBeUsedAgain(item, world))
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, item);

		player.setActiveHand(hand);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		if(!stack.hasTagCompound())
		{
			stack.setTagCompound(new NBTTagCompound());
			this.setStoredWater(stack, 0F);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack)
	{
		if(stack.hasTagCompound())
		{
			float fill = this.getFillFraction(stack);
			StringBuilder name = new StringBuilder(super.getItemStackDisplayName(stack));
			name.insert(0, this.getMetadata(stack) == META_DRY ? "Dry " : "Wet ");
			if(MOSTLY_DECORATOR_RANGE.contains(fill))
				name.insert(0, "Mostly ");
			return name.toString();
		}
		else return super.getItemStackDisplayName(stack);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		if(stack.hasTagCompound())
			tooltip.add(String.format("Wetness: %d%%", Math.round(100F * this.getFillFraction(stack))));
	}

	// Provides automatic drying when on lit furnace
	@Override
	public boolean onEntityItemUpdate(EntityItem entity)
	{
		ItemStack stack = entity.getItem();
		if(!stack.hasTagCompound()) return false;
		
		float stored = this.getStoredWater(stack);
		
		float heat = 5F + SurvivalInc.heatScanner.scanPosition(entity.world, entity.getPositionVector(),3F);
		if(RANGE_DAYTIME.contains(entity.world.getWorldTime() % 24000) && entity.world.canBlockSeeSky(entity.getPosition()))
			heat += 40F;
		heat /= 100F;
		
		if(entity.world.isRemote)
		{
			if(stored > 0 && !entity.isInWater()) this.spawnDryingParticles(entity, Math.min(0.4F, 0.2F * heat));
		}
		else
		{
			float post = 0F;
			
			if(entity.isInWater()) post = stored + (float)ModConfig.WETNESS.fullySubmergedRate;
			else if(stored > 0) post = stored - (float)ModConfig.WETNESS.towelDryRate * heat; // Cap to 0 from bottom
			post = Math.max(0F, Math.min(this.getCapacity(), post)); // Clamp between 0F and capacity
			
			// To reduce IO stress
			if(post != stored)
			{
				this.setStoredWater(stack, post);
				
				// If the texture shall change (metadata changes), or the towel is completely dried (should stop emmiting steam), sync the ItemStack data
				if(this.getMetadata(post) != this.getMetadata(stored) || post == 0)
				{
					WorldServer serverworld = (WorldServer)entity.world;
					serverworld.getEntityTracker().sendToTracking(entity, SurvivalInc.net.getPacketFrom(new EntityItemUpdateMessage(entity)));
				}
			}
		}
		return super.onEntityItemUpdate(entity);
	}
	
	protected void spawnDryingParticles(EntityItem entity, float chance)
	{
		WorldClient clientworld = (WorldClient)entity.world;
		// Spawn the particles on random ticks
		if(clientworld.rand.nextFloat() < chance)
		{
			clientworld.spawnParticle(EnumParticleTypes.CLOUD, entity.posX, entity.posY + 0.6, entity.posZ, 0, 0.1F, 0);
			clientworld.playSound(entity.getPosition(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.NEUTRAL, 0.05F, 1.5F, false);
		}
	}
	
	public int getMetadata(float fill)
	{
		return fill < 0.5F ? META_DRY : META_WET;
	}
	
	@Override
	public int getMetadata(ItemStack stack)
	{
		return this.getMetadata(this.getFillFraction(stack));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean showDurabilityBar(ItemStack stack)
	{
		return stack.hasTagCompound();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return 1D - (double)this.getFillFraction(stack);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		return ItemCanteen.waterBarColor;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 20;
	}

	public void setStoredWater(ItemStack stack, float stored)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)
			return;
		tag.setFloat(NBT_KEY_STOREDWATER, stored);
	}

	public float getStoredWater(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)
			return 0F;
		return tag.getFloat(NBT_KEY_STOREDWATER);
	}

	public float getFillFraction(ItemStack stack)
	{
		return this.getStoredWater(stack) / this.getCapacity();
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

	public void recordTowelUse(ItemStack stack, World usedIn)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null)
			return;
		tag.setLong(NBT_KEY_LASTUSE, usedIn.getTotalWorldTime());
	}
}
