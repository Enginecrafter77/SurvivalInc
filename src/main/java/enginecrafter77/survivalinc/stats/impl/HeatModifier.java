package enginecrafter77.survivalinc.stats.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Range;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.stats.effect.DamageStatEffect;
import enginecrafter77.survivalinc.stats.effect.FilteredEffectApplicator;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffect;
import enginecrafter77.survivalinc.stats.effect.FunctionalEffectFilter;
import enginecrafter77.survivalinc.stats.effect.PotionStatEffect;
import enginecrafter77.survivalinc.stats.effect.SideEffectFilter;
import enginecrafter77.survivalinc.stats.effect.SimpleEffectApplicator;
import enginecrafter77.survivalinc.stats.effect.StatEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;

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
	
	public static final HeatModifier instance = new HeatModifier();
	public static final ResourceLocation identifier = new ResourceLocation(SurvivalInc.MOD_ID, "heat");
	
	public static Map<Block, Float> blockHeatMap = new HashMap<Block, Float>();
	public static ArmorModifier armorInsulation = new ArmorModifier();
	
	public static SimpleEffectApplicator targettemp = new SimpleEffectApplicator();
	public static SimpleEffectApplicator exchangerate = new SimpleEffectApplicator();
	public static FilteredEffectApplicator consequences = new FilteredEffectApplicator();
	
	// Make it a singleton
	private HeatModifier() {}
	
	public void init()
	{
		HeatModifier.targettemp.add(new FunctionalEffect(HeatModifier::whenNearHotBlock));
		
		if(ModConfig.WETNESS.enabled) HeatModifier.exchangerate.add(new FunctionalEffect(HeatModifier::applyWetnessCooldown));
		HeatModifier.exchangerate.add(HeatModifier.armorInsulation);
		
		HeatModifier.consequences.addEffect(new PotionStatEffect(MobEffects.WEAKNESS, 0), new FunctionalEffectFilter(Range.lessThan(25F)), new SideEffectFilter(Side.SERVER));
		HeatModifier.consequences.addEffect(new PotionStatEffect(MobEffects.MINING_FATIGUE, 0), new FunctionalEffectFilter(Range.lessThan(20F)), new SideEffectFilter(Side.SERVER));
		HeatModifier.consequences.addEffect(new DamageStatEffect(HYPOTHERMIA, (float)ModConfig.HEAT.damageAmount, 10), new FunctionalEffectFilter(Range.lessThan(10F)), new SideEffectFilter(Side.SERVER));
		HeatModifier.consequences.addEffect(new FunctionalEffect(HeatModifier::onHighTemperature), new FunctionalEffectFilter(Range.greaterThan(110F)), new SideEffectFilter(Side.SERVER));
		
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
	
	@Override
	public float updateValue(EntityPlayer player, float current)
	{		
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
		
		float difference = Math.abs(target - current);
		float rate = difference * (float)ModConfig.HEAT.heatExchangeFactor;
		rate = HeatModifier.exchangerate.apply(player, rate);
		
		// Apply the "side effects"
		HeatModifier.consequences.apply(player, current);
		
		// If the current value is higher than the target, go down instead of up
		if(current > target) rate *= -1;
		// Checkout the rate to the value
		current += rate;
		
		return DefaultStats.capValue(this, current);
	}

	@Override
	public ResourceLocation getStatID()
	{
		return HeatModifier.identifier;
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
	public StatRecord createNewRecord()
	{
		return new SimpleStatRecord(80F);
	}
	
	@Override
	public boolean isAcitve(EntityPlayer player)
	{
		return !(player.isCreative() || player.isSpectator());
	}
	
	public static void onHighTemperature(EntityPlayer player)
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
		return current * (1F + (float)ModConfig.HEAT.wetnessExchangeMultiplier * (stats.getStat(DefaultStats.WETNESS) / DefaultStats.WETNESS.getMaximum()));
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
	
	/**
	 * Armor Modifier takes care of storing and computing conductivity vectors.
	 * In short, conductivity vector is an array of (4) values, each associated
	 * with a specific armor piece. The values 0, 1, 2 and 3 are associated to
	 * the helmet, chestplate, leggings and boots respectively. When computing
	 * the conductivity of the armor set, the values from the stored vectors
	 * associated to armor materials are used as a multiplier to base conductivity (1F)
	 * @see #ArmorModifier(float[])
	 * @see #addArmorType(net.minecraft.item.ItemArmor.ArmorMaterial, float)
	 * @author Enginecrafter77
	 */
	public static class ArmorModifier implements StatEffect
	{
		/** The number of armor slots */
		protected static final int armorPieces = 4;
		
		/** A map mapping armor materials to their conductivity vectors */
		protected Map<ItemArmor.ArmorMaterial, Float[]> materialmap;
		
		/**
		 * When an armor modifier is created, it is initialized with a
		 * <i>conductivityDistribution</i> vector. Conductivity distribution
		 * serves as a base for creating each conductivityVectors. It describes
		 * the distribution of the base conductivity value among the armor. For
		 * example, a conductivity distribution vector {0.2, 0.35, 0.3, 0.15}
		 * tells us that the helmet contributes 20% to the conductivity, the
		 * chestplate contributes 35%, the leggings 30% and the boots 15%.
		 */
		protected final float[] conductivityDistribution;
		
		/**
		 * @see #conductivityDistribution
		 * @param conductivityDistribution The conductivity distribution vector
		 */
		public ArmorModifier(float[] conductivityDistribution)
		{
			this.materialmap = new HashMap<ItemArmor.ArmorMaterial, Float[]>();
			this.conductivityDistribution = conductivityDistribution;
		}
		
		/**
		 * Constructs new ArmorModifier using the configured distribution vector.
		 * @see #conductivityDistribution
		 * @param conductivityDistribution The conductivity distribution vector
		 */
		public ArmorModifier()
		{
			this.materialmap = new HashMap<ItemArmor.ArmorMaterial, Float[]>();
			
			float sum = 0;
			this.conductivityDistribution = new float[ArmorModifier.armorPieces];
			for(int index = 0; index < ArmorModifier.armorPieces; index++)
			{
				sum += (this.conductivityDistribution[index] = (float)ModConfig.HEAT.distributionVector[index]);
			}
			
			// Normalize the vector if it's not normal already
			if(sum != 1)
			{
				for(int index = 0; index < ArmorModifier.armorPieces; index++)
					this.conductivityDistribution[index] /= sum;
			}
		}
		
		/**
		 * Adds an armor type and computes the conductivity vector for it.
		 * The conductivity vector is computed using the following equation:
		 * <pre>
		 * 	y[i] = a ^ x[i]
		 * </pre>
		 * Where <b>y</b> is the resulting conductivity vector, <b>a</b> is the
		 * <i>conductivity</i> parameter, <b>x</b> is the {@link #conductivityDistribution}
		 * and <b>i</b> is the index of the armor piece.
		 * @param material The material to associate the conductivity with
		 * @param conductivity The conductivity of the material (i.e. the multiplier used when the armor set is homogeneous <i>material</i>)
		 */
		public void addArmorType(ItemArmor.ArmorMaterial material, float conductivity)
		{
			Float[] conductivityVectorInstance = new Float[armorPieces];
			for(int index = 0; index < armorPieces; index++)
				conductivityVectorInstance[index] = (float)Math.pow(conductivity, this.conductivityDistribution[index]);
			this.materialmap.put(material, conductivityVectorInstance);
		}
		
		@Override
		public float apply(EntityPlayer target, float current)
		{
			float buff = 1F;
			int index = 0;
			for(ItemStack stack : target.getArmorInventoryList())
			{
				if(stack.getItem() instanceof ItemArmor)
				{
					ItemArmor.ArmorMaterial material = ((ItemArmor)stack.getItem()).getArmorMaterial();
					if(this.materialmap.containsKey(material))
						buff *= this.materialmap.get(material)[index];
				}
				index++;
			}
			return current * buff;
		}
	}
}
