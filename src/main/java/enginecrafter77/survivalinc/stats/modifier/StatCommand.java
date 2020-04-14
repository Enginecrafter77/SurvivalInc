package enginecrafter77.survivalinc.stats.modifier;

import enginecrafter77.survivalinc.stats.StatProvider;
import enginecrafter77.survivalinc.stats.StatRegister;
import enginecrafter77.survivalinc.stats.StatTracker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

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
		World world = sender.getEntityWorld();
		
		if(args.length < 2) throw new CommandException("Insufficient Arguments\nUsage: " + this.getUsage(sender));
		
		EntityPlayer player = world.getPlayerEntityByName(args[0]);
		StatTracker tracker = player.getCapability(StatRegister.CAPABILITY, null);
		
		switch(args[1])
		{
		case "list":
			sender.sendMessage(new TextComponentString("Registered stats: " + tracker.getRegisteredProviders().toString()));
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
			break;
		default:
			return;
		}
	}
	
}
