package net.schoperation.schopcraft.tweak;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

/*
 * These commands are all run when the server is starting. Usually just messing with the properties a bit.
 */
public class ServerCommands {
		
	
	// They share the same variables, and they're pretty minor, so may as well out them in the same method.
	// This method is called once when the server has started.
	public static void fireCommandsOnStartup() {
		
		// Instance of the server.
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		// Instance of the server world.
		World world = server.getEntityWorld();
		
		// GameRules
		GameRules gamerules = world.getGameRules();
		
		/*
		 * This enables ReducedDebugInfo automatically on server startup. This disables coordinates on the F3 debug screen.
		 * The coordinate system definitely helps with finding our way around. Now we'll need to depend on landmarks, maps, and compasses for once.
		 * This can be cheated pretty easily, but hey, we all make choices, including those that avoid pushing ourselves to greater limits.
		 */
		gamerules.setOrCreateGameRule("reducedDebugInfo", "true");
		
		/*
		 * If keepInventory is on, turn it off. Dying should suck.
		 */
		gamerules.setOrCreateGameRule("keepInventory", "false");
		
		/*
		 * This sets the difficulty to hard. May as well.
		 */
		server.setDifficultyForAllWorlds(EnumDifficulty.HARD);
		
		/*
		 * This sets the WORLD spawnpoint to 0,0. So having coordinates disabled isn't TOO bad.
		 */
		world.setSpawnPoint(BlockPos.ORIGIN);
		
	}
	
	// This method is called constantly (per tick) in TweakEvents class.
	public static void fireCommandsEveryTick() {
		
		// Instance of the server.
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		// List of players on the server.
		String[] playerList = server.getOnlinePlayerNames();
		
		// Number of players on the server.
		int numPlayers = server.getCurrentPlayerCount();
		
		/*
		 * Set the spawnpoint of every player to their current positions. That way, if they were to die,
		 * ...they'd respawn as a ghost ON their items.
		 */
		
		for (int num = 0; num < numPlayers; num++) {
			
			// The chosen player
			EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(playerList[0]);
			
			// Coords of player.
			BlockPos pos = player.getPosition();
			
			// Set their spawnpoint to those coordinates.
			if (!player.isDead) {
				
				player.setSpawnPoint(pos, true);
			}
		}
	}
}
