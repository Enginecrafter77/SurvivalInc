package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.impl.DefaultStats;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.modifier.ModifierApplicator;
import net.minecraft.entity.player.EntityPlayer;

public class GhostEnergy extends ModifierApplicator<EntityPlayer> implements StatProvider {
	private static final long serialVersionUID = -2088047893866334112L;
	
	public static final GhostEnergy instance = new GhostEnergy();
	
	private GhostEnergy() {}
	
	@Override
	public float updateValue(EntityPlayer target, float current)
	{
		return DefaultStats.capValue(this, this.apply(target, current));
	}

	@Override
	public String getStatID()
	{
		return "ghostenergy";
	}

	@Override
	public float getMaximum()
	{
		return 100F;
	}

	@Override
	public float getMinimum()
	{
		return 0F;
	}

	@Override
	public StatRecord createNewRecord()
	{
		return new SimpleStatRecord();
	}

	@Override
	public boolean isAcitve(EntityPlayer player)
	{
		Ghost ghost = player.getCapability(GhostProvider.target, null);
		return ghost.getStatus();
	}
}
