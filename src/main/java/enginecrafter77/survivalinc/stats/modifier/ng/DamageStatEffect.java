package enginecrafter77.survivalinc.stats.modifier.ng;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public class DamageStatEffect implements StatEffect {
	
	protected final DamageSource source;
	protected float amount;
	protected int cooldown;
	
	public DamageStatEffect(DamageSource source, float amount, int cooldown)
	{
		this.cooldown = cooldown;
		this.amount = amount;
		this.source = source;
	}
	
	public float getAmount()
	{
		return this.amount;
	}

	@Override
	public float apply(EntityPlayer target, float current)
	{
		if(cooldown == 0 || target.ticksExisted % cooldown == 0)
		{
			target.attackEntityFrom(source, amount);
		}
		return current;
	}
}
