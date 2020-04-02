package schoperation.schopcraft.cap.vital;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import schoperation.schopcraft.config.SchopConfig;

public enum VitalStatType {
	HYDRATION(SchopConfig.MECHANICS.thirstScale, 0, 100),
	SANITY(SchopConfig.MECHANICS.sanityScale, 0, 100);
	
	// Tempararily disabled
	//HEAT(SchopConfig.MECHANICS.temperatureScale, 0, 100);
	
	public final float scale;
	
	public final Set<Function<EntityPlayer, Float>> situational_modifiers;
	
	public final Map<Float, PotionEffect[]> debuffs;
	public float max, min;
	
	private VitalStatType(double scale, float min, float max)
	{
		this.situational_modifiers = new HashSet<Function<EntityPlayer, Float>>();
		this.debuffs = new HashMap<Float, PotionEffect[]>();
		this.scale = (float)scale;
		this.min = min;
		this.max = max;
	}
	
	private VitalStatType(double scale, float min, float max, float staticdrain)
	{
		this(scale, min, max);
		this.situational_modifiers.add((EntityPlayer player) -> staticdrain);
	}
	
	public void addSituationalModifier(Predicate<EntityPlayer> check, float value)
	{
		this.situational_modifiers.add((EntityPlayer player) -> check.test(player) ? value : 0);
	}
	
	public Float perTickDifference(EntityPlayer target)
	{
		float mod = 0;
		for(Function<EntityPlayer, Float> entry : this.situational_modifiers)
			mod += entry.apply(target);
		return this.scale * mod;
	}
	
	public String getStatID()
	{
		return this.name().toLowerCase();
	}
}