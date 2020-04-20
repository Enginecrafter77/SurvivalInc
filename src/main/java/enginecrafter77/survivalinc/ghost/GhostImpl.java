package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.stats.StatProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class GhostImpl implements Ghost {	
	protected boolean status;
	private boolean dirty;
	
	public GhostImpl()
	{
		this.status = false;
		this.dirty = false;
	}
	
	@Override
	public void setStatus(boolean status)
	{
		this.status = status;
		this.dirty = true;
	}
	
	@Override
	public void applyStatus(EntityPlayer player, boolean status)
	{
		player.setGameType(status ? GameType.ADVENTURE : GameType.SURVIVAL);
		player.capabilities.allowFlying = status;
	}
	
	@Override
	public boolean getStatus()
	{
		return this.status;
	}
	
	@Override
	public StatProvider getEnergyProvider()
	{
		return GhostEnergy.instance;
	}
	
	@Override
	public void update(EntityPlayer player)
	{
		if(dirty)
		{
			this.applyStatus(player, status);
			this.dirty = false;
		}
	}
}