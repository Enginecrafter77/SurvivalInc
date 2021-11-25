package enginecrafter77.survivalinc.stats.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Range;
import com.google.common.primitives.Floats;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.StatRegisterEvent;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.effect.CalculatorFunction;
import enginecrafter77.survivalinc.stats.effect.DamageStatEffect;
import enginecrafter77.survivalinc.stats.effect.EffectApplicator;
import enginecrafter77.survivalinc.stats.effect.FunctionalCalculator;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffectFilter;
import enginecrafter77.survivalinc.stats.effect.PotionStatEffect;
import enginecrafter77.survivalinc.stats.effect.StatEffect;
import enginecrafter77.survivalinc.stats.impl.armor.ConfigurableArmorModifier;
import enginecrafter77.survivalinc.util.FunctionalImplementation;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * The class that handles heat radiation and it's associated interactions with the player entity.
 *
 * @author Enginecrafter77
 */
public class HeatModifier implements StatProvider<SimpleStatRecord> {
	private static final long serialVersionUID = 6260092840749029918L;
	
	public static final DamageSource HYPERTHERMIA = new DamageSource("survivalinc_hyperthermia").setDamageIsAbsolute().setDamageBypassesArmor();
	public static final DamageSource HYPOTHERMIA = new DamageSource("survivalinc_hypothermia").setDamageIsAbsolute().setDamageBypassesArmor();
	
	public static HeatModifier instance = null;
	
	public final Map<Block, Float> blockHeatMap;
	public final ConfigurableArmorModifier armor;
	
	public final FunctionalCalculator targettemp;
	public final FunctionalCalculator exchangerate;
	public final EffectApplicator<SimpleStatRecord> consequences;
	public final StatEffect<SimpleStatRecord> counteraction;
	
	private HeatModifier()
	{
		this.targettemp = new FunctionalCalculator();
		this.exchangerate = new FunctionalCalculator();
		this.consequences = new EffectApplicator<SimpleStatRecord>();
		this.counteraction = HeatModifier::heatCounteraction;
		this.blockHeatMap = new HashMap<Block, Float>();
		this.armor = new ConfigurableArmorModifier();
		
		this.targettemp.add(HeatModifier::absorbRadiantHeat);
		this.targettemp.add(HeatModifier::acceptSunlightHeat);
		
		if(ModConfig.WETNESS.enabled) this.exchangerate.add(HeatModifier::applyWetnessCooldown);
		this.exchangerate.add(this.armor);
		
		this.consequences.add(new DamageStatEffect(HeatModifier.HYPOTHERMIA, (float)ModConfig.HEAT.damageAmount, 10)).addFilter(FunctionalEffectFilter.byValue(Range.lessThan(10F)));
		this.consequences.add(new PotionStatEffect(MobEffects.MINING_FATIGUE, 0)).addFilter(FunctionalEffectFilter.byValue(Range.lessThan(20F)));
		this.consequences.add(new PotionStatEffect(MobEffects.WEAKNESS, 0)).addFilter(FunctionalEffectFilter.byValue(Range.lessThan(25F)));
		this.consequences.add(HeatModifier::onHighTemperature).addFilter(FunctionalEffectFilter.byValue(Range.greaterThan(110F)));
	}
	
	public static void init()
	{
		HeatModifier.instance = new HeatModifier();
		MinecraftForge.EVENT_BUS.register(HeatModifier.class);
	}
	
	/**
	 * A simple method to check whether the provider was loaded or not. This should coincide with whether the provider is
	 * registered in the player's stat registry. This should NOT be confused with
	 * {@link enginecrafter77.survivalinc.config.HeatConfig#enabled}, since the latter can be changed during the game.
	 *
	 * @return True if the {@link #init()} method has been called in the past, false otherwise.
	 */
	public static boolean loaded()
	{
		return HeatModifier.instance != null;
	}
	
	public void buildCompatMaps()
	{
		// Shit, these repeated parsers will surely get me a bad codefactor.io mark.
		// Block temperature map
		for(String entry : ModConfig.HEAT.blockHeatMap)
		{
			int separator = entry.lastIndexOf(' ');
			Block target = Block.getBlockFromName(entry.substring(0, separator));
			Float value = Float.parseFloat(entry.substring(separator + 1));
			this.blockHeatMap.put(target, value);
		}
	}
	
	@SubscribeEvent
	public static void registerStat(StatRegisterEvent event)
	{
		event.register(HeatModifier.instance);
	}
	
	@Override
	public void update(EntityPlayer player, SimpleStatRecord heat)
	{
		if(player.isCreative() || player.isSpectator()) return;
		
		float target = HeatModifier.primaryHeatCalculation(player);
		target = this.targettemp.apply(player, target);
		
		float difference = Math.abs(target - heat.getValue());
		float rate = difference * (float)ModConfig.HEAT.heatExchangeFactor;
		rate = this.exchangerate.apply(player, rate);
		
		// If the current value is higher than the target, go down instead of up
		if(heat.getValue() > target) rate *= -1;
		
		// Apply counteraction (if enabled) and check out value change
		heat.addToValue(rate);
		if(ModConfig.HEAT.enableCounteraction) this.counteraction.apply(heat, player);
		heat.checkoutValueChange();
		
		// Apply the "side effects"
		this.consequences.apply(heat, player);
	}
	
	@Override
	public ResourceLocation getStatID()
	{
		return new ResourceLocation(SurvivalInc.MOD_ID, "heat");
	}
	
	@Override
	public SimpleStatRecord createNewRecord()
	{
		SimpleStatRecord record = new SimpleStatRecord(Range.closed(-20F, 120F));
		record.setValue(80F);
		return record;
	}
	
	@Override
	public Class<SimpleStatRecord> getRecordClass()
	{
		return SimpleStatRecord.class;
	}
	
	@FunctionalImplementation(of = StatEffect.class)
	public static void onHighTemperature(StatRecord record, EntityPlayer player)
	{
		if(ModConfig.HEAT.fireDuration > 0)
		{
			player.setFire(ModConfig.HEAT.fireDuration);
		}
		else
		{
			player.attackEntityFrom(HeatModifier.HYPERTHERMIA, (float)ModConfig.HEAT.damageAmount);
		}
	}
	
	@FunctionalImplementation(of = CalculatorFunction.class)
	public static float applyWetnessCooldown(EntityPlayer player, float current)
	{
		StatTracker stats = player.getCapability(StatCapability.target, null);
		SimpleStatRecord wetness = stats.getRecord(WetnessModifier.instance);
		return current * (1F + (float)ModConfig.HEAT.wetnessExchangeMultiplier * wetness.getNormalizedValue());
	}
	
	/**
	 * Applies the highest heat emmited by the neighboring blocks. Note that this method does NOT account blocks inbetween,
	 * as that would need to involve costly raytracing. Also, only the heat Anyway, the way the heat delivered to the player
	 * is calculated by the following formula:
	 *
	 * <pre>
	 *                   s
	 * f(x): y = t * ---------
	 *                x^2 + s
	 * </pre>
	 *
	 * Where t is the base heat of the block (the heat delivered when the distance to the source is 0), s is a special
	 * so-called "gaussian constant" and x is the distance to the player. The "gaussian constant" has got it's name because
	 * the graph of that function roughly resembles gauss's curve. The constant in itself is a special value that indicates
	 * the scaling of the heat given. The higher the value is the slower the heat decline with distance is. A fairly
	 * reasonable value is 1.5, but this value can be specified in the config. It is recommended that players that use low
	 * block scan range to also use lower gaussian constant.
	 *
	 * @author Enginecrafter77
	 * @param entity The target entity to apply this function to
	 * @return The addition to the heat stat value
	 */
	@FunctionalImplementation(of = CalculatorFunction.class)
	public static float absorbRadiantHeat(Entity entity, float current)
	{
		Vec3i offset = new Vec3i(ModConfig.HEAT.blockScanRange, 1, ModConfig.HEAT.blockScanRange);
		BlockPos originblock = entity.getPosition();
		
		Iterable<BlockPos> blocks = BlockPos.getAllInBox(originblock.subtract(offset), originblock.add(offset));
		
		float heat = 0;
		for(BlockPos position : blocks)
		{
			Block block = entity.world.getBlockState(position).getBlock();
			if(HeatModifier.instance.blockHeatMap.containsKey(block))
			{
				float currentheat = HeatModifier.instance.blockHeatMap.get(block);
				float proximity = (float)Math.sqrt(entity.getPositionVector().squareDistanceTo(new Vec3d(position)));
				currentheat *= (float)(ModConfig.HEAT.gaussScaling / (Math.pow(proximity, 2) + ModConfig.HEAT.gaussScaling));
				if(currentheat > heat) heat = currentheat; // Use only the maximum value
			}
		}
		
		return current + heat;
	}
	
	public static float primaryHeatCalculation(EntityPlayer player)
	{
		BlockPos position = player.getPosition();
		float target = player.world.getBiome(position).getTemperature(position);
		
		if(ModConfig.HEAT.gradientCaveTemperature)
		{
			// Get average surface height sample
			float surface = 0;
			int radius = ModConfig.HEAT.surfaceScanningRadius - 1;
			for(BlockPos additional : BlockPos.getAllInBoxMutable(position.getX() - radius, position.getY(), position.getZ() - radius, position.getX() + radius, position.getY(), position.getZ() + radius))
				surface += player.world.getPrecipitationHeight(additional).getY();
			surface /= Math.pow(1D + radius * 2D, 2D);
			
			if((float)player.posY < surface)
			{
				float effectheight = surface - ModConfig.HEAT.caveNormalizationDepth;
				float effectdelta = effectheight - (float)player.posY;
				if(effectdelta < ModConfig.HEAT.caveNormalizationDepth)
				{
					target += (ModConfig.HEAT.caveTemperature - target) * effectdelta / ModConfig.HEAT.caveNormalizationDepth;
				}
				else
				{
					target = (float)ModConfig.HEAT.caveTemperature * (1F + effectdelta / effectheight); // Cave
				}
			}
		}
		else if(player.posY < player.world.getSeaLevel()) target = (float)ModConfig.HEAT.caveTemperature;
		
		return Floats.constrainToRange(target, -0.2F, 1.5F) * (float)ModConfig.HEAT.tempCoefficient;
	}
	
	@FunctionalImplementation(of = CalculatorFunction.class)
	public static float acceptSunlightHeat(EntityPlayer player, float current)
	{
		if(player.world.isDaytime())
		{
			current += ModConfig.HEAT.daytimeDifference;
			if(player.world.canSeeSky(new BlockPos(player.getPositionEyes(1F)))) current += ModConfig.HEAT.sunlightBonus;
		}
		else if(ModConfig.HEAT.colderNights) current -= ModConfig.HEAT.daytimeDifference;
		return current;
	}
	
	@FunctionalImplementation(of = StatEffect.class)
	public static void heatCounteraction(SimpleStatRecord record, EntityPlayer player)
	{
		Range<Float> valrange = record.getValueRange();
		float rangesize = valrange.upperEndpoint() - valrange.lowerEndpoint();
		float midpoint = valrange.lowerEndpoint() + rangesize / 2F;
		
		float middiff = midpoint - record.getValue(); // How much colder the body is than optimal
		float middelta = Math.abs(middiff); // The distance to optimal temperature
		float way = middiff / middelta; // 1 = too cold (regenerating temp), -1 = too hot (dissipating temp)
		float amplitude = (float)(way > 0F ? ModConfig.HEAT.positiveCAAmplitude : ModConfig.HEAT.negativeCAAmplitude); // Determine target amplitude
		float adjustment = (float)Math.min(Math.pow(middelta / (rangesize * ModConfig.HEAT.counteractionCoverage), ModConfig.HEAT.counteractionExponent), 1F) * way * amplitude;
		
		//if(player.world.getWorldTime() % 20 == 0)
		//	SurvivalInc.logger.debug("[Heat Counteraction] R: {}({}), M: {}, V: {}, D: {}, A: {} (X:{})", valrange.toString(), rangesize, midpoint, record.getValue(), middiff, adjustment, amplitude);
		
		record.addToValue(adjustment);
	}
}
