package enginecrafter77.survivalinc.stats.effect;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayer;

/**
 * FunctionalEffec is, well, a simple StatEffect
 * implementation which uses lambdas to quickly
 * implement StatEffect. This is very useful in
 * case of using standalone static methods using
 * the StatEffect signature without creating a
 * separate subclasses for each method.
 * @author Enginecrafter77
 */
public class FunctionalEffect implements StatEffect {
	
	/** The functional target */
	public final BiFunction<EntityPlayer, Float, Float> target;
	
	/**
	 * Creates a value-rewritting functional effect.
	 * @param target The target function
	 */
	public FunctionalEffect(BiFunction<EntityPlayer, Float, Float> target)
	{
		this.target = target;
	}
	
	/**
	 * Creates a read-only functional effect. This effect takes
	 * both the player and the value as parameters.
	 * @param target The target function
	 */
	public FunctionalEffect(BiConsumer<EntityPlayer, Float> target)
	{
		this(new BiFunction<EntityPlayer, Float, Float>() {
			@Override
			public Float apply(EntityPlayer player, Float value)
			{
				target.accept(player, value);
				return value;
			}
		});
	}
	
	/**
	 * Creates a read-only functional effect. This effect takes
	 * only the player argument, thus making it suitable for
	 * cases when the current value is not really needed.
	 * @param target The target value
	 */
	public FunctionalEffect(Consumer<EntityPlayer> target)
	{
		this(new BiFunction<EntityPlayer, Float, Float>() {
			@Override
			public Float apply(EntityPlayer player, Float value)
			{
				target.accept(player);
				return value;
			}
		});
	}
	
	@Override
	public float apply(EntityPlayer player, float current)
	{
		return this.target.apply(player, current);
	}
}
