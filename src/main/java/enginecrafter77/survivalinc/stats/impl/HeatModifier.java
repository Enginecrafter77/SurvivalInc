package enginecrafter77.survivalinc.stats.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Range;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.StatRegisterEvent;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.effect.DamageStatEffect;
import enginecrafter77.survivalinc.stats.effect.EffectApplicator;
import enginecrafter77.survivalinc.stats.effect.FunctionalCalculator;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffectFilter;
import enginecrafter77.survivalinc.stats.effect.PotionStatEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * The class that handles heat radiation and
 * it's associated interactions with the
 * player entity.
 * @author Enginecrafter77
 */
public class HeatModifier implements StatProvider {
	private static final long serialVersionUID = 6260092840749029918L;
	
	public static final DamageSource HYPERTHERMIA = new DamageSource("survivalinc_hyperthermia").setDamageIsAbsolute().setDamageBypassesArmor();
	public static final DamageSource HYPOTHERMIA = new DamageSource("survivalinc_hypothermia").setDamageIsAbsolute().setDamageBypassesArmor();
	
	public static Map<Block, Float> blockHeatMap = new HashMap<Block, Float>();
	public static ArmorModifier armorInsulation = new ArmorModifier();
	
	public static FunctionalCalculator targettemp = new FunctionalCalculator();
	public static FunctionalCalculator exchangerate = new FunctionalCalculator();
	public static EffectApplicator<SimpleStatRecord> consequences = new EffectApplicator<SimpleStatRecord>();
	
	public HeatModifier()
	{
		HeatModifier.targettemp.add(HeatModifier::whenNearHotBlock);
		
		if(ModConfig.WETNESS.enabled) HeatModifier.exchangerate.add(HeatModifier::applyWetnessCooldown);
		HeatModifier.exchangerate.add(HeatModifier.armorInsulation);
		
		HeatModifier.consequences.add(new DamageStatEffect(HYPOTHERMIA, (float)ModConfig.HEAT.damageAmount, 10)).addFilter(FunctionalEffectFilter.byValue(Range.lessThan(10F)));
		HeatModifier.consequences.add(new PotionStatEffect(MobEffects.MINING_FATIGUE, 0)).addFilter(FunctionalEffectFilter.byValue(Range.lessThan(20F)));
		HeatModifier.consequences.add(new PotionStatEffect(MobEffects.WEAKNESS, 0)).addFilter(FunctionalEffectFilter.byValue(Range.lessThan(25F)));
		HeatModifier.consequences.add(HeatModifier::onHighTemperature).addFilter(FunctionalEffectFilter.byValue(Range.greaterThan(110F)));
		
		// Shit, these repeated parsers will surely get me a bad codefactor.io mark.
		// Block temperature map
		for(String entry : ModConfig.HEAT.blockHeatMap)
		{
			int separator = entry.lastIndexOf(' ');
			Block target = Block.getBlockFromName(entry.substring(0, separator));
			Float value = Float.parseFloat(entry.substring(separator + 1));
			HeatModifier.blockHeatMap.put(target, value);
		}
		
		// Armor heat isolation
		for(String entry : ModConfig.HEAT.armorMaterialConductivity)
		{
			int separator = entry.lastIndexOf(' ');
			ItemArmor.ArmorMaterial target = ItemArmor.ArmorMaterial.valueOf(entry.substring(0, separator).toUpperCase());
			Float value = Float.parseFloat(entry.substring(separator + 1));
			HeatModifier.armorInsulation.addArmorType(target, value);
		}
	}
	
	@SubscribeEvent
	public static void registerStat(StatRegisterEvent event)
	{
		event.register(SurvivalInc.proxy.heat);
	}
	
	@Override
	public void update(EntityPlayer player, StatRecord record)
	{
		if(player.isCreative() || player.isSpectator()) return;
		
		float target;
		if(player.posY < player.world.getSeaLevel()) target = (float)ModConfig.HEAT.caveTemperature; // Cave
		else
		{
			Biome biome = player.world.getBiome(player.getPosition());
			target = biome.getTemperature(player.getPosition());
			if(target < -0.2F) target = -0.2F;
			if(target > 1.5F) target = 1.5F;
		}
		target = targettemp.apply(player, target * (float)ModConfig.HEAT.tempCoefficient);
		
		SimpleStatRecord heat = (SimpleStatRecord)record;
		float difference = Math.abs(target - heat.getValue());
		float rate = difference * (float)ModConfig.HEAT.heatExchangeFactor;
		rate = HeatModifier.exchangerate.apply(player, rate);
		
		// Apply the "side effects"
		HeatModifier.consequences.apply(heat, player);
		
		// If the current value is higher than the target, go down instead of up
		if(heat.getValue() > target) rate *= -1;
		// Checkout the rate to the value
		heat.addToValue(rate);
		heat.checkoutValueChange();
	}

	@Override
	public ResourceLocation getStatID()
	{
		return new ResourceLocation(SurvivalInc.MOD_ID, "heat");
	}

	@Override
	public StatRecord createNewRecord()
	{
		SimpleStatRecord record = new SimpleStatRecord(Range.closed(-20F, 120F));
		record.setValue(80F);
		return record;
	}
	
	public static void onHighTemperature(StatRecord record, EntityPlayer player)
	{
		if(ModConfig.HEAT.fireDuration > 0)
		{
			player.setFire(1);
		}
		else
		{
			player.attackEntityFrom(HYPERTHERMIA, (float)ModConfig.HEAT.damageAmount);
		}
	}
	
	public static float applyWetnessCooldown(EntityPlayer player, float current)
	{
		StatTracker stats = player.getCapability(StatCapability.target, null);
		SimpleStatRecord wetness = (SimpleStatRecord)stats.getRecord(SurvivalInc.proxy.wetness);
		return current * (1F + (float)ModConfig.HEAT.wetnessExchangeMultiplier * (wetness.getValue() / wetness.valuerange.upperEndpoint()));
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
	public static float whenNearHotBlock(EntityPlayer player, float current)
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
		
		return current + heat;
	}
}
