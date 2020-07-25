package enginecrafter77.survivalinc.stats.modifier.ng;

import java.util.LinkedList;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.google.common.collect.Range;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class EffectFilterWrapper extends LinkedList<BiPredicate<EntityPlayer, Float>> implements StatEffect {
	
	private static final long serialVersionUID = -8376569386443358956L;
	
	public final StatEffect base;
	
	public EffectFilterWrapper(StatEffect base)
	{
		this.base = base;
	}
	
	public EffectFilterWrapper valueIs(Predicate<Float> predicate)
	{
		this.add((EntityPlayer player, Float value) -> predicate.test(value));
		return this;
	}
	
	public EffectFilterWrapper inRange(Range<Float> range)
	{
		return this.valueIs(range);
	}
	
	public EffectFilterWrapper withChance(float probability)
	{
		this.add((EntityPlayer player, Float value) -> player.world.rand.nextFloat() < probability);
		return this;
	}
	
	@Override
	public float apply(EntityPlayer player, float current)
	{
		// Run all the tests
		for(BiPredicate<EntityPlayer, Float> test : this)
		{
			if(!test.test(player, current))
			{
				return current;
			}
		}
		
		current = this.base.apply(player, current);
		return current;
	}

	@Override
	public Side sideOnly()
	{
		return base.sideOnly();
	}	
}