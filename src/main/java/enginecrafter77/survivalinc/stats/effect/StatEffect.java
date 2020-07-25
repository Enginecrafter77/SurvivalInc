package enginecrafter77.survivalinc.stats.effect;

import net.minecraft.entity.player.EntityPlayer;

/**
 * StatEffect is the base class of
 * the StatEffect API. StatEffect
 * describes a procedure which acts
 * based on the amount of a certain
 * stat, potentially rewriting it.
 * The StatEffect is usually used to
 * compute the change to a stat value,
 * although common usages also include
 * only acting upon the current value,
 * and returning it unchanged.
 * @author Enginecrafter77
 */
@FunctionalInterface
public interface StatEffect {
	
	/**
	 * Applies the current stat effect with the regards
	 * to the provided player, and the provided value
	 * of that. Please note that the values do NOT necessarily
	 * correspond to the player's stats.
	 * @param player The target player
	 * @param current The current value of the stat
	 * @return The new value to be assigned to a stat
	 */
	public float apply(EntityPlayer player, float current);
	
}
