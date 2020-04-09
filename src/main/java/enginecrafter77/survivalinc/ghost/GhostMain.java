package enginecrafter77.survivalinc.ghost;

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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import enginecrafter77.survivalinc.ModBlocks;
import enginecrafter77.survivalinc.config.ModConfig;
import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;
import enginecrafter77.survivalinc.util.SchopServerEffects;
import enginecrafter77.survivalinc.util.SchopServerParticles;
import enginecrafter77.survivalinc.util.SchopServerSounds;

/**
 * On death, instead of respawning out of thin air, you'll become a ghost. You're quite limited on what you can do. You can't hold items, can't open GUIs, can't craft, etc.
 * However, you can't die (again). The other stats (temperature, thirst, etc.) stay constant, so don't worry about those. 
 * Instead, there's an energy bar that appears above the health + hunger bar. It's pretty much "ghost stamina", like the stuff they say in those ghost hunting shows ("Man, I'm feeling this energy!" "Use my energy!" "HOLY SH*T!!!!")
 * You can use that energy to move around quicker, or resurrect yourself. 
 */
public class GhostMain {

	// Mark the player as a ghost upon death.
	public void onPlayerRespawn(EntityPlayer player)
	{

		// Ghost capability.
		IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);

		// Make them a ghost (if enabled)
		if(ModConfig.MECHANICS.enableGhost)
		{
			ghost.create();
			
			// Put them in adventure mode.
			player.setGameType(GameType.ADVENTURE);
		}
	}

	// This is in place for the resurrection to be delayed properly.
	private int resurrectionTimer = -1;

	// The main method.
	public void onPlayerUpdate(EntityPlayer player)
	{
		// Capabilities.
		StatTracker stat = player.getCapability(StatRegister.CAPABILITY, null);
		IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
		
		// Block position of player.
		BlockPos pos = player.getPosition();

		// Cached UUID.
		String uuid = player.getCachedUniqueIdString();

		// While the player is a ghost.
		if (ghost.status())
		{
			for(Map.Entry<StatProvider, Float> provider : stat)
				stat.setStat(provider.getKey(), provider.getKey().getDefault());
			
			// Give ghosts invisibility and invincibility.
			SchopServerEffects.affectPlayer(uuid, "invisibility", 20, 0, false, false);
			SchopServerEffects.affectPlayer(uuid, "resistance", 20, 4, false, false);
			
			// ==========================================
			// ENERGY (Measured in Ghastly Plasmatic Units)
			// ==========================================
			
			// Increases at night!
			if(!player.world.isDaytime())
			{
				ghost.addEnergy(0.05f);
			}
			
			// Decreases while sprinting!
			if(player.isSprinting())
			{
				ghost.addEnergy(-0.2f);
			}
			
			// Disable sprinting below 15, and re-enable it above 30.
			if(ghost.getEnergy() < 15.0f)
			{
				player.getFoodStats().setFoodLevel(6);
			}

			else if(ghost.getEnergy() > 30.0f)
			{
				player.getFoodStats().setFoodLevel(20);
			}

			// Manifest yourself with particles. Cheap, but cool.
			if(ghost.getEnergy() >= 90.0f)
			{
				SchopServerParticles.summonParticle(uuid, "GhostParticles", player.posX, player.posY, player.posZ);
			}

			// Attribute modifier stuff for speed. More energy = more speed.
			// Does the player have existing attributes with the same name?
			// Remove them.
			// Iterate through all of modifiers. If one of them is a ghost one,
			// delete it so another one can take its place.
			Iterator<AttributeModifier> speedModifiers = player
					.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifiers().iterator();

			// Speed.
			while (speedModifiers.hasNext())
			{

				AttributeModifier element = speedModifiers.next();

				if (element.getName().equals("ghostSpeedBuff"))
				{

					player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(element);
				}
			}

			// Scale the speed modifier based on current energy.
			double speedBuffAmount = (ghost.getEnergy() - 50) * 0.002;

			// The modifier itself.
			AttributeModifier speedBuff = new AttributeModifier("ghostSpeedBuff", speedBuffAmount, 0);

			// Apply the modifier.
			if (ghost.getEnergy() > 50.0f)
			{

				player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(speedBuff);
			}

			// ========================================
			// RESURRECTION
			// ========================================

			// In order for a player to be resurrected, they must be standing IN
			// a lucid block, surrounded by 4 soul sand with torches on top.
			// Also, a golden apple must be placed at your feet.

			// Variable to keep track of how many "steps" closer the ghost is to
			// resurrection.
			int resurrectionProgress = 0;

			// Look for a lucid block at their feet.
			if(player.world.getBlockState(pos).getBlock() == ModBlocks.LUCID_BLOCK.get())
			{

				resurrectionProgress++;
			}

			// Look for soul sand near the player (3 blocks away) on each
			// cardinal direction. There must also be a torch on top of the
			// sand.
			// Positive X
			if (player.world.getBlockState(new BlockPos(pos.getX() + 3, pos.getY(), pos.getZ()))
					.getBlock() == Blocks.SOUL_SAND)
			{

				if (player.world.getBlockState(new BlockPos(pos.getX() + 3, pos.getY() + 1, pos.getZ()))
						.getBlock() == Blocks.TORCH)
				{

					resurrectionProgress++;
				}
			}

			// Negative X
			if (player.world.getBlockState(new BlockPos(pos.getX() - 3, pos.getY(), pos.getZ()))
					.getBlock() == Blocks.SOUL_SAND)
			{

				if (player.world.getBlockState(new BlockPos(pos.getX() - 3, pos.getY() + 1, pos.getZ()))
						.getBlock() == Blocks.TORCH)
				{

					resurrectionProgress++;
				}
			}

			// Positive Z
			if (player.world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 3))
					.getBlock() == Blocks.SOUL_SAND)
			{

				if (player.world.getBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ() + 3))
						.getBlock() == Blocks.TORCH)
				{

					resurrectionProgress++;
				}
			}

			// Negative Z
			if (player.world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 3))
					.getBlock() == Blocks.SOUL_SAND)
			{

				if (player.world.getBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ() - 3))
						.getBlock() == Blocks.TORCH)
				{

					resurrectionProgress++;
				}
			}

			// Now for that golden apple.
			// EntityItems around the player
			AxisAlignedBB boundingBox = player.getEntityBoundingBox().grow(1, 1, 1);
			List<EntityItem> nearbyItems = player.world.getEntitiesWithinAABB(EntityItem.class, boundingBox);

			// Now iterate through the items and see if one of them is a golden
			// apple.
			for (int num = 0; num < nearbyItems.size(); num++)
			{

				// The chosen EntityItem
				EntityItem entityItem = (EntityItem) nearbyItems.get(num);

				// ItemStack
				ItemStack stack = (ItemStack) entityItem.getItem();

				if(ItemStack.areItemsEqual(stack, new ItemStack(Items.GOLDEN_APPLE)))
				{

					resurrectionProgress++;
				}
			}

			// Does the ghost have 100 GPUs?
			if (ghost.getEnergy() == 100.0f)
			{

				resurrectionProgress++;
			}

			// Enough "resurrection points" to continue?
			if (resurrectionProgress == 7)
			{

				// Fire the resurrection process.
				startResurrection(player, pos);

				// take away energy so this isn't repeated
				ghost.setEnergy(0.0f);
			}

			// Start resurrection timer if startResurrection() says so.
			// Also spawn the particles.
			if (resurrectionTimer >= 0)
			{

				// Increment
				resurrectionTimer++;

				// Particle methods
				SchopServerParticles.summonParticle(uuid, "ResurrectionFlameParticles", pos.getX(), pos.getY(),
						pos.getZ());
				SchopServerParticles.summonParticle(uuid, "ResurrectionEnchantmentParticles", pos.getX(), pos.getY(),
						pos.getZ());
			}

			// Finish resurrection if resurrectionTimer is 100. Stop the timer.
			// If any of the components of the process are missing (excluding
			// the energy one), stop the process, to prevent any possible
			// exploits.
			if (resurrectionTimer >= 100 && resurrectionProgress >= 6)
			{

				finishResurrection(player, pos);
				resurrectionTimer = -1;
			}

			else if (resurrectionProgress < 6)
			{

				resurrectionTimer = -1;
			}
		}
	}

	// This method starts the legitimate resurrection process.
	private void startResurrection(EntityPlayer player, BlockPos pos)
	{

		// Player cached UUID.
		String uuid = player.getCachedUniqueIdString();

		// Do portal sound.
		SchopServerSounds.playSound(uuid, "PortalSound", pos.getX(), pos.getY(), pos.getZ());

		// Start timer which'll resurrect the player at the end of the main
		// method.
		resurrectionTimer = 0;
	}

	// Finish resurrection!
	private void finishResurrection(EntityPlayer player, BlockPos pos)
	{
		StatTracker stat = player.getCapability(StatRegister.CAPABILITY, null);
		IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);

		// Summon lightning on the player.
		EntityLightningBolt lightning = new EntityLightningBolt(player.world, pos.getX(), pos.getY(), pos.getZ(), true);
		player.world.addWeatherEffect(lightning);

		// Basic stuff
		ghost.resurrect();
		player.setGameType(GameType.SURVIVAL);
		player.getFoodStats().setFoodLevel(10);
		ghost.setEnergy(0.0f);

		// Reset stats
		for(Map.Entry<StatProvider, Float> provider : stat)
			stat.setStat(provider.getKey(), provider.getKey().getDefault());
		
		// The lucid block is used up. Delete it.
		// TODO: Once finite torches are added, the torches should blow out as well.
		player.world.setBlockToAir(pos);

		// Destroy the golden apple.
		// EntityItems around the player
		AxisAlignedBB boundingBox = player.getEntityBoundingBox().grow(1, 1, 1);
		List<EntityItem> nearbyItems = player.world.getEntitiesWithinAABB(EntityItem.class, boundingBox);

		// Now iterate through the items and see if one of them is a golden
		// apple.
		for(int num = 0; num < nearbyItems.size(); num++)
		{
			// The chosen EntityItem
			EntityItem entityItem = (EntityItem) nearbyItems.get(num);

			// ItemStack
			ItemStack stack = (ItemStack) entityItem.getItem();

			if(ItemStack.areItemsEqual(stack, new ItemStack(Items.GOLDEN_APPLE)))
			{
				entityItem.setDead();
			}
		}
	}
}