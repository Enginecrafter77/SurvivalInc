package enginecrafter77.survivalinc.stats.modifier.ng;

import java.util.ArrayList;
import java.util.Collection;

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
