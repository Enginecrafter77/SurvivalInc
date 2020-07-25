package enginecrafter77.survivalinc.stats.modifier.ng;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

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
	 * 
	 * The code inside this function is only ever executed
	 * on {@link Side} specified by {@link #sideOnly()}.
	 * @param player The target player
	 * @param current The current value of the stat
	 * @return The new value to be assigned to a stat
	 */
	public float apply(EntityPlayer player, float current);
	
	/**
	 * Specifies whether this effect should run on one
	 * side only. If this method returns null, the stat
	 * will be applied on both sides. Please note that
	 * it is highly recommended to use null whenever the
	 * stat effect should modify the value of the stat,
	 * or else client and server's representation of
	 * stat values will differ.
	 * @return The side this stat should run on.
	 */
	public Side sideOnly();
	
}
