package enginecrafter77.survivalinc.stats;

import enginecrafter77.survivalinc.SurvivalInc;
import enginecrafter77.survivalinc.net.StatSyncMessage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

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
			ITextComponent component = new TextComponentString(player.getDisplayNameString() + "'s stats:");
			for(StatProvider provider : tracker.getRegisteredProviders())
			{
				component.appendText("\n");
				component.appendSibling(new TextComponentTranslation(String.format("%s stat.%s.name", TextFormatting.GREEN, provider.getStatID().toString())));
				component.appendText("(" + TextFormatting.YELLOW + provider.getStatID().toString() + TextFormatting.RESET + ")");
				component.appendText(": " + tracker.getStat(provider));
			}
			sender.sendMessage(component);
		}
		else
		{
			if(args.length < 3) throw new CommandException("Insufficient Arguments\nUsage: " + this.getUsage(sender));
			
			ResourceLocation res = new ResourceLocation(args[2]);
			StatProvider provider = tracker.getProvider(res);
			if(provider == null) throw new CommandException("Stat " + res.toString() + " does not exist!");
			
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
