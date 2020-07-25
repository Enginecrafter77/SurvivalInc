package enginecrafter77.survivalinc.stats.effect;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import enginecrafter77.survivalinc.stats.effect.FilteredEffectApplicator.EffectFilter;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Much like {@link FunctionalEffect}, this class uses
 * lambdas to implement a simple {@link EffectFilter}.
 * Moreover, this interface provides a {@link #invert()}
 * method.
 * @author Enginecrafter77
 */
public class FunctionalEffectFilter implements EffectFilter {
	/** The function this effect filter delegates to */
	public BiPredicate<EntityPlayer, Float> delegate;
	
	public FunctionalEffectFilter(BiPredicate<EntityPlayer, Float> delegate)
	{
		this.delegate = delegate;
	}
	
	public FunctionalEffectFilter(Predicate<Float> check)
	{
		this.delegate = (EntityPlayer player, Float value) -> check.test(value);
	}
	
	@Override
	public boolean isApplicableFor(EntityPlayer player, float value)
	{
		return this.delegate.test(player, value);
	}
	
	/**
	 * @return An EffectFilter returning exactly the opposite values for each matching inputs
	 */
	public EffectFilter invert()
	{
		return new FunctionalEffectFilter((EntityPlayer player, Float value) -> !this.delegate.test(player, value));
	}
}