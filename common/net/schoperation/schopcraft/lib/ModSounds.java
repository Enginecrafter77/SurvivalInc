package net.schoperation.schopcraft.lib;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.schoperation.schopcraft.SchopCraft;

public class ModSounds {
	
	/*
	 * A list of all custom sounds added to the game.
	 */
	
	// This list is for easy referencing.
	
	public static final SoundEvent FAN_WHOOSH = new SoundEvent(new ResourceLocation(SchopCraft.MOD_ID, "fan_whoosh")).setRegistryName(SchopCraft.MOD_ID, "fan_whoosh");
	
	// This list is for actually registering the sounds.
	public static final SoundEvent[] SOUNDS = {
			
				FAN_WHOOSH
	};
}
