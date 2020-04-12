package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.OverflowHandler;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.modifier.ConditionalModifier;
import enginecrafter77.survivalinc.stats.modifier.ModifierApplicator;
import enginecrafter77.survivalinc.stats.modifier.OperationType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class GhostImpl implements Ghost, StatProvider {
	private static final long serialVersionUID = -3704889805293574354L;
	
	public static final ModifierApplicator<EntityPlayer> calc = new ModifierApplicator<EntityPlayer>();
	
	protected boolean status;
	protected float energy;
	private boolean dirty;
	
	public GhostImpl()
	{
		this.energy = this.getDefault();
		this.status = false;
		this.dirty = false;
	}
	
	@Override
	public void setStatus(boolean status)
	{
		this.status = status;
	}
	
	@Override
	public void applyStatus(EntityPlayer player, boolean status)
	{
		GameType gamemode = status ? GameType.ADVENTURE : GameType.SURVIVAL;
		player.setGameType(gamemode);
	}
	
	@Override
	public boolean getStatus()
	{
		return this.status;
	}
	
	@Override
	public StatProvider getEnergyProvider()
	{
		return this;
	}
	
	@Override
	public void update(EntityPlayer player)
	{
		if(dirty)
		{
			this.applyStatus(player, status);
			this.dirty = false;
		}
		
		if(status)
			this.energy = this.updateValue(player, this.energy);
	}

	@Override
	public float updateValue(EntityPlayer target, float current)
	{
		current = calc.apply(target, current);
		return this.getOverflowHandler().apply(this.getEnergyProvider(), current);
	}
	
	@Override
	public float getEnergy()
	{
		return this.energy;
	}
	
	@Override
	public float setEnergy(float energy)
	{
		return this.energy = energy;
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
	public float getDefault()
	{
		return this.getMinimum();
	}

	@Override
	public OverflowHandler getOverflowHandler()
	{
		return OverflowHandler.CAP;
	}
	
	public static void register()
	{
		GhostImpl.calc.put(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> !player.world.isDaytime(), 0.05F), OperationType.OFFSET);
		GhostImpl.calc.put(new ConditionalModifier<EntityPlayer>((EntityPlayer player) -> player.isSprinting(), -0.2F), OperationType.OFFSET);
	}
	
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerRespawnEvent event)
	{
		EntityPlayer player = event.player;
		if(!(player.world.isRemote || event.isEndConquered()))
		{
			Ghost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
			if(ModConfig.MECHANICS.enableGhost)
				ghost.setStatus(true);
		}
	}
}