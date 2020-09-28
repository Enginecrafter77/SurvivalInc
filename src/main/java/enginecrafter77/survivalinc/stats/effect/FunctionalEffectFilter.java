package enginecrafter77.survivalinc.stats.effect;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import net.minecraft.entity.player.EntityPlayer;

/**
 * FunctionalEffectFilter provides an elegant way of wrapping lambda
 * functions, while providing an {@link #invert()} method, which
 * allows for inverting of the filter. FunctionalEffectFilter is an
 * immutable object. As such, methods like {@link #invert()} return
 * a new instance each time they're called.
 * @author Enginecrafter77
 */
public class FunctionalEffectFilter<RECORD> implements EffectFilter<RECORD> {
	/** The function this effect filter delegates to */
	public final BiPredicate<RECORD, EntityPlayer> delegate;
	
	/** If true, inverts the value returned by the predicate */
	public final boolean inverted;
	
	public FunctionalEffectFilter(BiPredicate<RECORD, EntityPlayer> delegate, boolean inverted)
	{
		this.delegate = delegate;
		this.inverted = inverted;
	}
	
	public FunctionalEffectFilter(BiPredicate<RECORD, EntityPlayer> delegate)
	{
		this(delegate, false);
	}
	
	@Override
	public boolean isApplicableFor(RECORD record, EntityPlayer player)
	{
		return this.delegate.test(record, player) != inverted;
	}
	
	/**
	 * Constructs a new FunctionalEffectFilter with inverted value output. Since
	 * FunctionalEffectFilter is immutable, this method returns a new instance each time.
	 * @return An EffectFilter returning exactly the opposite values for each matching inputs
	 */
	public FunctionalEffectFilter<RECORD> invert()
	{
		return new FunctionalEffectFilter<RECORD>(this.delegate, !this.inverted);
	}
	
	/**
	 * Constructs a new FunctionalEffectFilter with check based solely on the stat value.
	 * @param check The predicate checking the stat's value
	 * @return A new instance of FunctionalEffectFilter
	 */
	public static <RECORD> FunctionalEffectFilter<RECORD> byRecord(Predicate<RECORD> check)
	{
		return new FunctionalEffectFilter<RECORD>((RECORD record, EntityPlayer player) -> check.test(record));
	}
	
	public static FunctionalEffectFilter<SimpleStatRecord> byValue(Predicate<Float> check)
	{
		return new FunctionalEffectFilter<SimpleStatRecord>((SimpleStatRecord record, EntityPlayer player) -> check.test(record.getValue()));
	}
	
	/**
	 * Constructs a new FunctionalEffectFilter with check based solely on the {@link EntityPlayer player entity}.
	 * @param check The predicate checking the player entity
	 * @return A new instance of FunctionalEffectFilter
	 */
	public static FunctionalEffectFilter<Object> byPlayer(Predicate<EntityPlayer> check)
	{
		return new FunctionalEffectFilter<Object>((Object record, EntityPlayer player) -> check.test(player));
	}
}