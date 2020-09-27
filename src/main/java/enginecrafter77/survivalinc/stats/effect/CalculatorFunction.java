package enginecrafter77.survivalinc.stats.effect;

import net.minecraft.entity.player.EntityPlayer;

@FunctionalInterface
public interface CalculatorFunction {
	public float apply(EntityPlayer player, float value);
}
