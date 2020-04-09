package enginecrafter77.survivalinc.ghost;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class GhostCommand extends CommandBase {

	@Override
	public String getName()
	{
		return "ghost";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/ghost [player] [on|off]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayer player;
		
		if(args.length >= 1) player = CommandBase.getPlayer(server, sender, args[0]);
		else player = (EntityPlayer)sender;
		
		IGhost ghost = player.getCapability(GhostProvider.GHOST_CAP, null);
		boolean status = !ghost.status();
		
		if(args.length >= 2) status = args[1].equals("on");
		
		if(status == ghost.status()) return;
		
		if(status)
			ghost.create();
		else
			ghost.resurrect();
	}

}
