package enginecrafter77.survivalinc.season.melting;

/**
 * MeltAction represents the possible actions
 * that may be performed on a melting block.
 *
 * @author Enginecrafter77
 */
public enum MeltAction {
	/**
	 * Causes the block to undergo freezing, i.e. it's melt phase decreases
	 */
	FREEZE,

	/**
	 * Does not modify the block's melt phase
	 */
	PASS,

	/**
	 * Causes the block to undergo melting, i.e. it's melt phase increases
	 */
	MELT;

	/**
	 * @return The increment to the melt phase this action causes.
	 */
	public int getPhaseIncrement()
	{
		return this.ordinal() - 1;
	}
}
