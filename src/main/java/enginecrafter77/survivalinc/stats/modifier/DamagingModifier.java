package enginecrafter77.survivalinc.stats.modifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public class DamagingModifier implements Modifier<EntityPlayer> {
	
	protected final DamageSource source;
	protected float amount;
	protected int cooldown;
	
	public DamagingModifier(DamageSource source, float amount, int cooldown)
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
	public boolean shouldTrigger(EntityPlayer target, float level)
	{
		return !target.world.isRemote && (cooldown == 0 || target.ticksExisted % cooldown == 0);
	}

	@Override
	public float apply(EntityPlayer target, float current)
	{
		target.attackEntityFrom(source, amount);
		return current;
	}
}
