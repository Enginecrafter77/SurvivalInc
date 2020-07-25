package enginecrafter77.survivalinc.stats.modifier.ng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public abstract class EffectApplicator implements StatEffect {
	
	private ArrayList<StatEffect> slice;
	private int perround;
	
	private Iterator<StatEffect> iterator;
	
	public EffectApplicator()
	{
		this.setEffectsPerRound(0);
	}
	
	protected abstract Collection<StatEffect> getEffectSet();
	
	protected Iterator<StatEffect> newIterator()
	{
		return this.getEffectSet().iterator();
	}
	
	protected StatEffect getNextEffect()
	{
		if(this.iterator == null || !this.iterator.hasNext())
			this.iterator = this.newIterator();
		return this.iterator.next();
	}
	
	public final void setEffectsPerRound(int perround)
	{
		this.slice = new ArrayList<StatEffect>(perround);
		this.perround = perround;
	}
	
	public Collection<StatEffect> nextRound()
	{
		Collection<StatEffect> set = this.getEffectSet();
		if(this.perround <= 0 || this.perround > set.size()) return set;
		else
		{
			for(int index = 0; index < this.perround; index++)
			{
				this.slice.set(index, this.getNextEffect());
			}
			
			return slice;
		}
	}
	
	protected boolean checkSide(World world, StatEffect effect)
	{
		Side side = effect.sideOnly();
		return side == null || (side.isClient() == world.isRemote);
	}
	
	protected float applyEffect(StatEffect effect, EntityPlayer player, float current)
	{
		return effect.apply(player, current);
	}
	
	@Override
	public float apply(EntityPlayer player, float current)
	{
		Collection<StatEffect> effects = this.nextRound();
		for(StatEffect effect : effects)
		{
			if(this.checkSide(player.world, effect))
			{
				current = this.applyEffect(effect, player, current);
			}
		}
		return current;
	}

	@Override
	public Side sideOnly()
	{
		return null;
	}
}
