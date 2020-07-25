package enginecrafter77.survivalinc.stats.modifier.ng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class RoundRobinApplicator extends EffectApplicator {

	public final ArrayList<StatEffect> effects;
	protected final ArrayList<StatEffect> slice;
	protected final int perround;
	private Iterator<StatEffect> iterator;
	
	public RoundRobinApplicator(int perround)
	{
		this.slice = new ArrayList<StatEffect>(perround);
		this.effects = new ArrayList<StatEffect>();
		this.perround = perround;
	}
	
	public RoundRobinApplicator()
	{
		this(0);
	}
	
	public RoundRobinApplicator(Collection<StatEffect> effects, int perround)
	{
		this(perround);
		this.effects.addAll(effects);
	}
	
	public RoundRobinApplicator(Collection<StatEffect> effects)
	{
		this();
		this.effects.addAll(effects);
	}
	
	protected Iterator<StatEffect> getIterator()
	{
		if(this.iterator == null || !this.iterator.hasNext())
			this.iterator = this.effects.iterator();
		return this.iterator;
	}
	
	public void add(StatEffect effect)
	{
		this.effects.add(effect);
	}
	
	@Override
	public Collection<StatEffect> nextRound()
	{
		if(this.perround <= 0 || this.perround > this.effects.size()) return this.effects;
		else
		{
			for(int index = 0; index < this.perround; index++)
			{
				this.slice.set(index, this.getIterator().next());
			}
			
			return slice;
		}
	}

}
