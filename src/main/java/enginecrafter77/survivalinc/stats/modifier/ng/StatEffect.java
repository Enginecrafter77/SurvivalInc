package enginecrafter77.survivalinc.stats.modifier.ng;

import net.minecraft.entity.player.EntityPlayer;

/**
 * StatEffect is the base of all applicable effect.
 * StatEffect basically describes the change to stat
 * which should occur when certain conditions are met.
 * StatEffects are often used to calculate the changes
 * to stats, rather than modifying them directly.
 * @author Enginecrafter77
 */
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
