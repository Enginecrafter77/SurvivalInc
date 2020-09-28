package enginecrafter77.survivalinc.stats.effect;

import net.minecraft.entity.player.EntityPlayer;

/**
 * EffectFilter is a simple functional interface used
 * by {@link EffectApplicator}, which is used to check
 * whether the application of an effect is viable for
 * the specified situation and subject.
 * @author Enginecrafter77
 */
@FunctionalInterface
public interface EffectFilter<RECORD> {
	/**
	 * @param record The stored and manipulated record
	 * @param player The player the stat effect will be applied on
	 * @return true if the stat effect should be applied, false otherwise
	 */
	public boolean isApplicableFor(RECORD record, EntityPlayer player);
}