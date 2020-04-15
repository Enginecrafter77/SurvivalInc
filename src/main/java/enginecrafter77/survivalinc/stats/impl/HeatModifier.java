package enginecrafter77.survivalinc.stats.impl;

import java.util.HashMap;
import java.util.Map;

import enginecrafter77.survivalinc.ModDamageSources;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.OverflowHandler;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.modifier.DamagingModifier;
import enginecrafter77.survivalinc.stats.modifier.FunctionalModifier;
import enginecrafter77.survivalinc.stats.modifier.ModifierApplicator;
import enginecrafter77.survivalinc.stats.modifier.OperationType;
import enginecrafter77.survivalinc.stats.modifier.PotionEffectModifier;
import enginecrafter77.survivalinc.stats.modifier.ThresholdModifier;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.biome.Biome;

/**
 * The class that handles heat radiation and
 * it's associated interactions with the
 * player entity.
 * @author Enginecrafter77
 */
public class HeatModifier implements StatProvider {
	private static final long serialVersionUID = 6260092840749029918L;
	
	public static final HeatModifier instance = new HeatModifier();
	
	public static Map<Block, Float> blockHeatMap = new HashMap<Block, Float>();
	public static Map<ItemArmor.ArmorMaterial, Float> armorInsulation = new HashMap<ItemArmor.ArmorMaterial, Float>();
	public static ModifierApplicator<EntityPlayer> targettemp = new ModifierApplicator<EntityPlayer>();
	public static ModifierApplicator<EntityPlayer> exchangerate = new ModifierApplicator<EntityPlayer>();
	public static ModifierApplicator<EntityPlayer> consequences = new ModifierApplicator<EntityPlayer>();
	
	// Make it a singleton
	private HeatModifier() {}
	
	public void init()
	{
		// Block temperature map
		HeatModifier.blockHeatMap.put(Blocks.LAVA, 400F);
		HeatModifier.blockHeatMap.put(Blocks.FLOWING_LAVA, 350F);
		HeatModifier.blockHeatMap.put(Blocks.MAGMA, 300F);
		HeatModifier.blockHeatMap.put(Blocks.FIRE, 200F);
		HeatModifier.blockHeatMap.put(Blocks.LIT_FURNACE, 80F);
		HeatModifier.blockHeatMap.put(Blocks.LIT_PUMPKIN, 10F);
		
		// Armor heat isolation
		HeatModifier.armorInsulation.put(ItemArmor.ArmorMaterial.LEATHER, 0.5F);
		HeatModifier.armorInsulation.put(ItemArmor.ArmorMaterial.CHAIN, 1.1F);
		HeatModifier.armorInsulation.put(ItemArmor.ArmorMaterial.IRON, 1.2F);
		HeatModifier.armorInsulation.put(ItemArmor.ArmorMaterial.GOLD, 1.5F);
		HeatModifier.armorInsulation.put(ItemArmor.ArmorMaterial.DIAMOND, 2.25F);
		
		HeatModifier.targettemp.add(new FunctionalModifier<EntityPlayer>(HeatModifier::whenNearHotBlock), OperationType.OFFSET);
		
		HeatModifier.exchangerate.add(new FunctionalModifier<EntityPlayer>(HeatModifier::whenWearingArmor), OperationType.SCALE);
		HeatModifier.exchangerate.add(new FunctionalModifier<EntityPlayer>(HeatModifier::applyWetnessCooldown), OperationType.SCALE);
		
		HeatModifier.consequences.add(new ThresholdModifier<EntityPlayer>(new PotionEffectModifier(MobEffects.WEAKNESS, 0), 25F, ThresholdModifier.LOWER));
		HeatModifier.consequences.add(new ThresholdModifier<EntityPlayer>(new PotionEffectModifier(MobEffects.MINING_FATIGUE, 0), 20F, ThresholdModifier.LOWER));
		HeatModifier.consequences.add(new ThresholdModifier<EntityPlayer>(new DamagingModifier(ModDamageSources.HYPOTHERMIA, 1F, 10), 10F, ThresholdModifier.LOWER));
		HeatModifier.consequences.add(new ThresholdModifier<EntityPlayer>(new FunctionalModifier<EntityPlayer>((EntityPlayer player) -> player.setFire(1)), 110F, ThresholdModifier.HIGHER));
	}
	
	@Override
	public float updateValue(EntityPlayer player, float current)
	{		
		float target;
		if(player.posY < player.world.getSeaLevel()) target = 0.7F; // Cave
		else
		{
			Biome biome = player.world.getBiome(player.getPosition());
			target = biome.getTemperature(player.getPosition());
			if(target < -0.2F) target = -0.2F;
			if(target > 1.5F) target = 1.5F;
		}
		target = targettemp.apply(player, target * 78); // 78 = Schoperation's body heat constant
		
		float difference = Math.abs(target - current);
		float rate = difference * (float)ModConfig.HEAT.heatExchangeFactor;
		rate = HeatModifier.exchangerate.apply(player, rate);
		
		// Apply the "side effects"
		HeatModifier.consequences.apply(player, current);
		
		// If the current value is higher than the target, go down instead of up
		if(current > target) rate *= -1;
		return current + rate;
	}

	@Override
	public String getStatID()
	{
		return "heat";
	}

	@Override
	public float getMaximum()
	{
		return 120;
	}

	@Override
	public float getMinimum()
	{
		return -20F;
	}

	@Override
	public float getDefault()
	{
		return 75;
	}
	
	@Override
	public OverflowHandler getOverflowHandler()
	{
		return OverflowHandler.CAP;
	}
	
	public static float applyWetnessCooldown(EntityPlayer player)
	{
		StatTracker stats = player.getCapability(StatRegister.CAPABILITY, null);
		return 1F + 4 * (stats.getStat(DefaultStats.WETNESS) / DefaultStats.WETNESS.getMaximum());
	}
	
	/**
	 * Applies the highest heat emmited by the neighboring blocks.
	 * Note that this method does NOT account blocks inbetween, as
	 * that would need to involve costly raytracing. Also, only the
	 * heat Anyway, the
	 * way the heat delivered to the player is calculated by the
	 * following formula:
	 * <pre>
	 *                   s
	 * f(x): y = t * ---------
	 *                x^2 + s
	 * </pre>
	 * Where t is the base heat of the block (the heat delivered when
	 * the distance to the source is 0), s is a special so-called
	 * "gaussian constant" and x is the distance to the player. The
	 * "gaussian constant" has got it's name because the graph of
	 * that function roughly resembles gauss's curve. The constant
	 * in itself is a special value that indicates the scaling of
	 * the heat given. The higher the value is the slower the heat
	 * decline with distance is. A fairly reasonable value is 1.5,
	 * but this value can be specified in the config. It is recommended
	 * that players that use low block scan range to also use lower
	 * gaussian constant.
	 * @author Enginecrafter77
	 * @param player The player to apply this function to
	 * @return The addition to the heat stat value
	 */
	public static float whenNearHotBlock(EntityPlayer player)
	{
		Vec3i offset = new Vec3i(ModConfig.HEAT.blockScanRange, 1, ModConfig.HEAT.blockScanRange);
		BlockPos originblock = player.getPosition();
		
		Iterable<BlockPos> blocks = BlockPos.getAllInBox(originblock.subtract(offset), originblock.add(offset));
		
		float heat = 0;
		for(BlockPos position : blocks)
		{
			Block block = player.world.getBlockState(position).getBlock();
			if(HeatModifier.blockHeatMap.containsKey(block))
			{
				float currentheat = HeatModifier.blockHeatMap.get(block);
				float proximity = (float)Math.sqrt(player.getPositionVector().squareDistanceTo(new Vec3d(position)));
				currentheat *= (float)(ModConfig.HEAT.gaussScaling / (Math.pow(proximity, 2) + ModConfig.HEAT.gaussScaling));
				if(currentheat > heat) heat = currentheat; // Use only the maximum value
			}
		}
		
		return heat;
	}
	
	private static final double offset = -1.3D, sbase = 5E+9D;
	
	public static float whenWearingArmor(EntityPlayer player)
	{
		float buff = 0F;
		int index = 0;
		for(ItemStack stack : player.getArmorInventoryList())
		{
			if(stack.getItem() instanceof ItemArmor)
			{
				ItemArmor item = (ItemArmor)stack.getItem();
				if(HeatModifier.armorInsulation.containsKey(item.getArmorMaterial()))
				{
					buff += (HeatModifier.armorInsulation.get(item.getArmorMaterial()) / 4F) * (float)(sbase / (Math.pow(index + offset, 2) + sbase));
				}
			}
			index++;
		}
		
		if(buff == 0) buff = 1F;
		return buff;
	}
}
