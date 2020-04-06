package schoperation.schopcraft.cap.stat;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.entity.player.EntityPlayer;
import schoperation.schopcraft.config.SchopConfig;

public enum DefaultStats implements StatProvider {
	HEAT(SchopConfig.MECHANICS.temperatureScale, 0, 120, 75),
	HYDRATION(SchopConfig.MECHANICS.thirstScale, 0, 100),
	SANITY(SchopConfig.MECHANICS.sanityScale, 0, 100);
	
	public final float scale;
	
	public final Set<Function<EntityPlayer, Float>> situational_modifiers;
	
	//TODO implement this
	//public final Map<Float, PotionEffect[]> debuffs;
	private float max, min, def;
	
	private DefaultStats(double scale, float min, float max, float def)
	{
		this.situational_modifiers = new HashSet<Function<EntityPlayer, Float>>();
		//this.debuffs = new HashMap<Float, PotionEffect[]>();
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
		float mod = 0;
		for(Function<EntityPlayer, Float> entry : this.situational_modifiers)
			mod += entry.apply(target);
		return current + (this.scale * mod);
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
	
	public void addConditionalModifier(Predicate<EntityPlayer> check, float value)
	{
		this.situational_modifiers.add((EntityPlayer player) -> check.test(player) ? value : 0);
	}
}