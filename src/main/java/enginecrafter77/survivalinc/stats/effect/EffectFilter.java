package enginecrafter77.survivalinc.stats.effect;

import net.minecraft.entity.player.EntityPlayer;

/**
 * A simple interface which is used to check
 * whether a stat effect is applicable or not.
 * @author Enginecrafter77
 */
@FunctionalInterface
public interface EffectFilter<RECORD> {
	/**
	 * @param player The player the stat effect will be run for
	 * @param value The current value of the stat
	 * @return True if the stat effect should run, false otherwise
	 */
	public boolean isApplicableFor(RECORD record, EntityPlayer player);
}