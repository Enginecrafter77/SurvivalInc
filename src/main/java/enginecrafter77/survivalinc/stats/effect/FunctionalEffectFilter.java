package enginecrafter77.survivalinc.stats.effect;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import enginecrafter77.survivalinc.stats.effect.FilteredEffectApplicator.EffectFilter;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Much like {@link FunctionalEffect}, this class uses
 * lambdas to implement a simple {@link EffectFilter}.
 * Moreover, this interface provides a {@link #invert()}
 * method. FunctionalEffectFilter is an immutable object.
 * As such, methods like {@link #invert()} return a new
 * instance each time they're called.
 * @author Enginecrafter77
 */
public class FunctionalEffectFilter implements EffectFilter {
	/** The function this effect filter delegates to */
	public final BiPredicate<EntityPlayer, Float> delegate;
	
	/** If true, inverts the value returned by the predicate */
	public final boolean inverted;
	
	public FunctionalEffectFilter(BiPredicate<EntityPlayer, Float> delegate, boolean inverted)
	{
		this.delegate = delegate;
		this.inverted = inverted;
	}
	
	public FunctionalEffectFilter(BiPredicate<EntityPlayer, Float> delegate)
	{
		this(delegate, false);
	}
	
	@Override
	public boolean isApplicableFor(EntityPlayer player, float value)
	{
		return this.delegate.test(player, value) != inverted;
	}
	
	/**
	 * Constructs a new FunctionalEffectFilter with inverted value output. Since
	 * FunctionalEffectFilter is immutable, this method returns a new instance each time.
	 * @return An EffectFilter returning exactly the opposite values for each matching inputs
	 */
	public FunctionalEffectFilter invert()
	{
		return new FunctionalEffectFilter(this.delegate, !this.inverted);
	}
	
	/**
	 * Constructs a new FunctionalEffectFilter with check based solely on the stat value.
	 * @param check The predicate checking the stat's value
	 * @return A new instance of FunctionalEffectFilter
	 */
	public static FunctionalEffectFilter byValue(Predicate<Float> check)
	{
		return new FunctionalEffectFilter((EntityPlayer player, Float value) -> check.test(value));
	}
	
	/**
	 * Constructs a new FunctionalEffectFilter with check based solely on the {@link EntityPlayer player entity}.
	 * @param check The predicate checking the player entity
	 * @return A new instance of FunctionalEffectFilter
	 */
	public static FunctionalEffectFilter byPlayer(Predicate<EntityPlayer> check)
	{
		return new FunctionalEffectFilter((EntityPlayer player, Float value) -> check.test(player));
	}
}