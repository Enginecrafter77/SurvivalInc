package enginecrafter77.survivalinc.stats;

import java.util.HashSet;
import java.util.Set;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.net.StatUpdateMessage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * Command used to control the stats for a player.
 * @author Enginecrafter77
 */
public class StatCommand extends CommandBase {
	
	@Override
	public String getName()
	{
		return "playerstat";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/playerstat <player> <get|set|list> [stat] [value]";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{		
		if(args.length < 2) throw new CommandException("Insufficient Arguments\nUsage: " + this.getUsage(sender));
		
		EntityPlayer player = CommandBase.getPlayer(server, sender, args[0]);
		StatTracker tracker = player.getCapability(StatCapability.target, null);
		
		switch(args[1])
		{
		case "list":
			Set<String> providers = new HashSet<String>();
			for(StatProvider provider : tracker.getRegisteredProviders())
				providers.add(provider.getStatID());
			sender.sendMessage(new TextComponentString("Registered stats: " + providers.toString()));
			break;
		case "get":
		case "set":
			if(args.length < 3) throw new CommandException("Insufficient Arguments\nUsage: " + this.getUsage(sender));
			StatProvider provider = tracker.getProvider(args[2]);
			if(provider == null) throw new CommandException("Stat " + args[2] + " does not exist!");
			if(args[1].equals("set"))
			{
				if(args.length < 4) throw new CommandException("Insufficient Arguments\nUsage: " + this.getUsage(sender));
				tracker.setStat(provider, Float.parseFloat(args[3]));
			}
			else sender.sendMessage(new TextComponentString(provider.getStatID() + ": " + tracker.getStat(provider)));
			SurvivalInc.proxy.net.sendTo(new StatUpdateMessage(tracker), (EntityPlayerMP)player);
			break;
		default:
			return;
		}
	}
	
}
