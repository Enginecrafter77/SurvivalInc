package net.schoperation.schopcraft.tweak;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

/*
 * These commands are all run when the server is starting. Usually just messing with the properties a bit.
 */
public class ServerCommands {
		
	
	// They share the same variables, and they're pretty minor, so may as well out them in the same method.
	public static void fireAllCommands() {
		
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
		 * If keepInventory is on, turn it off. Dying should suck. It'll suck more in the future. Or be cool.
		 */
		gamerules.setOrCreateGameRule("keepInventory", "false");
		
		/*
		 * This sets the difficulty to hard. May as well.
		 */
		server.setDifficultyForAllWorlds(EnumDifficulty.HARD);
		
	}
}
