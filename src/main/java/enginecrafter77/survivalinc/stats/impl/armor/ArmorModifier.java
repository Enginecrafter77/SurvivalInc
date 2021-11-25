package enginecrafter77.survivalinc.stats.impl.armor;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import enginecrafter77.survivalinc.stats.effect.CalculatorFunction;
import enginecrafter77.survivalinc.stats.impl.HeatModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;

/**
 * ArmorModifier is a {@link CalculatorFunction} designed to be used inside of {@link HeatModifier}. The ArmorModifier
 * class takes care of computing the exchange rate multiplier caused by wearing different types of armor. The individual
 * conductivity of each armor piece is based on 2 things: the base conductivity of a material and the {@link #cdcvector
 * distribution vector}.
 * 
 * The thermal exchange multiplier is calculated using the following formula:
 *
 * <pre>
 * e(x) = x * PROD(b ^ d)
 * </pre>
 * 
 * Where x is the input conductivity, b is the base material conductivity and d is the distribution vector. For example,
 * image that the player wears helmet and chestplate of material X with conductivity 0.5, and leggings and boots of
 * material Y with conductivity 1.5. Let's say the distribution vector is [0.2, 0.3, 0.2, 0.1]. Then, the equation would
 * look something like this:
 * 
 * <pre>
 * e(x) = x * (0.5^0.2 * 0.5^0.3 * 1.5^0.2 * 1.5^0.1) = <u>0.79856 * x</u>
 * </pre>
 *
 * @see ConductivityDistributionVector
 * @see #setMaterialConductivity(net.minecraft.item.ItemArmor.ArmorMaterial, float)
 * @author Enginecrafter77
 */
public class ArmorModifier implements CalculatorFunction {
	/** The number of armor slots */
	protected static final int armorPieces = 4;
	
	/** A map mapping armor materials to their conductivity bases */
	protected final Map<ItemArmor.ArmorMaterial, Float> materialmap;
	
	/** The instance of {@link ConductivityDistributionVector} used in the calculation */
	protected ConductivityDistributionVector cdcvector;
	
	public ArmorModifier(ConductivityDistributionVector cdcvector)
	{
		this.materialmap = new EnumMap<ItemArmor.ArmorMaterial, Float>(ItemArmor.ArmorMaterial.class);
		this.cdcvector = cdcvector;
	}
	
	/**
	 * Sets the internally used {@link ConductivityDistributionVector}.
	 * @param cdcvector The vector to be used in the calculations
	 */
	public void setDistributionVector(ConductivityDistributionVector cdcvector)
	{
		this.cdcvector = cdcvector;
	}
	
	/**
	 * @return The internally used {@link ConductivityDistributionVector}.
	 */
	public ConductivityDistributionVector getDistributionVector()
	{
		return this.cdcvector;
	}
	
	/**
	 * @return An iterator iterating the mappings of {@link ArmorMaterial}s to their base conductivities.
	 */
	public Iterator<Map.Entry<ItemArmor.ArmorMaterial, Float>> iterateMap()
	{
		return this.materialmap.entrySet().iterator();
	}
	
	/**
	 * Returns the base conductivity of the provided material, or null if the specified material doesn't have one
	 * associated.
	 * @param material
	 * @return
	 */
	@Nullable
	public Float getMaterialBaseConductivity(ItemArmor.ArmorMaterial material)
	{
		return this.materialmap.get(material);
	}
	
	/**
	 * Sets the base conductivity of the specified material.
	 * @param material The material which is going to be associated with the conductivity value
	 * @param conductivity The base conductivity value of the given material
	 */
	public void setMaterialConductivity(ItemArmor.ArmorMaterial material, float conductivity)
	{
		this.materialmap.put(material, conductivity);
	}
	
	/**
	 * Removes the mapping of the specified material from the internal map.
	 * @param material The material to clear mapping for.
	 */
	public void unregisterMaterial(ItemArmor.ArmorMaterial material)
	{
		this.materialmap.remove(material);
	}
	
	@Override
	public float apply(EntityPlayer target, float current)
	{
		float buff = 1F;
		for(Map.Entry<ArmorPiece, ItemStack> entry : ArmorPiece.armorInventory(target))
		{
			Item itemtype = entry.getValue().getItem();
			if(itemtype instanceof ItemArmor)
			{
				ItemArmor.ArmorMaterial material = ((ItemArmor)itemtype).getArmorMaterial();
				Float value = this.getMaterialBaseConductivity(material);
				if(value != null) buff *= Math.pow(value, this.cdcvector.getValueFor(entry.getKey()));
			}
		}
		return current * buff;
	}
	
	/**
	 * ConductivityDistributionVector is a class that determines what share do individual {@link ArmorPiece armor pieces} on
	 * the resultant armor conductivity. In it's default implementation, ConductivityDistributionVector is an immutable
	 * class. It's instances are created using {@link #of(Map)} method.
	 * @see #of(Map)
	 * @author Enginecrafter77
	 */
	public static class ConductivityDistributionVector implements Iterable<Map.Entry<ArmorPiece, Float>> {
		/** The backing map storing the vector values */
		protected final Map<ArmorPiece, Float> vector;
		
		/**
		 * Creates the ConductivityDistributionVector using the provided backing map.
		 * @param vector The backing map of the vector
		 */
		protected ConductivityDistributionVector(Map<ArmorPiece, Float> vector)
		{
			this.vector = vector;
		}
		
		@Override
		public Iterator<Entry<ArmorPiece, Float>> iterator()
		{
			return this.vector.entrySet().iterator();
		}
		
		/**
		 * Returns the conductivity share of the provided armor piece.
		 * @param piece The armor piece to query the conductivity of.
		 * @return The conductivity share of the given armor piece.
		 */
		public float getValueFor(ArmorPiece piece)
		{
			return this.vector.get(piece);
		}
		
		/**
		 * Normalizes a Map containing vector. Normalization in this context means that the sum of individual vector components
		 * cannot be greater than 1. To achieve this, a sum of the vector's components is computed, and then each of the
		 * vector's components is divided by this sum. This method does NOT modify the provided map. Rather, a consumer is
		 * provided as a second argument. This consumer accepts the already normalized vector components. In case you want to
		 * actually modify the source map, simply pass
		 * 
		 * <pre>
		 * vector::put
		 * </pre>
		 * 
		 * as the sink parameter.
		 * @param <KEY> The type of keys, can be ignored because the compiler infers it automatically
		 * @param vector The source vector containing the value mappings
		 * @param sink The consumer which accepts the normalized vector components in an orderely manner.
		 */
		protected static <KEY> void normalize(Map<KEY, Float> vector, BiConsumer<? super KEY, ? super Float> sink)
		{
			// Normalize the vector if it's not normal already
			float sum = 0F;
			
			for(float val : vector.values())
				sum += val;
			
			if(sum > 1.0F)
			{
				for(Map.Entry<KEY, Float> entry : vector.entrySet())
					sink.accept(entry.getKey(), entry.getValue() / sum);
			}
		}
		
		/**
		 * Creates an immutable instance of {@link ConductivityDistributionVector} using the provided vector.
		 * @param vector The source vector
		 * @return A new immutable instance of {@link ConductivityDistributionVector} representing data from the given map.
		 */
		public static ConductivityDistributionVector of(Map<ArmorPiece, Float> vector)
		{
			ImmutableMap.Builder<ArmorPiece, Float> builder = ImmutableMap.builder();
			ConductivityDistributionVector.normalize(vector, builder::put);
			return new ConductivityDistributionVector(builder.build());
		}
	}
}
