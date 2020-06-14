package enginecrafter77.survivalinc.stats;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.net.StatSyncMessage;
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
		return "stat";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/stat <player> [list|<get|set> [stat] [value]]";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{		
		if(args.length < 1) throw new CommandException("Insufficient Arguments\nUsage: " + this.getUsage(sender));
		
		EntityPlayer player = CommandBase.getPlayer(server, sender, args[0]);
		StatTracker tracker = player.getCapability(StatCapability.target, null);
		
		if(args.length < 2 || args[1].equals("list"))
		{
			StringBuilder builder = new StringBuilder();
			builder.append(player.getDisplayNameString() + "'s stats:");
			for(StatProvider provider : tracker.getRegisteredProviders())
			{
				builder.append(String.format("\n \u00A7a%s\u00A7r: %s", provider.getStatID(), tracker.getRecord(provider).toString()));
			}
			sender.sendMessage(new TextComponentString(builder.toString()));
		}
		else
		{
			if(args.length < 3) throw new CommandException("Insufficient Arguments\nUsage: " + this.getUsage(sender));
			
			StatProvider provider = tracker.getProvider(args[2]);
			if(provider == null) throw new CommandException("Stat " + args[2] + " does not exist!");
			
			if(args[1].equals("set"))
			{
				if(args.length < 4) throw new CommandException("Insufficient Arguments\nUsage: " + this.getUsage(sender));
				tracker.setStat(provider, Float.parseFloat(args[3]));
				SurvivalInc.proxy.net.sendTo(new StatSyncMessage(tracker), (EntityPlayerMP)player);
			}
			else sender.sendMessage(new TextComponentString(provider.getStatID() + ": " + tracker.getStat(provider)));
		}
	}
	
}
