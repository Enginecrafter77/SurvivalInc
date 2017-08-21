package net.schoperation.schopcraft.cap.ghost;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.schoperation.schopcraft.cap.sanity.ISanity;
import net.schoperation.schopcraft.cap.sanity.SanityProvider;
import net.schoperation.schopcraft.cap.temperature.ITemperature;
import net.schoperation.schopcraft.cap.temperature.TemperatureProvider;
import net.schoperation.schopcraft.cap.thirst.IThirst;
import net.schoperation.schopcraft.cap.thirst.ThirstProvider;
import net.schoperation.schopcraft.cap.wetness.IWetness;
import net.schoperation.schopcraft.cap.wetness.WetnessProvider;
import net.schoperation.schopcraft.lib.ModBlocks;
import net.schoperation.schopcraft.util.SchopServerEffects;
import net.schoperation.schopcraft.util.SchopServerParticles;
import net.schoperation.schopcraft.util.SchopServerSounds;

/*
 * On death, instead of respawning out of thin air, you'll become a ghost. You're quite limited on what you can do. You can't hold items, can't open GUIs, can't craft, etc.
 * However, you can't die (again). The other stats (temperature, thirst, etc.) stay constant, so don't worry about those. 
 * Instead, there's an energy bar that appears above the health + hunger bar. It's pretty much "ghost stamina", like the stuff they say in those ghost hunting shows ("Man, I'm feeling this energy!" "Use my energy!" "HOLY SH*T!!!!")
 * You can use that energy to move around quicker, or resurrect yourself. 
 */

public class GhostMain {
	
	// Mark the player as a ghost upon death.
	public static void onPlayerRespawn(EntityPlayer player) {
		
		// Ghost capability
		IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
		
		// Make them a ghost
		ghost.setGhost();
		
		// Put them in adventure mode.
		player.setGameType(GameType.ADVENTURE);
	}
	
	// This is in place for the resurrection to be delayed properly.
	private static int resurrectionTimer = -1;
	
	// Main method
	public static void onPlayerUpdate(EntityPlayer player) {
		
		// Capabilities
		IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
		IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
		ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
		ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
		IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
		
		// Block position of player
		BlockPos pos = player.getPosition();
		
		// Cached UUID
		String uuid = player.getCachedUniqueIdString();
		
		// Server side & while they're a ghost
		if (!player.world.isRemote && ghost.isGhost()) {
			
			// Constantly set the other values to default. Ghosts don't worry about that crap.
			wetness.set(0.0f);
			thirst.set(100.0f);
			sanity.set(100.0f);
			temperature.set(68.0f);
			
			// Give ghosts invisibility and invincibility.
			SchopServerEffects.affectPlayer(uuid, "invisibility", 20, 0, false, false);
			SchopServerEffects.affectPlayer(uuid, "resistance", 20, 4, false, false);
			
			// ==========================================
			//  ENERGY (Measured in Ghastly Plasmic Units)
			// ==========================================
			
			// Increases at night
			if (!player.world.isDaytime()) {
				
				ghost.increaseEnergy(0.05f);
			}
			
			// Decreases while sprinting.
			if (player.isSprinting()) {
				
				ghost.decreaseEnergy(0.2f);	
			}
			
			// Disable sprinting below 15, and re-enable it above 30.
			if (ghost.getEnergy() < 15.0f) {
					
				player.getFoodStats().setFoodLevel(6);
			}
			
			else if (ghost.getEnergy() > 30.0f) {
				
				player.getFoodStats().setFoodLevel(20);
			}
			
			// Become visible (with particles)
			if (ghost.getEnergy() >= 90.0f) {
				
				SchopServerParticles.summonParticle(uuid, "GhostParticles", player.posX, player.posY, player.posZ);
			}
			
			// Attribute modifier stuff for speed. More energy = more speed
			// Does the player have existing attributes with the same name? Remove them.
			// Iterate through all of modifiers. If one of them is a ghost one, delete it so another one can take its place.
			Iterator<AttributeModifier> speedModifiers = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifiers().iterator();
			
			// Speed
			while (speedModifiers.hasNext()) {
				
				AttributeModifier element = speedModifiers.next();
				
				if (element.getName().equals("ghostSpeedBuff")) {
					
					player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(element);
				}
			}
			
			// Scale the speed modifier based on current energy.
			double speedBuffAmount = (ghost.getEnergy() - 50) * 0.002;
			
			// The modifier itself.
			AttributeModifier speedBuff = new AttributeModifier("ghostSpeedBuff", speedBuffAmount, 0);
			
			// Apply the modifier.
			if (ghost.getEnergy() > 50.0f) {
				
				player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(speedBuff);
			}
			
			// ========================================
			//              RESURRECTION
			// ========================================
			
			// In order for a player to be resurrected, they must be standing IN a lucid block, surrounded by 4 soul sand with torches on top.
			// Also, a golden apple must be placed at your feet.
			
			// Variable to keep track of how many "steps" or things the ghost has to resurrection.
			int resurrectionProgress = 0;
			
			// Look for a lucid block at their feet.
			if (player.world.getBlockState(pos).getBlock() == ModBlocks.LUCID_BLOCK) {
				
				resurrectionProgress++;
			}
			
			// Look for soul sand near the player (3 blocks away) on each cardinal direction.
			// Positive X
			if (player.world.getBlockState(new BlockPos(pos.getX()+3, pos.getY(), pos.getZ())).getBlock() == Blocks.SOUL_SAND) {
				
				// Torch?
				if (player.world.getBlockState(new BlockPos(pos.getX()+3, pos.getY()+1, pos.getZ())).getBlock() == Blocks.TORCH) {
					
					resurrectionProgress++;
				}
			}
			
			// Negative X
			if (player.world.getBlockState(new BlockPos(pos.getX()-3, pos.getY(), pos.getZ())).getBlock() == Blocks.SOUL_SAND) {
				
				// Torch?
				if (player.world.getBlockState(new BlockPos(pos.getX()-3, pos.getY()+1, pos.getZ())).getBlock() == Blocks.TORCH) {
					
					resurrectionProgress++;
				}
			}
			
			// Positive Z
			if (player.world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ()+3)).getBlock() == Blocks.SOUL_SAND) {
				
				// Torch?
				if (player.world.getBlockState(new BlockPos(pos.getX(), pos.getY()+1, pos.getZ()+3)).getBlock() == Blocks.TORCH) {
					
					resurrectionProgress++;
				}
			}
			
			// Negative Z
			if (player.world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ()-3)).getBlock() == Blocks.SOUL_SAND) {
				
				// Torch?
				if (player.world.getBlockState(new BlockPos(pos.getX(), pos.getY()+1, pos.getZ()-3)).getBlock() == Blocks.TORCH) {
					
					resurrectionProgress++;
				}
			}
			
			// Now for that golden apple.
			// EntityItems around the player
			AxisAlignedBB boundingBox = player.getEntityBoundingBox().grow(1, 1, 1);
			List nearbyItems = player.world.getEntitiesWithinAABB(EntityItem.class, boundingBox);
			
			// Now iterate through the items and see if one of them is a golden apple.
			for (int num = 0; num < nearbyItems.size(); num++) {
				
				// the chosen EntityItem
				EntityItem entityItem = (EntityItem) nearbyItems.get(num);
				
				// ItemStack
				ItemStack stack = (ItemStack) entityItem.getItem();
				
				if (stack.areItemsEqual(stack, new ItemStack(Items.GOLDEN_APPLE))) {
					
					resurrectionProgress++;
				}
			}
			
			// Does the ghost have 100 GPUs?
			if (ghost.getEnergy() == 100.0f) {
				
				resurrectionProgress++;
			}
			
			// Enough "resurrection points" to continue?
			if (resurrectionProgress == 7) {
				
				// fire method
				startResurrection(player, pos);
				
				// take away energy so this isn't repeated
				ghost.setEnergy(0.0f);
			}
			
			// Start resurrection timer if startResurrection() says so.
			// Also spawn the particles.
			if (resurrectionTimer >= 0) {
				
				// increment
				resurrectionTimer++;
				
				// particle methods
				SchopServerParticles.summonParticle(uuid, "ResurrectionFlameParticles", pos.getX(), pos.getY(), pos.getZ());
				SchopServerParticles.summonParticle(uuid, "ResurrectionEnchantmentParticles", pos.getX(), pos.getY(), pos.getZ());
			}
			
			// Finish resurrection if resurrectionTimer is 100. Stop the timer.
			if (resurrectionTimer >= 100) {
				
				finishResurrection(player, pos);
				resurrectionTimer = -1;
			}
		}
	}
	
	// This method starts the legit resurrection process
	private static void startResurrection(EntityPlayer player, BlockPos pos) {
		
		// Player cached UUID
		String uuid = player.getCachedUniqueIdString();
		
		// Do portal sound
		SchopServerSounds.playSound(uuid, "PortalSound", pos.getX(), pos.getY(), pos.getZ());
		
		// Start timer which'll resurrect the player at the end of the main method.
		resurrectionTimer = 0;
	}
	
	// Finish resurrection
	private static void finishResurrection(EntityPlayer player, BlockPos pos) {
		
		// Capabilities
		IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
		IThirst thirst = player.getCapability(ThirstProvider.THIRST_CAP, null);
		ISanity sanity = player.getCapability(SanityProvider.SANITY_CAP, null);
		ITemperature temperature = player.getCapability(TemperatureProvider.TEMPERATURE_CAP, null);
		IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
		
		// Summon lightning on the player
		EntityLightningBolt lightning = new EntityLightningBolt(player.world, pos.getX(), pos.getY(), pos.getZ(), true);
		player.world.addWeatherEffect(lightning);
		
		// Basic stuff
		ghost.setAlive();
		player.setGameType(GameType.SURVIVAL);
		player.getFoodStats().setFoodLevel(10);
		ghost.setEnergy(0.0f);
		
		// Reset stats
		temperature.set(50.0f);
		thirst.set(75.0f);
		sanity.set(60.0f);
		wetness.set(0.0f);
		
		// The lucid block is used up. Delete it.
		// TODO: Once finite torches are added, the torches should blow out as well.
		player.world.setBlockToAir(pos);
		
		// Destroy the golden apple.
		// EntityItems around the player
		AxisAlignedBB boundingBox = player.getEntityBoundingBox().grow(1, 1, 1);
		List nearbyItems = player.world.getEntitiesWithinAABB(EntityItem.class, boundingBox);
		
		// Now iterate through the items and see if one of them is a golden apple.
		for (int num = 0; num < nearbyItems.size(); num++) {
			
			// the chosen EntityItem
			EntityItem entityItem = (EntityItem) nearbyItems.get(num);
			
			// ItemStack
			ItemStack stack = (ItemStack) entityItem.getItem();
			
			if (stack.areItemsEqual(stack, new ItemStack(Items.GOLDEN_APPLE))) {
				
				entityItem.setDead();
			}
		}
	}
}
