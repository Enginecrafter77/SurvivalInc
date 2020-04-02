package schoperation.schopcraft.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ProximityDetect {

	/*
	 * This is called whenever the mod wants to detect a block near the player.
	 * narf These methods should only be called on the server unless you like
	 * crashes.
	 */

	/*
	 * ========================================================= SAME Y-LEVEL AS
	 * PLAYER ==========================================================
	 */

	// Returns true if the block is right next to the player,
	// horizontally/diagonally.
	public static boolean isBlockNextToPlayer(int posX, int posY, int posZ, Block block, Entity player)
	{

		// Basic variables.
		World world = player.world;
		boolean result = false;
		int x = 0;
		int z = 0;

		// Just iterate through all of the possible positions around the player!
		// CODING!
		// I've redone this four times already, and this one is more
		// straightforward and NOT AS tedious.
		for (int pos = 0; pos < 9; pos++)
		{

			// These are the block positions; kek is the one being looped
			// through.
			Block kek = world.getBlockState(new BlockPos(posX + x, posY, posZ + z)).getBlock();

			if (x == 0 && z == 0)
			{
				z--;
			}
			else if (x == 0 && z == -1)
			{
				x++;
			}
			else if (x == 1 && z == -1)
			{
				z++;
			}
			else if (x == 1 && z == 0)
			{
				z++;
			}
			else if (x == 1 && z == 1)
			{
				x--;
			}
			else if (x == 0 && z == 1)
			{
				x--;
			}
			else if (x == -1 && z == 1)
			{
				z--;
			}
			else if (x == -1 && z == 0)
			{
				z--;
			}
			else if (x == -1 && z == -1)
			{
				x++;
			}

			// See if that block is the specified block.
			if (kek == block)
			{

				result = true;
				pos = 9;
			}
		}

		return result;
	}

	// Returns true if the block is within two blocks of the player,
	// horizontally.
	public static boolean isBlockNearPlayer2(int posX, int posY, int posZ, Block block, Entity player,
			boolean throughBlock)
	{

		// Basic variables.
		World world = player.world;
		Block air = Block.getBlockFromName("minecraft:air");
		boolean result = false;
		boolean noWall = true;
		int x = 0;
		int z = 0;

		for (int pos = 0; pos < 17; pos++)
		{

			// These are the block positions; kek is the one being looped
			// through; the others are where a wall could be, blocking heat.
			Block kek = world.getBlockState(new BlockPos(posX + x, posY, posZ + z)).getBlock();
			Block blockingNorth = world.getBlockState(new BlockPos(posX + x, posY, posZ + z + 1)).getBlock();
			Block blockingEast = world.getBlockState(new BlockPos(posX + x - 1, posY, posZ + z)).getBlock();
			Block blockingSouth = world.getBlockState(new BlockPos(posX + x, posY, posZ + z - 1)).getBlock();
			Block blockingWest = world.getBlockState(new BlockPos(posX + x + 1, posY, posZ + z)).getBlock();
			Block blockingNortheast = world.getBlockState(new BlockPos(posX + x - 1, posY, posZ + z + 1)).getBlock();
			Block blockingSoutheast = world.getBlockState(new BlockPos(posX + x - 1, posY, posZ + z - 1)).getBlock();
			Block blockingSouthwest = world.getBlockState(new BlockPos(posX + x + 1, posY, posZ + z - 1)).getBlock();
			Block blockingNorthwest = world.getBlockState(new BlockPos(posX + x + 1, posY, posZ + z + 1)).getBlock();

			if (x == 0 && z == 0)
			{
				z = -2;
			}
			else if (x == 0 && z == -2)
			{
				x++;
				if (blockingNorth != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 1 && z == -2)
			{
				x++;
				if (blockingNorth != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 2 && z == -2)
			{
				z++;
				if (blockingNortheast != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 2 && z == -1)
			{
				z++;
				if (blockingEast != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 2 && z == 0)
			{
				z++;
				if (blockingEast != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 2 && z == 1)
			{
				z++;
				if (blockingEast != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 2 && z == 2)
			{
				x--;
				if (blockingSoutheast != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 1 && z == 2)
			{
				x--;
				if (blockingSouth != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 0 && z == 2)
			{
				x--;
				if (blockingSouth != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -1 && z == 2)
			{
				x--;
				if (blockingSouth != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -2 && z == 2)
			{
				z--;
				if (blockingSouthwest != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -2 && z == 1)
			{
				z--;
				if (blockingWest != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -2 && z == 0)
			{
				z--;
				if (blockingWest != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -2 && z == -1)
			{
				z--;
				if (blockingWest != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -2 && z == -2)
			{
				x++;
				if (blockingNorthwest != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -1 && z == -2)
			{
				x++;
				if (blockingNorth != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}

			// See if that block is the specified block.
			if (kek == block && noWall)
			{

				result = true;
				pos = 17;
			}
		}

		return result;
	}

	/*
	 * ============================================= UNDER THE PLAYER
	 * ============================================
	 */

	// Returns true if the block is right UNDER the player,
	// horizontally/diagonally. One block radius.
	public static boolean isBlockUnderPlayer(int posX, int posY, int posZ, Block block, Entity player)
	{

		// Basic variables.
		World world = player.world;
		boolean result = false;
		int x = 0;
		int z = 0;

		// Just iterate through all of the possible positions around the player!
		// CODING!
		// I've redone this four times already, and this one is more
		// straightforward and NOT AS tedious.
		for (int pos = 0; pos < 9; pos++)
		{

			// These are the block positions; kek is the one being looped
			// through.
			Block kek = world.getBlockState(new BlockPos(posX + x, posY - 1, posZ + z)).getBlock();

			if (x == 0 && z == 0)
			{
				z--;
			}
			else if (x == 0 && z == -1)
			{
				x++;
			}
			else if (x == 1 && z == -1)
			{
				z++;
			}
			else if (x == 1 && z == 0)
			{
				z++;
			}
			else if (x == 1 && z == 1)
			{
				x--;
			}
			else if (x == 0 && z == 1)
			{
				x--;
			}
			else if (x == -1 && z == 1)
			{
				z--;
			}
			else if (x == -1 && z == 0)
			{
				z--;
			}
			else if (x == -1 && z == -1)
			{
				x++;
			}

			// See if that block is the specified block.
			if (kek == block)
			{

				result = true;
				pos = 9;
			}
		}

		return result;
	}

	// Returns true if the block is within two blocks of the player,
	// horizontally.
	public static boolean isBlockUnderPlayer2(int posX, int posY, int posZ, Block block, Entity player,
			boolean throughBlock)
	{

		// Basic variables.
		World world = player.world;
		Block air = Block.getBlockFromName("minecraft:air");
		boolean result = false;
		boolean noWall = true;
		int x = 0;
		int z = 0;

		for (int pos = 0; pos < 17; pos++)
		{

			// These are the block positions; kek is the one being looped
			// through; the others are where a wall could be, blocking heat.
			Block kek = world.getBlockState(new BlockPos(posX + x, posY - 1, posZ + z)).getBlock();
			Block blockingNorth = world.getBlockState(new BlockPos(posX + x, posY, posZ + z + 1)).getBlock();
			Block blockingEast = world.getBlockState(new BlockPos(posX + x - 1, posY, posZ + z)).getBlock();
			Block blockingSouth = world.getBlockState(new BlockPos(posX + x, posY, posZ + z - 1)).getBlock();
			Block blockingWest = world.getBlockState(new BlockPos(posX + x + 1, posY, posZ + z)).getBlock();
			Block blockingNortheast = world.getBlockState(new BlockPos(posX + x - 1, posY, posZ + z + 1)).getBlock();
			Block blockingSoutheast = world.getBlockState(new BlockPos(posX + x - 1, posY, posZ + z - 1)).getBlock();
			Block blockingSouthwest = world.getBlockState(new BlockPos(posX + x + 1, posY, posZ + z - 1)).getBlock();
			Block blockingNorthwest = world.getBlockState(new BlockPos(posX + x + 1, posY, posZ + z + 1)).getBlock();
			Block blockingNorthDown = world.getBlockState(new BlockPos(posX + x, posY - 1, posZ + z + 1)).getBlock();
			Block blockingEastDown = world.getBlockState(new BlockPos(posX + x - 1, posY - 1, posZ + z)).getBlock();
			Block blockingSouthDown = world.getBlockState(new BlockPos(posX + x, posY - 1, posZ + z - 1)).getBlock();
			Block blockingWestDown = world.getBlockState(new BlockPos(posX + x + 1, posY - 1, posZ + z)).getBlock();
			Block blockingNortheastDown = world.getBlockState(new BlockPos(posX + x - 1, posY - 1, posZ + z + 1))
					.getBlock();
			Block blockingSoutheastDown = world.getBlockState(new BlockPos(posX + x - 1, posY - 1, posZ + z - 1))
					.getBlock();
			Block blockingSouthwestDown = world.getBlockState(new BlockPos(posX + x + 1, posY - 1, posZ + z - 1))
					.getBlock();
			Block blockingNorthwestDown = world.getBlockState(new BlockPos(posX + x + 1, posY - 1, posZ + z + 1))
					.getBlock();

			if (x == 0 && z == 0)
			{
				z = -2;
			}
			else if (x == 0 && z == -2)
			{
				x++;
				if (blockingNorth != air && !throughBlock && blockingNorthDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 1 && z == -2)
			{
				x++;
				if (blockingNorth != air && !throughBlock && blockingNorthDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 2 && z == -2)
			{
				z++;
				if (blockingNortheast != air && !throughBlock && blockingNortheastDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 2 && z == -1)
			{
				z++;
				if (blockingEast != air && !throughBlock && blockingEastDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 2 && z == 0)
			{
				z++;
				if (blockingEast != air && !throughBlock && blockingEastDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 2 && z == 1)
			{
				z++;
				if (blockingEast != air && !throughBlock && blockingEastDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 2 && z == 2)
			{
				x--;
				if (blockingSoutheast != air && !throughBlock && blockingSoutheastDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 1 && z == 2)
			{
				x--;
				if (blockingSouth != air && !throughBlock && blockingSouthDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 0 && z == 2)
			{
				x--;
				if (blockingSouth != air && !throughBlock && blockingSouthDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -1 && z == 2)
			{
				x--;
				if (blockingSouth != air && !throughBlock && blockingSouthDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -2 && z == 2)
			{
				z--;
				if (blockingSouthwest != air && !throughBlock && blockingSouthwestDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -2 && z == 1)
			{
				z--;
				if (blockingWest != air && !throughBlock && blockingWestDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -2 && z == 0)
			{
				z--;
				if (blockingWest != air && !throughBlock && blockingWestDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -2 && z == -1)
			{
				z--;
				if (blockingWest != air && !throughBlock && blockingWestDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -2 && z == -2)
			{
				x++;
				if (blockingNorthwest != air && !throughBlock && blockingNorthwestDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -1 && z == -2)
			{
				x++;
				if (blockingNorth != air && !throughBlock && blockingNorthDown != air)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}

			// See if that block is the specified block.
			if (kek == block && noWall)
			{

				result = true;
				pos = 17;
			}
		}

		return result;
	}

	/*
	 * ============================================= SAME Y-LEVEL AS PLAYER'S
	 * FACE ============================================
	 */

	// Returns true if the block is right at the player's face,
	// horizontally/diagonally. One block radius.
	public static boolean isBlockAtPlayerFace(int posX, int posY, int posZ, Block block, Entity player)
	{

		// Basic variables
		World world = player.world;
		boolean result = false;
		int x = 0;
		int z = 0;

		// Just iterate through all of the possible positions around the player!
		// CODING!
		// I've redone this four times already, and this one is more
		// straightforward and NOT AS tedious.
		for (int pos = 0; pos < 9; pos++)
		{

			// These are the block positions; kek is the one being looped
			// through
			Block kek = world.getBlockState(new BlockPos(posX + x, posY + 1, posZ + z)).getBlock();

			if (x == 0 && z == 0)
			{
				z--;
			}
			else if (x == 0 && z == -1)
			{
				x++;
			}
			else if (x == 1 && z == -1)
			{
				z++;
			}
			else if (x == 1 && z == 0)
			{
				z++;
			}
			else if (x == 1 && z == 1)
			{
				x--;
			}
			else if (x == 0 && z == 1)
			{
				x--;
			}
			else if (x == -1 && z == 1)
			{
				z--;
			}
			else if (x == -1 && z == 0)
			{
				z--;
			}
			else if (x == -1 && z == -1)
			{
				x++;
			}

			// See if that block is the specified block
			if (kek == block)
			{

				result = true;
				pos = 9;
			}
		}

		return result;
	}

	// Returns true if the block is right at the player's face,
	// horizontally/diagonally. Two block radius.
	public static boolean isBlockAtPlayerFace2(int posX, int posY, int posZ, Block block, Entity player,
			boolean throughBlock)
	{

		// Basic variables
		World world = player.world;
		Block air = Block.getBlockFromName("minecraft:air");
		boolean result = false;
		boolean noWall = true;
		int x = 0;
		int z = 0;

		for (int pos = 0; pos < 17; pos++)
		{

			// These are the block positions; kek is the one being looped
			// through; the others are where a wall could be, blocking heat.
			Block kek = world.getBlockState(new BlockPos(posX + x, posY + 1, posZ + z)).getBlock();
			Block blockingNorth = world.getBlockState(new BlockPos(posX + x, posY + 1, posZ + z + 1)).getBlock();
			Block blockingEast = world.getBlockState(new BlockPos(posX + x - 1, posY + 1, posZ + z)).getBlock();
			Block blockingSouth = world.getBlockState(new BlockPos(posX + x, posY + 1, posZ + z - 1)).getBlock();
			Block blockingWest = world.getBlockState(new BlockPos(posX + x + 1, posY + 1, posZ + z)).getBlock();
			Block blockingNortheast = world.getBlockState(new BlockPos(posX + x - 1, posY + 1, posZ + z + 1))
					.getBlock();
			Block blockingSoutheast = world.getBlockState(new BlockPos(posX + x - 1, posY + 1, posZ + z - 1))
					.getBlock();
			Block blockingSouthwest = world.getBlockState(new BlockPos(posX + x + 1, posY + 1, posZ + z - 1))
					.getBlock();
			Block blockingNorthwest = world.getBlockState(new BlockPos(posX + x + 1, posY + 1, posZ + z + 1))
					.getBlock();

			if (x == 0 && z == 0)
			{
				z = -2;
			}
			else if (x == 0 && z == -2)
			{
				x++;
				if (blockingNorth != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 1 && z == -2)
			{
				x++;
				if (blockingNorth != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 2 && z == -2)
			{
				z++;
				if (blockingNortheast != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 2 && z == -1)
			{
				z++;
				if (blockingEast != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 2 && z == 0)
			{
				z++;
				if (blockingEast != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 2 && z == 1)
			{
				z++;
				if (blockingEast != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 2 && z == 2)
			{
				x--;
				if (blockingSoutheast != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 1 && z == 2)
			{
				x--;
				if (blockingSouth != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == 0 && z == 2)
			{
				x--;
				if (blockingSouth != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -1 && z == 2)
			{
				x--;
				if (blockingSouth != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -2 && z == 2)
			{
				z--;
				if (blockingSouthwest != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -2 && z == 1)
			{
				z--;
				if (blockingWest != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -2 && z == 0)
			{
				z--;
				if (blockingWest != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -2 && z == -1)
			{
				z--;
				if (blockingWest != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -2 && z == -2)
			{
				x++;
				if (blockingNorthwest != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}
			else if (x == -1 && z == -2)
			{
				x++;
				if (blockingNorth != air && !throughBlock)
				{
					noWall = false;
				}
				else
				{
					noWall = true;
				}
			}

			// See if that block is the specified block.
			if (kek == block && noWall)
			{

				result = true;
				pos = 17;
			}
		}

		return result;
	}
}