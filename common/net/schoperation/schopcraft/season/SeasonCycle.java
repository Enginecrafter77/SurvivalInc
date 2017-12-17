package net.schoperation.schopcraft.season;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.schoperation.schopcraft.SchopCraft;

public class SeasonCycle {
	
	/*
	 * Everything to do with the day and night cycle, whether it be toggling it or modifying it as the seasons go on.
	 */
	
	public static void toggleCycle(boolean enable) {
		
		// Server
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		// Server world
		World world = server.getEntityWorld();
		
		// Gamerules
		GameRules gamerules = world.getGameRules();
		
		// Toggle it.
		if (enable) {
			
			gamerules.setOrCreateGameRule("doDaylightCycle", "true");
			SchopCraft.logger.info("Day-night cycle enabled.");
		}
		
		else {
			
			gamerules.setOrCreateGameRule("doDaylightCycle", "false");
			SchopCraft.logger.info("Day-night cycle disabled.");
		}
	}
}