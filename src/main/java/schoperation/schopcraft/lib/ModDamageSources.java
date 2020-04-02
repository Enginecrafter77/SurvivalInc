package schoperation.schopcraft.lib;

import net.minecraft.util.DamageSource;

public class ModDamageSources {

	/*
	 * A list of all damage sources in the mod.
	 */

	public static final DamageSource DEHYDRATION = new DamageSource("schopcraft_dehydration").setDamageIsAbsolute()
			.setDamageBypassesArmor();
	public static final DamageSource HYPERTHERMIA = new DamageSource("schopcraft_hyperthermia").setDamageIsAbsolute()
			.setDamageBypassesArmor();
	public static final DamageSource HYPOTHERMIA = new DamageSource("schopcraft_hypothermia").setDamageIsAbsolute()
			.setDamageBypassesArmor();
}