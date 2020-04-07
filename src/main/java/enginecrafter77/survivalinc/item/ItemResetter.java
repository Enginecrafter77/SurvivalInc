package enginecrafter77.survivalinc.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map.Entry;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;

public class ItemResetter extends Item {

	public ItemResetter()
	{
		this.setRegistryName(new ResourceLocation(SurvivalInc.MOD_ID, "resettool"));
		this.setUnlocalizedName("resettool");
		this.setMaxStackSize(1);
		this.setCreativeTab(SurvivalInc.mainTab);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		if(!world.isRemote)
		{
			StatTracker tracker = player.getCapability(StatRegister.CAPABILITY, null);
			for(Entry<StatProvider, Float> entries : tracker)
			{
				tracker.setStat(entries.getKey(), entries.getKey().getDefault());
			}
		}
		return super.onItemRightClick(world, player, hand);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add("This little thing will restore all your stats to default values");
	}
}