package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

public class GhostCommand extends CommandBase {

	@Override
	public String getName()
	{
		return "ghost";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/ghost [on|off] [player]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length < 1) throw new WrongUsageException("Usage: /ghost <player> [on|off]");
		
		List<EntityPlayerMP> subjects = CommandBase.getPlayers(server, sender, args[0]);
		StatSyncMessage message = new StatSyncMessage();
		
		for(EntityPlayerMP player : subjects)
		{
			StatTracker tracker = player.getCapability(StatCapability.target, null);
			GhostEnergyRecord record = tracker.getRecord(SurvivalInc.ghost);
			boolean status = !record.isActive();
			
			if(args.length >= 2)
			{
				switch(args[1])
				{
				case "on":
					status = true;
					break;
				case "off":
					status = false;
					break;
				default:
					throw new CommandException("Unknown state \"" + args[1] + "\"");
				}
			}
			
			record.setActive(status);
			message.addPlayer(player);
			sender.sendMessage(new TextComponentString(String.format("Transformed %s into %s", player.getName(), status ? "ghost" : "human")));
		}
		SurvivalInc.proxy.net.sendToAll(message);
	}

}
