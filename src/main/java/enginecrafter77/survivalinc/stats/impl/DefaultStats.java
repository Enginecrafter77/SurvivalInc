package enginecrafter77.survivalinc.stats.impl;

import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRecord;
import enginecrafter77.survivalinc.stats.effect.FilteredEffectApplicator;

import com.google.common.collect.Range;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.stats.SimpleStatRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public enum DefaultStats implements StatProvider {
	WETNESS(0F, 100F, 0F),
	HYDRATION(0F, 100F),
	SANITY(0F, 100F);
	
	public final FilteredEffectApplicator effects;
	public final ResourceLocation id;
	public final float max, min, def;
	
	private DefaultStats(float min, float max, float def)
	{
		this.effects = new FilteredEffectApplicator();
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
	public void update(EntityPlayer player, StatRecord record)
	{
		if(!(player.isCreative() || player.isSpectator()))
		{
			SimpleStatRecord srec = (SimpleStatRecord)record;
			srec.setValue(effects.apply(player, srec.getValue()));
		}
	}
	
	@Override
	public ResourceLocation getStatID()
	{
		return this.id;
	}
	
	@Override
	public StatRecord createNewRecord()
	{
		SimpleStatRecord record = new SimpleStatRecord(Range.closed(this.min, this.max));
		record.setValue(this.def);
		return record;
	}
}