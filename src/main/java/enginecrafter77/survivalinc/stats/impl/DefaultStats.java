package enginecrafter77.survivalinc.stats.impl;

import enginecrafter77.survivalinc.stats.OverflowHandler;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.modifier.ModifierApplicator;
import net.minecraft.entity.player.EntityPlayer;

public enum DefaultStats implements StatProvider {
	WETNESS(0, 100, 0F),
	HYDRATION(0, 100),
	SANITY(0, 100);
	
	public final ModifierApplicator<EntityPlayer> modifiers;
	private float max, min, def;
	
	private DefaultStats(float min, float max, float def)
	{
		this.modifiers = new ModifierApplicator<EntityPlayer>();
		this.min = min;
		this.max = max;
		this.def = def;
	}
	
	private DefaultStats(float min, float max)
	{
		this(min, max, max * 0.75F);
	}
	
	@Override
	public float updateValue(EntityPlayer target, float current)
	{
		return modifiers.apply(target, current);
	}
	
	@Override
	public String getStatID()
	{
		return this.name().toLowerCase();
	}

	@Override
	public float getMaximum()
	{
		return this.max;
	}

	@Override
	public float getMinimum()
	{
		return this.min;
	}
	
	@Override
	public StatRecord createNewRecord()
	{
		return new SimpleStatRecord(this.def);
	}

	@Override
	public OverflowHandler getOverflowHandler()
	{
		return OverflowHandler.CAP;
	}

	@Override
	public boolean isAcitve(EntityPlayer player)
	{
		return !(player.isCreative() || player.isSpectator());
	}
}