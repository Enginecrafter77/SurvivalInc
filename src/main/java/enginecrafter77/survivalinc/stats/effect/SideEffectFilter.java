package enginecrafter77.survivalinc.stats.effect;

import enginecrafter77.survivalinc.stats.effect.FilteredEffectApplicator.EffectFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * A simple yet important {@link EffectFilter} implementation.
 * This effect filter makes sure the effect only gets run on
 * one side, either {@link Side.CLIENT} or {@link Side.SERVER}.
 * The check is done using world.isRemote, and so if the entered
 * side was client, the expected value of world.isRemote is true.
 * @author Enginecrafter77
 */
public class SideEffectFilter implements EffectFilter {

	/** The side the effect should only launch on */
	public final Side side;
	
	public SideEffectFilter(Side side)
	{
		this.side = side;
	}
	
	@Override
	public boolean isApplicableFor(EntityPlayer player, float value)
	{
		return side.isClient() == player.world.isRemote;
	}

}
