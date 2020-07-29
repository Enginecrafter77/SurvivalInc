package enginecrafter77.survivalinc.ghost;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import enginecrafter77.survivalinc.stats.StatCapability;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

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
		if(args.length < 1) throw new CommandException("Usage: /ghost <player> [on|off]");
		
		EntityPlayer player = CommandBase.getPlayer(server, sender, args[0]);
		
		StatTracker tracker = player.getCapability(StatCapability.target, null);
		GhostEnergyRecord record = (GhostEnergyRecord)tracker.getRecord(GhostProvider.instance);
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
		SurvivalInc.proxy.net.sendToAll(new StatSyncMessage(player));
		
		sender.sendMessage(new TextComponentString(String.format("Transformed %s into %s", player.getName(), status ? "ghost" : "human")));
	}

}
