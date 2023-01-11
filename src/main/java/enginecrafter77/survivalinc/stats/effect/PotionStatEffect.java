package enginecrafter77.survivalinc.stats.effect;

import enginecrafter77.survivalinc.stats.StatRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Potion stat effect is an effect which applies
 * a specified {@link PotionEffect} on a player.
 * The PotionStatEffect makes sure the specified
 * potion effect stays active as long as this effect
 * is being continuously applied. When the potion
 * effect timer goes below {@link #resetThreshold}
 * ticks and this effect is still being applied, the
 * potion effect resets to the {@link #durationMax}
 * ticks.
 * @author Enginecrafter77
 */
public class PotionStatEffect implements StatEffect<StatRecord> {
	/** The effect to apply */
	public final Potion effect;
	
	/** The amplifier of the effect */
	public final int amplifier;
	
	/** Determines whether the potion particles will be visible */
	public boolean visible;
	
	/**
	 * The fraction of the maximum duration after which the
	 * effects resets to max. In other words, it is the number
	 * which when multiplied by {@link #durationMax} returns
	 * the minimum time an effect may last after the {@link StatEffect}
	 * no longer applies.
	 */
	public float resetThreshold;
	
	/**
	 * The maximum duration a potion effect may last
	 * after the {@link StatEffect} no longer applies.
	 */
	public int durationMax;
	
	public PotionStatEffect(Potion effect, int amplifier)
	{
		this.resetThreshold = 0.25F;
		this.amplifier = amplifier;
		this.durationMax = 100;
		this.effect = effect;
		this.visible = false;
	}
	
	/**
	 * Sets whether the potion effect's "bubbles" (more like swirls) can be seen.
	 * @param visible True if the particles should be visible, false otherwise
	 * @return Instance of this class for easier chaining
	 */
	public PotionStatEffect setVisible(boolean visible)
	{
		this.visible = visible;
		return this;
	}
	
	/**
	 * @see #durationMax
	 * @param duration The maximum time the potion effect may last
	 * @return Instance of this class for easier chaining
	 */
	public PotionStatEffect setDuration(int duration)
	{
		this.durationMax = duration;
		return this;
	}
	
	/**
	 * @see #resetThreshold
	 * @param threshold A fraction of {@link #durationMax} indicating the minimum time the potion effect may last
	 * @return Instance of this class for easier chaining
	 */
	public PotionStatEffect setResetThreshold(float threshold)
	{
		this.resetThreshold = threshold;
		return this;
	}

	@Override
	public void apply(StatRecord record, EntityPlayer player)
	{
		if(player.world.isRemote) return;
		
		PotionEffect poteff = player.getActivePotionEffect(effect);
		if(poteff == null || poteff.getDuration() <= Math.round((float)durationMax * resetThreshold))
		{
			player.addPotionEffect(new PotionEffect(effect, durationMax, amplifier, false, this.visible));
		}
	}
}
