package enginecrafter77.survivalinc.ghost;

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
		EntityPlayer player;
		
		if(args.length >= 2) player = CommandBase.getPlayer(server, sender, args[1]);
		else player = (EntityPlayer)sender;
		
		Ghost ghost = player.getCapability(GhostProvider.target, null);
		boolean status = args.length >= 1 ? args[0].equals("on") : !ghost.getStatus();
		
		ghost.setStatus(status);
		ghost.applyStatus(player, status);
		
		sender.sendMessage(new TextComponentString(String.format("Transformed %s into %s", player.getName(), status ? "ghost" : "human")));
	}

}
