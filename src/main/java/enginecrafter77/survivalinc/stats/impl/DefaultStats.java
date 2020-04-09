package enginecrafter77.survivalinc.stats.impl;

import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.OverflowHandler;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.util.ModifierCalculator;
import net.minecraft.entity.player.EntityPlayer;

public enum DefaultStats implements StatProvider {
	HYDRATION(ModConfig.MECHANICS.thirstScale, 0, 100),
	SANITY(ModConfig.MECHANICS.sanityScale, 0, 100),
	WETNESS(ModConfig.MECHANICS.wetnessScale, 0, 100, 0F);
	
	public final float scale;
	
	public final ModifierCalculator<EntityPlayer> modifiers;
	private float max, min, def;
	
	private DefaultStats(double scale, float min, float max, float def)
	{
		this.modifiers = new ModifierCalculator<EntityPlayer>();
		this.scale = (float)scale;
		this.min = min;
		this.max = max;
		this.def = def;
	}
	
	private DefaultStats(double scale, float min, float max)
	{
		this(scale, min, max, max * 0.75F);
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
	public float getDefault()
	{
		return this.def;
	}

	@Override
	public OverflowHandler getOverflowHandler()
	{
		return OverflowHandler.CAP;
	}
}