package enginecrafter77.survivalinc.stats.modifier.ng;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import enginecrafter77.survivalinc.stats.modifier.ng.FilteredEffectApplicator.EffectFilter;
import net.minecraft.entity.player.EntityPlayer;

public class FunctionalEffectFilter implements EffectFilter
{
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
	
	public EffectFilter invert()
	{
		return new FunctionalEffectFilter((EntityPlayer player, Float value) -> !this.delegate.test(player, value));
	}
}