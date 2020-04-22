package enginecrafter77.survivalinc.season;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class SeasonCommand extends CommandBase {

	public SeasonCommand()
	{
		System.out.println("Registering command season");
	}
	
	@Override
	public String getName()
	{
		return "season";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/season <get|set|day> [season|day]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		World world = sender.getEntityWorld();
		SeasonData data = SeasonData.load(world);
		
		if(args.length < 1) throw new CommandException("Insufficient arguments\n" + this.getUsage(sender));
		
		switch(args[0])
		{
		case "get":
			sender.sendMessage(new TextComponentString("Current season: " + data.toString()));
			break;
		case "set":
			if(args.length < 2) throw new CommandException("Missing season name\n" + this.getUsage(sender));
			data.season = Season.valueOf(args[1]);
			sender.sendMessage(new TextComponentString("Set season to " + data.season.name()));
			break;
		case "day":
			if(args.length < 2) throw new CommandException("Missing day parameter\n" + this.getUsage(sender));
			data.day = Integer.parseInt(args[1]);
			sender.sendMessage(new TextComponentString("Set day in the current season to " + data.day));
			break;
		}
		
		if(args[0] != "get")
		{
			MinecraftForge.EVENT_BUS.post(new SeasonUpdateEvent(world, data));
			data.markDirty();
		}
	}
	
}
