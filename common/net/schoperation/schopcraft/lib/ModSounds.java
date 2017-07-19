package net.schoperation.schopcraft.lib;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.schoperation.schopcraft.SchopCraft;

public class ModSounds {
	
	/*
	 * A list of all custom sounds added to the game.
	 */
	
	public static final SoundEvent[] SOUNDS = {
			
				new SoundEvent(new ResourceLocation(SchopCraft.MOD_ID, "fan_whoosh")).setRegistryName(SchopCraft.MOD_ID, "fan_whoosh")
	};
}
