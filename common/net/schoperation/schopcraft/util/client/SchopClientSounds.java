package net.schoperation.schopcraft.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class SchopClientSounds {
	
	/*
	 * Client-only sound handling
	 * Mainly for insanity, ambiance, etc.
	 */
	
	// Main method to play sounds
	public static void playSound(String uuid, String soundMethod, double posX, double posY, double posZ) {
		
		// instance of Minecraft
		Minecraft mc = Minecraft.getMinecraft();
		
		// instance of the player
		EntityPlayer player = mc.player;
		
		// the dimension/world the player is in
		WorldClient world = mc.world;
		
		// position of sound
		BlockPos pos = new BlockPos(posX, posY, posZ);
		
		// is this the correct player?
		if (player.getCachedUniqueIdString().equals(uuid) && player.world.isRemote) {
			
			// determine what particles need to be summoned/spawned/rendered/i used a million ways to describe that process of making particles appear
			if (soundMethod.equals("EndermanSound")) { playEndermanSound(world, pos); }
			else if (soundMethod.equals("ZombieSound")) { playZombieSound(world, pos); }
			else if (soundMethod.equals("GhastSound")) { playGhastSound(world, pos); }
			else if (soundMethod.equals("ExplosionSound")) { playExplosionSound(world, pos); }
			else if (soundMethod.equals("StoneBreakSound")) { playStoneBreakSound(world, pos); }
			else if (soundMethod.equals("FireSound")) { playFireSound(world, pos); }
			else if (soundMethod.equals("VillagerSound")) { playVillagerSound(world, pos); }
			else if (soundMethod.equals("LavaSound")) { playLavaSound(world, pos); }
			else if (soundMethod.equals("InsanityAmbienceSound")) { playInsanityAmbienceSound(world, pos); }
			else if (soundMethod.equals("InsanityAmbienceSoundLoud")) { playInsanityAmbienceSoundLoud(world, pos); }
		}
	}
	// ===============================================================================
	//                Below sounds are mainly for INSANITY.
	// ===============================================================================
	
	// The sound of the enderman. Angry sound.
	private static void playEndermanSound(WorldClient world, BlockPos pos) {
		
		world.playSound(pos, SoundEvents.ENTITY_ENDERMEN_SCREAM	, SoundCategory.HOSTILE, 0.5f, 1.0f, false);
	}
	
	// The sound of a zombie. Dur, Bur, or Grr?
	private static void playZombieSound(WorldClient world, BlockPos pos) {
		
		world.playSound(pos, SoundEvents.ENTITY_ZOMBIE_AMBIENT, SoundCategory.HOSTILE, 0.5f, 1.0f, false);
	}
	
	// Ghast scream sound
	private static void playGhastSound(WorldClient world, BlockPos pos) {
		
		world.playSound(pos, SoundEvents.ENTITY_GHAST_AMBIENT, SoundCategory.HOSTILE, 0.5f, 1.0f, false);
	}
	
	// Explosion sound
	private static void playExplosionSound(WorldClient world, BlockPos pos) {
		
		world.playSound(pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 1.0f, 1.0f, false);
	}
	
	// Stone break sound
	private static void playStoneBreakSound(WorldClient world, BlockPos pos) {
		
		world.playSound(pos, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 0.5f, 1.0f, false);
	}
	
	// Fire sound
	private static void playFireSound(WorldClient world, BlockPos pos) {
		
		world.playSound(pos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5f, 1.0f, false);
	}
	
	// Villager sounds. hur hur hur huh heerr huh herr hurrrrrrr errrrrrrr
	private static void playVillagerSound(WorldClient world, BlockPos pos) {
		
		world.playSound(pos, SoundEvents.ENTITY_VILLAGER_AMBIENT, SoundCategory.NEUTRAL, 0.5f, 1.0f, false);
	}
	
	// Lava sounds blub blub pop plop plob blub bluh well actually just the ambient noises
	private static void playLavaSound(WorldClient world, BlockPos pos) {
		
		world.playSound(pos, SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.5f, 1.0f, false);
	}
	
	// Ambience of insanity
	private static void playInsanityAmbienceSound(WorldClient world, BlockPos pos) {
		
		world.playSound(pos, SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.AMBIENT, 0.1f, 0.2f, false);
	}
	
	// Louder version of insanity ambience
	private static void playInsanityAmbienceSoundLoud(WorldClient world, BlockPos pos) {
		
		world.playSound(pos, SoundEvents.ENTITY_WITHER_DEATH, SoundCategory.AMBIENT, 0.5f, 0.2f, false);
	}
}
