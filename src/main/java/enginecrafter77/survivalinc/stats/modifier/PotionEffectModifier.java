package enginecrafter77.survivalinc.stats.modifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Potion effect modifier is designed as a demo implementation
 * of modifier. Despite this, it is still a fully functional modifier
 * implementation, ready to be used in the code where feasible.
 * @author Enginecrafter77
 */
public class PotionEffectModifier implements Modifier<EntityPlayer>
{
	/** The duration after which the potion effect (when applicable) is reset to double this time */
	public static final int duration = 100;
	
	/** The effect to apply */
	public final Potion effect;
	
	/** The amplifier of the effect */
	public int amplifier;
	
	/** Determines whether the potion particles will be visible */
	public boolean visible;
	
	public PotionEffectModifier(Potion effect, int amplifier)
	{
		this.amplifier = amplifier;
		this.effect = effect;
		this.visible = false;
	}
	
	@Override
	public boolean shouldTrigger(EntityPlayer player, float level)
	{
		PotionEffect poteff = player.getActivePotionEffect(effect);
		return poteff == null || poteff.getDuration() < duration;
	}

	@Override
	public float apply(EntityPlayer target, float current)
	{
		target.addPotionEffect(new PotionEffect(effect, duration * 2, amplifier, false, this.visible));
		return 0;
	}
}