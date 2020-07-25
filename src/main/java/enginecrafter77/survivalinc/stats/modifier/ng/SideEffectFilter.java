package enginecrafter77.survivalinc.stats.modifier.ng;

import enginecrafter77.survivalinc.stats.modifier.ng.FilteredEffectApplicator.EffectFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class SideEffectFilter implements EffectFilter {

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
