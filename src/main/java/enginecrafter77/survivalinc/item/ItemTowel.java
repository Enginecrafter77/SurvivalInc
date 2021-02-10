package enginecrafter77.survivalinc.item;

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

import java.util.List;

import com.google.common.collect.Range;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.net.EntityItemUpdateMessage;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.impl.HeatModifier;
import enginecrafter77.survivalinc.stats.impl.WetnessModifier;

public class ItemTowel extends Item {
	
	private static final Range<Long> daytime = Range.open(0L, 16000L);
	
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
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity)
	{
		StatTracker tracker = entity.getCapability(StatCapability.target, null);
		NBTTagCompound tag = stack.getTagCompound();
		
		// Wetness equalization
		SimpleStatRecord wetness = tracker.getRecord(WetnessModifier.instance);
		
		float stored = tag.getFloat("stored"), absorb = (wetness.getValue() + stored) / 2F, leave = absorb;
		
		SurvivalInc.logger.debug("TOWEL| W: {}, S: {}, A/L: {}", wetness, stored, absorb);
		
		if(absorb > this.getCapacity())
		{
			float overflow = absorb - this.getCapacity();
			absorb -= overflow;
			leave += overflow;
			SurvivalInc.logger.debug("TOWEL| \\--> Capacity exceeded");
		}
		
		SurvivalInc.logger.debug("TOWEL| \\--> A: {}, L: {}", absorb, leave);
		
		wetness.setValue(leave);
		tag.setFloat("stored", absorb);
		return stack;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack item = player.getHeldItem(hand);
		// Towel usage is always allowed since the equalization already takes care of the checks
		player.setActiveHand(hand);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		if(!stack.hasTagCompound())
		{
			NBTTagCompound tag = new NBTTagCompound();
			tag.setFloat("stored", 0F);
			stack.setTagCompound(tag);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack)
	{
		if(stack.hasTagCompound())
		{
			NBTTagCompound tag = stack.getTagCompound();
			StringBuilder name = new StringBuilder(super.getItemStackDisplayName(stack));
			float stored = tag.getFloat("stored"), quarter = this.getCapacity() / 4F;
			name.insert(0, this.getMetadata(stack) == 0 ? "Dry " : "Wet ");
			if(stored < (quarter * 3) && stored > quarter) name.insert(0, "Mostly ");
			return name.toString();
		}
		else return super.getItemStackDisplayName(stack);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
	{
		if(stack.hasTagCompound())
			tooltip.add(String.format("Wetness: %d%%", Math.round(100F * (stack.getTagCompound().getFloat("stored") / this.getCapacity()))));
	}

	// Provides automatic drying when on lit furnace
	@Override
	public boolean onEntityItemUpdate(EntityItem entity)
	{
		ItemStack stack = entity.getItem();
		if(!stack.hasTagCompound()) return false;
		
		NBTTagCompound tag = entity.getItem().getTagCompound();
		float stored = tag.getFloat("stored");
		
		float heat = HeatModifier.absorbRadiantHeat(entity, 5F);
		if(daytime.contains(entity.world.getWorldTime() % 24000) && entity.world.canBlockSeeSky(entity.getPosition()))
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
				tag.setFloat("stored", post);
				
				// If the texture shall change (metadata changes), or the towel is completely dried (should stop emmiting steam), sync the ItemStack data
				if(this.getMetadata(post) != this.getMetadata(stored) || post == 0)
				{
					WorldServer serverworld = (WorldServer)entity.world;
					serverworld.getEntityTracker().sendToTracking(entity, SurvivalInc.proxy.net.getPacketFrom(new EntityItemUpdateMessage(entity)));
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
			clientworld.spawnParticle(EnumParticleTypes.CLOUD, entity.posX, entity.posY + 0.6, entity.posZ, 0, 0.1F, 0, null);
			clientworld.playSound(entity.getPosition(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.NEUTRAL, 0.05F, 1.5F, false);
		}
	}
	
	public int getMetadata(float stored)
	{
		return stored < (this.getCapacity() / 2) ? 0 : 1;
	}
	
	@Override
	public int getMetadata(ItemStack stack)
	{
		int meta = 0;
		if(stack.hasTagCompound())
		{
			NBTTagCompound tag = stack.getTagCompound();
			float stored = tag.getFloat("stored");
			meta = this.getMetadata(stored);
		}
		return meta;
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
		NBTTagCompound tag = stack.getTagCompound();
		return 1 - (double)tag.getInteger("stored") / this.getCapacity();
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
}