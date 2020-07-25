package enginecrafter77.survivalinc.stats.effect;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayer;

public class FunctionalEffect implements StatEffect {

	public final BiFunction<EntityPlayer, Float, Float> target;
	
	public FunctionalEffect(BiFunction<EntityPlayer, Float, Float> target)
	{
		this.target = target;
	}
	
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
