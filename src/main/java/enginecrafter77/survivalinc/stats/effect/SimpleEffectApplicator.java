package enginecrafter77.survivalinc.stats.effect;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A very simple proof-of-concept {@link EffectApplicator} implementation.
 * SimpleEffectApplicator basically traverses it's elements each round, without
 * doing anything more. Unlike it's cousin {@link FilteredEffectApplicator},
 * it can't do cool tricks like effect filtering. However, it may still come in
 * handy if you need that performance.
 * @author Enginecrafter77
 */
public class SimpleEffectApplicator extends EffectApplicator {

	public final ArrayList<StatEffect> effects;
	
	public SimpleEffectApplicator()
	{
		super();
		this.effects = new ArrayList<StatEffect>();
	}
	
	public SimpleEffectApplicator(Collection<StatEffect> effects)
	{
		this();
		this.effects.addAll(effects);
	}
	
	public void add(StatEffect effect)
	{
		this.effects.add(effect);
	}

	@Override
	protected Collection<StatEffect> getEffectSet()
	{
		return this.effects;
	}

}
