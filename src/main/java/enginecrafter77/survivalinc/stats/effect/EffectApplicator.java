package enginecrafter77.survivalinc.stats.effect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;

/**
 * EffectApplicator is a class which takes care of
 * applying a set of {@link StatEffect effects} to
 * a stat. EffectApplicators are also StatEffect
 * in themselves, which allows for easy nesting of
 * effects applicators in other applicators.
 * @author Enginecrafter77
 */
public abstract class EffectApplicator implements StatEffect {
	
	/** A buffer for putting effects that will be applied next round */
	private final ArrayList<StatEffect> slice;
	
	/**
	 * A number indicating the amount of effects applied next round.
	 * If the number is positive and less than the size of collection
	 * returned by {@link #getEffectSet()}, then this number corresponds
	 * to the number of elements contained in collection returned by
	 * {@link #nextRound()}.
	 * @see #nextRound()
	 */
	private int perround;
	
	/** An iterator used to traverse the collection returned by {@link #getEffectSet()} */
	private Iterator<StatEffect> iterator;
	
	/**
	 * Creates an effect applicator with the effective
	 * slice size of 0. If you need the applicator to
	 * work with slices, use {@link #setEffectsPerRound(int)}
	 */
	public EffectApplicator()
	{
		this.slice = new ArrayList<StatEffect>(0);
		this.perround = 0;
	}
	
	/**
	 * A getter method used to access the implementation's
	 * collection of registered stat effects.
	 * @return A collection of all contained {@link StatEffect}.
	 */
	protected abstract Collection<StatEffect> getEffectSet();
	
	/**
	 * Creates a new iterator to the implementation's {@link StatEffect}
	 * collection. Please note that the iterator has to be a fresh new
	 * instance of an Iterator for the aforementioned collection. Passing
	 * a reference to an existing "exhausted" iterator will result in undefined behavior.
	 * @return A new iterator instance to the implementation's {@link StatEffect} collection.
	 */
	protected Iterator<StatEffect> newIterator()
	{
		return this.getEffectSet().iterator();
	}
	
	/**
	 * Returns a next effect in line from the implementation's collection of {@link StatEffect}.
	 * @return A next effect from the stat effects using the internal iterator.
	 */
	protected StatEffect getNextEffect()
	{
		if(this.iterator == null || !this.iterator.hasNext())
			this.iterator = this.newIterator();
		return this.iterator.next();
	}
	
	/**
	 * Sets the number of effects in a slice returned each round.
	 * @param perround The number of elements in slice
	 */
	public final void setEffectsPerRound(int perround)
	{
		this.perround = perround;
		
		// Ensure new capacity, saving CPU cycles the next time the array list is filled
		if(perround > this.perround) this.slice.ensureCapacity(perround);
	}
	
	/**
	 * @return A collection of effects that should be applied next round.
	 */
	public Collection<StatEffect> nextRound()
	{
		Collection<StatEffect> set = this.getEffectSet();
		if(this.perround <= 0 || this.perround > set.size()) return set;
		else
		{
			this.slice.clear();
			
			for(int index = 0; index < this.perround; index++)
			{
				this.slice.add(index, this.getNextEffect());
			}
			
			return slice;
		}
	}
	
	/**
	 * Applies the specified effect to the player
	 * @param effect The effect to be applied
	 * @param player The player to apply the effect for
	 * @param current The current value of the stat
	 * @return The new value for the stat
	 */
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
			current = this.applyEffect(effect, player, current);
		}
		return current;
	}
}
