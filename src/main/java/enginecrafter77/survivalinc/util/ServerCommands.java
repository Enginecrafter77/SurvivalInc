package enginecrafter77.survivalinc.util;

import enginecrafter77.survivalinc.config.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

/*
 * These commands are all run when the server is starting. Usually just messing with the properties a bit.
 */
public class ServerCommands {

	// They share the same variables, and they're pretty minor, so may as well
	// out them in the same method.
	// This method is called once when the server has started. No need for
	// !world.isRemote check here.
	public static void fireCommandsOnStartup()
	{
		// Instance of the server.
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

		// Instance of the server world.
		World world = server.getEntityWorld();

		// GameRules
		GameRules gamerules = world.getGameRules();
		
		// If keepInventory is on, turn it off. Dying should suck more.
		gamerules.setOrCreateGameRule("keepInventory", "false");
		
		// This is for seasons. Make sure the time stays there until a player joins.
		gamerules.setOrCreateGameRule("doDaylightCycle", "false");
		
		// This sets the WORLD spawn point to 0,0. So having coordinates disabled isn't TOO bad.
		world.setSpawnPoint(BlockPos.ORIGIN);
	}

	// This method is called constantly (per tick) in TweakEvents class.
	public static void fireCommandsEveryTick(EntityPlayer player)
	{
		// Server-side.
		if (!player.world.isRemote)
		{

			// Coords of player.
			BlockPos pos = player.getPosition();

			// Set their spawnpoint to those coordinates.
			if (!player.isDead && ModConfig.MECHANICS.enableGhost)
			{

				player.setSpawnPoint(pos, true);
			}
		}
	}
}