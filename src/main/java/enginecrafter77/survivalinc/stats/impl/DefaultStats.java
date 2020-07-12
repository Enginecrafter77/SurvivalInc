package enginecrafter77.survivalinc.stats.impl;

import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import enginecrafter77.survivalinc.stats.modifier.ModifierApplicator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public enum DefaultStats implements StatProvider {
	WETNESS(0, 100, 0F),
	HYDRATION(0, 100),
	SANITY(0, 100);
	
	public final ModifierApplicator<EntityPlayer> modifiers;
	public final ResourceLocation id;
	private float max, min, def;
	
	private DefaultStats(float min, float max, float def)
	{
		this.modifiers = new ModifierApplicator<EntityPlayer>();
		this.id = new ResourceLocation(SurvivalInc.MOD_ID, this.name().toLowerCase());
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
		return DefaultStats.capValue(this, modifiers.apply(target, current));
	}
	
	@Override
	public ResourceLocation getStatID()
	{
		return this.id;
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
	public boolean isAcitve(EntityPlayer player)
	{
		return !(player.isCreative() || player.isSpectator());
	}
	
	public static float capValue(StatProvider provider, float current)
	{
		if(current > provider.getMaximum()) current = provider.getMaximum();
		if(current < provider.getMinimum()) current = provider.getMinimum();
		return current;
	}
}