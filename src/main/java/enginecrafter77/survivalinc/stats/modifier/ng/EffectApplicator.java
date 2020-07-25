package enginecrafter77.survivalinc.stats.modifier.ng;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public abstract class EffectApplicator implements StatEffect {
	
	public abstract Collection<StatEffect> nextRound();
	
	protected boolean checkSide(World world, StatEffect effect)
	{
		Side side = effect.sideOnly();
		return side == null || (side.isClient() == world.isRemote);
	}
	
	@Override
	public float apply(EntityPlayer player, float current)
	{
		Collection<StatEffect> effects = this.nextRound();
		for(StatEffect effect : effects)
		{
			if(this.checkSide(player.world, effect))
			{
				current = effect.apply(player, current);
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
