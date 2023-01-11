package enginecrafter77.survivalinc.stats.effect;

import enginecrafter77.survivalinc.stats.StatRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

/**
 * DamageStatEffect describes a {@link StatEffect}
 * which should inflict a damage from a specified
 * {@link DamageSource}. It's as simple as that.
 * @author Enginecrafter77
 */
public class DamageStatEffect implements StatEffect<StatRecord> {
	
	/** The source/type of the damage */
	protected final DamageSource source;
	
	/** The amount of damage per hit */
	protected final float amount;
	
	/** The cooldown of the attack, in minecraft ticks */
	protected final int cooldown;
	
	public DamageStatEffect(DamageSource source, float amount, int cooldown)
	{
		this.cooldown = cooldown;
		this.amount = amount;
		this.source = source;
	}
	
	/**
	 * @return The damage dealt per second by this {@link DamageStatEffect}
	 */
	public float getDPS()
	{
		return (20F * this.amount) / this.cooldown;
	}
	
	/**
	 * @return The amount of damage this effect inflicts
	 */
	public float getAmount()
	{
		return this.amount;
	}

	@Override
	public void apply(StatRecord record, EntityPlayer player)
	{
		if(player.world.isRemote) return;
		
		if(this.cooldown == 0 || player.ticksExisted % this.cooldown == 0)
		{
			player.attackEntityFrom(this.source, this.amount);
		}
	}
}
