package enginecrafter77.survivalinc.stats.impl;

import com.google.common.collect.Range;
import com.google.common.primitives.Floats;
import enginecrafter77.survivalinc.ClientProxy;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.client.*;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.*;
import enginecrafter77.survivalinc.stats.effect.*;
import enginecrafter77.survivalinc.stats.impl.armor.ConfigurableArmorModifier;
import enginecrafter77.survivalinc.util.FunctionalImplementation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.Point;
import org.lwjgl.util.Rectangle;

/**
 * The class that handles heat radiation and its associated interactions with the player entity.
 * @author Enginecrafter77
 */
public class HeatModifier implements StatProvider<SimpleStatRecord> {
	public static final DamageSource HYPERTHERMIA = new DamageSource("survivalinc_hyperthermia").setDamageIsAbsolute().setDamageBypassesArmor();
	public static final DamageSource HYPOTHERMIA = new DamageSource("survivalinc_hypothermia").setDamageIsAbsolute().setDamageBypassesArmor();

	public final ConfigurableArmorModifier armor;
	
	public final FunctionalCalculator targettemp;
	public final FunctionalCalculator exchangerate;
	public final EffectApplicator<SimpleStatRecord> consequences;
	public final StatEffect<SimpleStatRecord> counteraction;
	
	public HeatModifier()
	{
		this.targettemp = new FunctionalCalculator();
		this.exchangerate = new FunctionalCalculator();
		this.consequences = new EffectApplicator<SimpleStatRecord>();
		this.counteraction = this::heatCounteraction;
		this.armor = new ConfigurableArmorModifier();
		
		this.targettemp.add(this::absorbRadiantHeat);
		this.targettemp.add(this::acceptSunlightHeat);
		
		if(ModConfig.WETNESS.enabled) this.exchangerate.add(this::applyWetnessCooldown);
		this.exchangerate.add(this.armor);
		
		this.consequences.add(new DamageStatEffect(HeatModifier.HYPOTHERMIA, (float)ModConfig.HEAT.damageAmount, 10)).addFilter(FunctionalEffectFilter.byValue(Range.lessThan(10F)));
		this.consequences.add(new PotionStatEffect(MobEffects.MINING_FATIGUE, 0)).addFilter(FunctionalEffectFilter.byValue(Range.lessThan(20F)));
		this.consequences.add(new PotionStatEffect(MobEffects.WEAKNESS, 0)).addFilter(FunctionalEffectFilter.byValue(Range.lessThan(25F)));
		this.consequences.add(this::onHighTemperature).addFilter(FunctionalEffectFilter.byValue(Range.greaterThan(110F)));
	}
	
	@SubscribeEvent
	public void registerStat(StatRegisterEvent event)
	{
		event.register(this);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void constructHud(HUDConstructEvent event)
	{
		StatFillBar<SimpleStatRecord> bar = new StatFillBar<SimpleStatRecord>(this, Direction2D.UP, ClientProxy.STAT_ICONS.region(new Rectangle(0, 18, 9, 16)));
		bar.addLayer(ClientProxy.STAT_ICONS.region(new Rectangle(9, 18, 9, 16)), SimpleStatRecord::getNormalizedValue);
		bar.setCapacity(1);

		event.addElement(bar, new AbsoluteElementLayoutFunction((float)ModConfig.CLIENT.hud.originX, (float)ModConfig.CLIENT.hud.originY, ModConfig.CLIENT.hud.heatIconX, ModConfig.CLIENT.hud.heatIconY)).setTrigger(RenderGameOverlayEvent.ElementType.EXPERIENCE);
		event.addRenderStageFilter(new TranslateRenderFilter(new Point(0, -10)), RenderGameOverlayEvent.ElementType.SUBTITLES);

		if(ModConfig.CLIENT.vignette.enable)
		{
			event.addElement(new StatRangeVignette(this, Range.lessThan(35F), ClientProxy.parseColor(ModConfig.CLIENT.vignette.coldColor), ModConfig.CLIENT.vignette.maxOpacity, ModConfig.CLIENT.vignette.logarithmicOpacity, true), AbsoluteElementLayoutFunction.ORIGIN).addFilter(new ScaleRenderFilter(Vec2f.MAX));
			event.addElement(new StatRangeVignette(this, Range.greaterThan(85F), ClientProxy.parseColor(ModConfig.CLIENT.vignette.hotColor), ModConfig.CLIENT.vignette.maxOpacity, ModConfig.CLIENT.vignette.logarithmicOpacity, false), AbsoluteElementLayoutFunction.ORIGIN).addFilter(new ScaleRenderFilter(Vec2f.MAX));
		}
	}
	
	@Override
	public void update(EntityPlayer player, SimpleStatRecord heat)
	{
		if(player.isCreative() || player.isSpectator()) return;
		
		float target = this.primaryHeatCalculation(player);
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
		return new SimpleStatRecord(Range.closed(-20F, 120F));
	}

	@Override
	public void resetRecord(SimpleStatRecord record)
	{
		record.setValue(80F);
	}

	@Override
	public Class<SimpleStatRecord> getRecordClass()
	{
		return SimpleStatRecord.class;
	}
	
	@FunctionalImplementation(of = StatEffect.class)
	public void onHighTemperature(StatRecord record, EntityPlayer player)
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
	public float applyWetnessCooldown(EntityPlayer player, float current)
	{
		float wetness = StatCapability.obtainRecord(SurvivalInc.wetness, player).map(SimpleStatRecord::getNormalizedValue).orElse(0F);
		return current * (1F + (float)ModConfig.HEAT.wetnessExchangeMultiplier * wetness);
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
	public float absorbRadiantHeat(Entity entity, float current)
	{
		return current + SurvivalInc.heatScanner.scanPosition(entity.world, entity.getPositionVector(), (float)ModConfig.HEAT.blockScanRange);
	}
	
	public float primaryHeatCalculation(EntityPlayer player)
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
	public float acceptSunlightHeat(EntityPlayer player, float current)
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
	public void heatCounteraction(SimpleStatRecord record, EntityPlayer player)
	{
		Range<Float> valrange = record.getValueRange();
		float rangesize = valrange.upperEndpoint() - valrange.lowerEndpoint();
		float midpoint = valrange.lowerEndpoint() + rangesize / 2F;
		
		float middiff = midpoint - record.getValue(); // How much colder the body is than optimal
		float middelta = Math.abs(middiff); // The distance to optimal temperature
		float way = middiff / middelta; // 1 = too cold (regenerating temp), -1 = too hot (dissipating temp)
		float amplitude = (float)(way > 0F ? ModConfig.HEAT.positiveCAAmplitude : ModConfig.HEAT.negativeCAAmplitude); // Determine target amplitude
		float adjustment = (float)Math.min(Math.pow(middelta / (rangesize * ModConfig.HEAT.counteractionCoverage), ModConfig.HEAT.counteractionExponent), 1F) * way * amplitude;
		
		record.addToValue(adjustment);
	}
}
