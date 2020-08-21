package enginecrafter77.survivalinc.stats.impl;

import java.util.HashMap;
import java.util.Map;

import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.effect.StatEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

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
public class ArmorModifier implements StatEffect {
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