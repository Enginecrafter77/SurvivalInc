package schoperation.schopcraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import schoperation.schopcraft.SchopCraft;
import schoperation.schopcraft.cap.sanity.ISanity;
import schoperation.schopcraft.cap.sanity.SanityProvider;
import schoperation.schopcraft.cap.temperature.ITemperature;
import schoperation.schopcraft.cap.temperature.TemperatureProvider;

public class ItemIceCream extends ItemFood {
	
	/*
	 * A simple food item dedicated to cooling the player and fulfilling the desire for a cool treat.
	 */

	public ItemIceCream(int amount, float saturation, boolean isWolfFood) {
		
		// Get and apply stuff.
		super(amount, saturation, isWolfFood);
		
		// Set registry and unlocalized names.
		setRegistryName(new ResourceLocation(SchopCraft.MOD_ID, "ice_cream"));
		setUnlocalizedName(SchopCraft.RESOURCE_PREFIX + "ice_cream");
		
		// Basic properties.
		setMaxStackSize(2);
		setCreativeTab(SchopCraft.mainTab);
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
		
        if (entityLiving instanceof EntityPlayer) {
        	
        	// Default eating crap.
            EntityPlayer entityplayer = (EntityPlayer)entityLiving;
            entityplayer.getFoodStats().addStats(this, stack);
            world.playSound((EntityPlayer) null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
            this.onFoodEaten(stack, world, entityplayer);
            entityplayer.addStat(StatList.getObjectUseStats(this));

            if (entityplayer instanceof EntityPlayerMP) {
            	
                CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, stack);
            }
            
            // Server-side stuff for adjusting temp.
            if (!world.isRemote) {
            	
            	// Capabilities
            	ITemperature temperature = entityLiving.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
            	ISanity sanity = entityLiving.getCapability(SanityProvider.SANITY_CAP, null);
            	
            	// Lower temperature
            	temperature.decrease(20.0f);
            	
            	// Increase sanity
            	sanity.increase(20.0f);
            }
        }

        stack.shrink(1);
        return stack;
    }
}