package enginecrafter77.survivalinc;

import net.minecraft.util.DamageSource;

public class ModDamageSources {

	/*
	 * A list of all damage sources in the mod.
	 */

	public static final DamageSource DEHYDRATION = new DamageSource("survivalinc_dehydration").setDamageIsAbsolute()
			.setDamageBypassesArmor();
	public static final DamageSource HYPERTHERMIA = new DamageSource("survivalinc_hyperthermia").setDamageIsAbsolute()
			.setDamageBypassesArmor();
	public static final DamageSource HYPOTHERMIA = new DamageSource("survivalinc_hypothermia").setDamageIsAbsolute()
			.setDamageBypassesArmor();
}