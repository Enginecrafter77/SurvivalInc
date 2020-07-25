package enginecrafter77.survivalinc.stats.modifier.ng;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.entity.player.EntityPlayer;

public class FunctionalEffect implements StatEffect {

	public final BiFunction<EntityPlayer, Float, Float> target;
	
	public FunctionalEffect(BiFunction<EntityPlayer, Float, Float> target)
	{
		this.target = target;
	}
	
	public FunctionalEffect(Function<EntityPlayer, Float> target)
	{
		this((EntityPlayer player, Float current) -> target.apply(player));
	}
	
	public FunctionalEffect(Consumer<EntityPlayer> target)
	{
		this(new BiFunction<EntityPlayer, Float, Float>() {
			@Override
			public Float apply(EntityPlayer t, Float u)
			{
				target.accept(t);
				return u;
			}
		});
	}
	
	@Override
	public float apply(EntityPlayer player, float current)
	{
		return this.target.apply(player, current);
	}
}
